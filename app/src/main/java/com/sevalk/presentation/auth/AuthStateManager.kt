package com.sevalk.presentation.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

enum class AuthState {
    LOADING,
    FIRST_TIME_USER,
    AUTHENTICATED,
    UNAUTHENTICATED
}

@Singleton
class AuthStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    var authState by mutableStateOf(AuthState.LOADING)
        private set

    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    var isProviderMode by mutableStateOf(false)
        private set

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    init {
        // Listen for auth state changes
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            currentUser = user
            
            if (user != null) {
                Timber.d("User authenticated: ${user.uid}")
                checkUserTypeAndNavigate(user)
            } else {
                Timber.d("User not authenticated")
                checkIfFirstTimeUser()
            }
        }
    }

    fun checkInitialAuthState() {
        val user = firebaseAuth.currentUser
        currentUser = user
        
        if (user != null) {
            Timber.d("User already authenticated: ${user.uid}")
            checkUserTypeAndNavigate(user)
        } else {
            Timber.d("No authenticated user")
            checkIfFirstTimeUser()
        }
    }

    private fun checkIfFirstTimeUser() {
        val isFirstRun = sharedPreferences.getBoolean("is_first_run", true)
        
        if (isFirstRun) {
            Timber.d("First time user - showing onboarding")
            authState = AuthState.FIRST_TIME_USER
        } else {
            Timber.d("Returning user - showing login")
            authState = AuthState.UNAUTHENTICATED
        }
    }

    private fun checkUserTypeAndNavigate(user: FirebaseUser) {
        // Check if user is a service provider
        firestore.collection(Constants.COLLECTION_USERS)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userType = document.getString("userType")
                    isProviderMode = userType == Constants.USER_TYPE_PROVIDER
                    Timber.d("User type determined: $userType")
                } else {
                    // User document doesn't exist, might be a new user
                    isProviderMode = false
                    Timber.d("User document not found, defaulting to customer mode")
                }
                authState = AuthState.AUTHENTICATED
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Failed to get user type")
                // Default to customer mode on error
                isProviderMode = false
                authState = AuthState.AUTHENTICATED
            }
    }

    suspend fun checkUserTypeAsync(user: FirebaseUser): Boolean {
        return try {
            val document = firestore.collection(Constants.COLLECTION_USERS)
                .document(user.uid)
                .get()
                .await()
            
            if (document.exists()) {
                val userType = document.getString("userType")
                isProviderMode = userType == Constants.USER_TYPE_PROVIDER
                Timber.d("User type determined: $userType")
                isProviderMode
            } else {
                isProviderMode = false
                Timber.d("User document not found, defaulting to customer mode")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get user type")
            isProviderMode = false
            false
        }
    }

    fun markOnboardingCompleted() {
        sharedPreferences.edit().putBoolean("is_first_run", false).apply()
        Timber.d("Onboarding completed, marked in preferences")
    }

    fun signOut() {
        firebaseAuth.signOut()
        isProviderMode = false
        authState = AuthState.UNAUTHENTICATED
        Timber.d("User signed out")
    }

    fun updateProviderMode(isProvider: Boolean) {
        isProviderMode = isProvider
        Timber.d("Provider mode updated: $isProvider")
    }

    fun refreshAuthState() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            checkUserTypeAndNavigate(user)
        } else {
            checkIfFirstTimeUser()
        }
    }
}
