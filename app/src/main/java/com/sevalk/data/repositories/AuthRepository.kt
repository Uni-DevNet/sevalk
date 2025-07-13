package com.sevalk.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.models.ProviderStatus
import com.sevalk.data.models.Service
import com.sevalk.data.models.ServiceLocation
import com.sevalk.data.models.ServiceProvider
import com.sevalk.data.models.User
import com.sevalk.data.models.UserType
import com.sevalk.utils.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Properties
import javax.inject.Inject
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    suspend fun sendVerificationCode(email: String, fullName: String): String
    suspend fun verifyCode(email: String, code: String): Boolean
    suspend fun registerUser(email: String, password: String, fullName: String, userType: UserType): Result<FirebaseUser>
    suspend fun createGoogleUser(email: String, fullName: String, userType: UserType): Result<FirebaseUser>
    suspend fun createGoogleServiceProvider(email: String, fullName: String, userType: UserType): Result<FirebaseUser>
    suspend fun getCurrentUserId(): String?
    suspend fun getUserData(userId: String): Result<User>
    suspend fun updateUserData(user: User): Result<Unit>
    suspend fun logout()
    suspend fun registerServiceProvider(email: String, password: String, fullName: String, userType: UserType): Result<FirebaseUser>
    suspend fun updateServiceProviderServices(providerId: String, services: List<Service>): Result<Unit>
    suspend fun updateServiceProviderLocation(providerId: String, serviceLocation: ServiceLocation, serviceRadius: Double): Result<Unit>
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    
    private val verificationCodes = mutableMapOf<String, String>()
    
    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.failure(Exception("Authentication failed"))
            
            Timber.d("User logged in successfully: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Login failed for email: $email")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: return Result.failure(Exception("Google authentication failed"))
            
            Timber.d("User logged in with Google successfully: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Google sign-in failed")
            Result.failure(e)
        }
    }

    override suspend fun sendVerificationCode(email: String, fullName: String): String {
        val code = generateVerificationCode()
        verificationCodes[email] = code
        
        try {
            sendVerificationEmail(email, fullName, code)
            return code
        } catch (e: Exception) {
            Timber.e(e, "Failed to send verification email")
            throw e
        }
    }
    
    override suspend fun verifyCode(email: String, code: String): Boolean {
        val storedCode = verificationCodes[email]
        return if (storedCode == code) {
            true
        } else {
            false
        }
    }
    
    override suspend fun registerUser(
        email: String, 
        password: String, 
        fullName: String, 
        userType: UserType
    ): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Result.failure(Exception("Failed to get user ID"))
            
            // Create User object
            val user = User(
                id = userId,
                email = email,
                displayName = fullName,
                userType = userType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // Convert User object to Map using companion function
            val userData = User.toMap(user)
            
            // Save to Firestore
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(userData)
                .await()
            
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Timber.e(e, "Registration failed")
            Result.failure(e)
        }
    }
    
    override suspend fun registerServiceProvider(
        email: String, 
        password: String, 
        fullName: String, 
        userType: UserType
    ): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Result.failure(Exception("Failed to get user ID"))
            
            // Create User object for customers collection
            val user = User(
                id = userId,
                email = email,
                displayName = fullName,
                userType = userType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // Create ServiceProvider object for service providers collection
            val serviceProvider = ServiceProvider(
                id = userId,
                userId = userId,
                businessName = fullName, // Initially set to full name, can be updated later
                description = "",
                status = ProviderStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // Convert objects to Maps using companion functions
            val userData = User.toMap(user)
            val serviceProviderData = ServiceProvider.toMap(serviceProvider)
            
            // Save User to customers collection
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(userData)
                .await()
            
            // Save ServiceProvider to service providers collection
            firestore.collection(Constants.COLLECTION_SERVICE_PROVIDERS)
                .document(userId)
                .set(serviceProviderData)
                .await()
            
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Timber.e(e, "Service provider registration failed")
            Result.failure(e)
        }
    }
    
    override suspend fun createGoogleUser(
        email: String, 
        fullName: String, 
        userType: UserType
    ): Result<FirebaseUser> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("No authenticated user"))
            val userId = currentUser.uid
            
            // Create User object
            val user = User(
                id = userId,
                email = email,
                displayName = fullName,
                userType = userType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // Convert User object to Map using companion function
            val userData = User.toMap(user)
            
            // Save to Firestore
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(userData)
                .await()
            
            Result.success(currentUser)
        } catch (e: Exception) {
            Timber.e(e, "Google user creation failed")
            Result.failure(e)
        }
    }
    
    override suspend fun createGoogleServiceProvider(
        email: String, 
        fullName: String, 
        userType: UserType
    ): Result<FirebaseUser> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("No authenticated user"))
            val userId = currentUser.uid
            
            // Create User object for customers collection
            val user = User(
                id = userId,
                email = email,
                displayName = fullName,
                userType = userType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // Create ServiceProvider object for service providers collection
            val serviceProvider = ServiceProvider(
                id = userId,
                userId = userId,
                businessName = fullName, // Initially set to full name, can be updated later
                description = "",
                status = ProviderStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // Convert objects to Maps using companion functions
            val userData = User.toMap(user)
            val serviceProviderData = ServiceProvider.toMap(serviceProvider)
            
            // Save User to customers collection
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(userData)
                .await()
            
            // Save ServiceProvider to service providers collection
            firestore.collection(Constants.COLLECTION_SERVICE_PROVIDERS)
                .document(userId)
                .set(serviceProviderData)
                .await()
            
            Result.success(currentUser)
        } catch (e: Exception) {
            Timber.e(e, "Google service provider creation failed")
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    override suspend fun getUserData(userId: String): Result<User> {
        return try {
            val document = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                val userData = document.data
                if (userData != null) {
                    // Convert Map to User object using companion function
                    val user = User.fromMap(userData)
                    if (user != null) {
                        Result.success(user)
                    } else {
                        Result.failure(Exception("Failed to parse user data"))
                    }
                } else {
                    Result.failure(Exception("User data is null"))
                }
            } else {
                Result.failure(Exception("User document not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get user data")
            Result.failure(e)
        }
    }
    
    override suspend fun updateUserData(user: User): Result<Unit> {
        return try {
            val updatedUser = user.copy(updatedAt = System.currentTimeMillis())
            
            // Convert User object to Map using companion function
            val userData = User.toMap(updatedUser)
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.id)
                .set(userData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update user data")
            Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        auth.signOut()
    }
    
    override suspend fun updateServiceProviderServices(
        providerId: String, 
        services: List<Service>
    ): Result<Unit> {
        return try {
            val serviceProviderRef = firestore.collection(Constants.COLLECTION_SERVICE_PROVIDERS)
                .document(providerId)
            
            // Check if service provider document exists
            val document = serviceProviderRef.get().await()
            
            if (document.exists()) {
                // Update existing service provider with services and business name
                val updates = mapOf(
                    "services" to services.map { Service.toMap(it) },
                    "updatedAt" to System.currentTimeMillis()
                )
                
                serviceProviderRef.update(updates).await()
                Timber.d("Service provider services updated successfully for provider: $providerId")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Service provider not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update service provider services")
            Result.failure(e)
        }
    }
    
    override suspend fun updateServiceProviderLocation(
        providerId: String, 
        serviceLocation: ServiceLocation,
        serviceRadius: Double
    ): Result<Unit> {
        return try {
            val serviceProviderRef = firestore.collection(Constants.COLLECTION_SERVICE_PROVIDERS)
                .document(providerId)
            
            // Check if service provider document exists
            val document = serviceProviderRef.get().await()

            // Ensure service radius to whole number
            val intRadius = serviceRadius.toInt()
            
            if (document.exists()) {
                // Update existing service provider with location and radius
                val updates = mapOf(
                    "serviceLocation" to ServiceLocation.toMap(serviceLocation),
                    "serviceRadius" to intRadius,
                    "updatedAt" to System.currentTimeMillis()
                )
                
                serviceProviderRef.update(updates).await()
                Timber.d("Service provider location updated successfully for provider: $providerId")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Service provider not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update service provider location")
            Result.failure(e)
        }
    }
    
    private fun generateVerificationCode(): String {
        return Random.nextInt(100000, 999999).toString()
    }
    
    private suspend fun sendVerificationEmail(email: String, name: String, code: String) {
        return suspendCoroutine { continuation ->
            Thread {
                try {
                    // Log the code for debugging purposes
                    Timber.d("Sending verification code to $email: $code")
                    
                    val props = Properties()
                    props.put("mail.smtp.auth", "true")
                    props.put("mail.smtp.starttls.enable", "true")
                    props.put("mail.smtp.host", "smtp.gmail.com")
                    props.put("mail.smtp.port", "587")
                    
                    // Replace with your email and app password
                    val emailSender = "adoptawallet.devnet@gmail.com"  // TODO: Replace with your Gmail
                    val emailPassword = "nozr nzud dlpn gncs"   // TODO: Replace with your app password
                    
                    val session = Session.getInstance(props, object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(emailSender, emailPassword)
                        }
                    })
                    
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress(emailSender))
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                    message.subject = "SevaLK - Email Verification"
                        
                        val emailContent = """
                            <html>
                            <body style="font-family: Arial, sans-serif; padding: 20px;">
                                <h2>Welcome to SevaLK!</h2>
                                <p>Hello $name,</p>
                                <p>Thank you for registering with SevaLK. Please use the verification code below to complete your registration:</p>
                                <h3 style="background-color: #f2f2f2; padding: 10px; text-align: center; font-size: 24px;">$code</h3>
                                <p>This code will expire in 10 minutes.</p>
                                <p>If you didn't request this code, please ignore this email.</p>
                                <p>Best regards,<br>The SevaLK Team</p>
                            </body>
                            </html>
                        """.trimIndent()
                        
                        message.setContent(emailContent, "text/html; charset=utf-8")
                        
                        Transport.send(message)
                        continuation.resume(Unit)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to send email")
                        continuation.resumeWithException(e)
                    }
                }.start()
            }
        }
    }



