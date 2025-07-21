package com.sevalk.presentation.auth.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sevalk.data.repositories.AuthRepository
import com.sevalk.presentation.auth.AuthStateManager
import com.sevalk.utils.GoogleSignInHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStateManager: AuthStateManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()
    
    private val googleSignInHelper = GoogleSignInHelper(context)

    fun onEmailChange(email: String) {
        _uiState.update { 
            it.copy(
                email = email,
                emailError = null,
                errorMessage = null
            ) 
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { 
            it.copy(
                password = password,
                passwordError = null,
                errorMessage = null
            ) 
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        
        // Clear previous errors
        _uiState.update { 
            it.copy(
                emailError = null,
                passwordError = null,
                errorMessage = null
            ) 
        }
        
        // Validate inputs
        val validationErrors = validateInputs(currentState.email, currentState.password)
        if (validationErrors.isNotEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    emailError = validationErrors["email"],
                    passwordError = validationErrors["password"]
                )
            }
            return
        }
        
        // Perform login
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = authRepository.login(currentState.email, currentState.password)
                
                result.fold(
                    onSuccess = { user ->
                        Timber.d("Login successful for user: ${user.uid}")
                        // Refresh auth state to trigger navigation
                        authStateManager.refreshAuthState()
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = null
                            ) 
                        }
                        onSuccess()
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "Login failed")
                        val errorMessage = getFirebaseErrorMessage(exception)
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = errorMessage
                            ) 
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during login")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "An unexpected error occurred. Please try again."
                    ) 
                }
            }
        }
    }

    fun forgotPassword() {
        // TODO: Implement forgot password functionality
        Timber.d("Forgot password clicked")
    }
    
    fun initiateGoogleSignIn(onSignInIntentReady: (android.content.Intent) -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val signInIntent = googleSignInHelper.getSignInIntent()
                _uiState.update { it.copy(isLoading = false) }
                onSignInIntentReady(signInIntent)
            } catch (e: Exception) {
                Timber.e(e, "Failed to create Google Sign-In intent")
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to start Google Sign-In. Please try again.",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun handleGoogleSignInResult(
        data: android.content.Intent?, 
        onNavigateToUserTypeSelection: (String, String) -> Unit,
        onLoginSuccess: () -> Unit
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = googleSignInHelper.handleSignInResult(task)
                
                if (account != null) {
                    val idToken = account.idToken
                    if (idToken != null) {
                        val result = authRepository.signInWithGoogle(idToken)
                        result.fold(
                            onSuccess = { user ->
                                Timber.d("Google Sign-In successful: ${user.uid}")
                                
                                // Check if user already exists in database
                                val userDataResult = authRepository.getUserData(user.uid)
                                userDataResult.fold(
                                    onSuccess = { userData ->
                                        // User exists, refresh auth state
                                        authStateManager.refreshAuthState()
                                        _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                                        onLoginSuccess()
                                    },
                                    onFailure = {
                                        // User doesn't exist, navigate to user type selection
                                        val email = account.email ?: ""
                                        val name = account.displayName ?: ""
                                        _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                                        onNavigateToUserTypeSelection(email, name)
                                    }
                                )
                            },
                            onFailure = { exception ->
                                Timber.e(exception, "Google authentication failed")
                                _uiState.update { 
                                    it.copy(
                                        errorMessage = "Google authentication failed. Please try again.",
                                        isLoading = false
                                    )
                                }
                            }
                        )
                    } else {
                        Timber.w("Google Sign-In completed but no ID token received")
                        _uiState.update { 
                            it.copy(
                                errorMessage = "Google Sign-In incomplete. Please try again.",
                                isLoading = false
                            )
                        }
                    }
                } else {
                    // This is normal when user cancels, don't log as error
                    Timber.d("Google Sign-In was cancelled by user")
                    _uiState.update { 
                        it.copy(
                            isLoading = false
                            // Don't set error message for cancellation
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during Google Sign-In")
                _uiState.update { 
                    it.copy(
                        errorMessage = "Google Sign-In failed. Please try again.",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun validateInputs(email: String, password: String): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        // Email validation
        if (email.isBlank()) {
            errors["email"] = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors["email"] = "Please enter a valid email address"
        }
        
        // Password validation
        if (password.isBlank()) {
            errors["password"] = "Password is required"
        } else if (password.length < 6) {
            errors["password"] = "Password must be at least 6 characters"
        }
        
        return errors
    }
    
    private fun getFirebaseErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("password is invalid") == true ||
            exception.message?.contains("INVALID_PASSWORD") == true -> 
                "Invalid password. Please check your password and try again."
            
            exception.message?.contains("user not found") == true ||
            exception.message?.contains("USER_NOT_FOUND") == true -> 
                "No account found with this email address."
            
            exception.message?.contains("email address is badly formatted") == true ||
            exception.message?.contains("INVALID_EMAIL") == true -> 
                "Please enter a valid email address."
            
            exception.message?.contains("too many attempts") == true ||
            exception.message?.contains("TOO_MANY_REQUESTS") == true -> 
                "Too many failed attempts. Please try again later."
            
            exception.message?.contains("network error") == true -> 
                "Network error. Please check your connection and try again."
            
            else -> "Login failed. Please check your credentials and try again."
        }
    }
}