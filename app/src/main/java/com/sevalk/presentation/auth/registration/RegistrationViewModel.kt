package com.sevalk.presentation.auth.registration

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sevalk.data.models.UserType
import com.sevalk.data.repositories.AuthRepository
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
open class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationState())
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()
    
    private val googleSignInHelper = GoogleSignInHelper(context)

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.FullNameChanged -> _uiState.update { it.copy(fullName = event.fullName) }
            is RegistrationEvent.EmailChanged -> _uiState.update { it.copy(email = event.email) }
            is RegistrationEvent.PasswordChanged -> _uiState.update { it.copy(password = event.password) }
            is RegistrationEvent.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = event.password) }
            is RegistrationEvent.UserTypeChanged -> _uiState.update { it.copy(userType = event.type) }
            is RegistrationEvent.VerificationCodeChanged -> {
                val newCode = _uiState.value.verificationCode.toMutableList()
                if (event.code.length <= 1) {
                    newCode[event.index] = event.code
                }
                _uiState.update { it.copy(verificationCode = newCode) }
            }
            RegistrationEvent.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            RegistrationEvent.ToggleConfirmPasswordVisibility -> _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            RegistrationEvent.NextStep -> {
                when (_uiState.value.currentStep) {
                    1 -> proceedToVerificationStep()
                    2 -> validateVerificationCode()
                    else -> _uiState.update { it.copy(currentStep = it.currentStep + 1, error = null) }
                }
            }
            RegistrationEvent.PreviousStep -> {
                if (_uiState.value.currentStep > 1) {
                    _uiState.update { it.copy(currentStep = it.currentStep - 1, error = null) }
                }
            }
            RegistrationEvent.SubmitRegistration -> {
                if (_uiState.value.userType == UserType.SERVICE_PROVIDER) {
                    registerServiceProvider(null)
                } else {
                    registerCustomer(null)
                }
            }
            is RegistrationEvent.SubmitServiceProviderRegistration -> {
                registerServiceProvider(event.onNavigateToServiceSelection)
            }
            is RegistrationEvent.SubmitCustomerRegistration -> {
                registerCustomer(event.onNavigateToHome)
            }
            is RegistrationEvent.GoogleSignIn -> {
                handleGoogleSignIn(event.onNavigateToUserTypeSelection)
            }
        }
    }
    
    private fun proceedToVerificationStep() {
        _uiState.update { it.copy(isLoading = true) }
        
        // Basic validation
        if (_uiState.value.fullName.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your full name", isLoading = false) }
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            _uiState.update { it.copy(error = "Please enter a valid email address", isLoading = false) }
            return
        }
        
        // Send verification code
        viewModelScope.launch {
            try {
                val code = authRepository.sendVerificationCode(_uiState.value.email, _uiState.value.fullName)
                
                // Store the verification code in debug UI for testing purposes
                Timber.d("Verification code generated: $code")
                
                _uiState.update { 
                    it.copy(
                        currentStep = it.currentStep + 1,
                        error = null,
                        isLoading = false,
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to send verification email")
                _uiState.update { 
                    it.copy(
                        error = "Failed to send verification code: ${e.message ?: "Unknown error"}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun validateVerificationCode() {
        val code = _uiState.value.verificationCode.joinToString("")
        
        if (code.length != 6) {
            _uiState.update { it.copy(error = "Please enter the complete 6-digit code") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val isVerified = authRepository.verifyCode(_uiState.value.email, code)
                if (isVerified) {
                    _uiState.update { 
                        it.copy(
                            currentStep = it.currentStep + 1,
                            error = null,
                            isLoading = false
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "Invalid verification code",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to verify code")
                _uiState.update { 
                    it.copy(
                        error = "Failed to verify code: ${e.message ?: "Unknown error"}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun validateRegistrationInput(): Boolean {
        if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return false
        }
        
        if (_uiState.value.password != _uiState.value.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return false
        }
        
        return true
    }
    
    private fun registerServiceProvider(onNavigateToServiceSelection: (() -> Unit)?) {
        if (!validateRegistrationInput()) return
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val result = authRepository.registerServiceProvider(
                    _uiState.value.email,
                    _uiState.value.password,
                    _uiState.value.fullName,
                    _uiState.value.userType
                )
                
                result.fold(
                    onSuccess = { 
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        onNavigateToServiceSelection?.invoke()
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "Service provider registration failed")
                        _uiState.update { 
                            it.copy(
                                error = "Registration failed: ${exception.message ?: "Unknown error"}",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Service provider registration failed")
                _uiState.update { 
                    it.copy(
                        error = "Registration failed: ${e.message ?: "Unknown error"}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun registerCustomer(onNavigateToHome: (() -> Unit)?) {
        if (!validateRegistrationInput()) return
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val result = authRepository.registerUser(
                    _uiState.value.email,
                    _uiState.value.password,
                    _uiState.value.fullName,
                    _uiState.value.userType
                )
                
                result.fold(
                    onSuccess = { 
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        onNavigateToHome?.invoke()
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "Registration failed")
                        _uiState.update { 
                            it.copy(
                                error = "Registration failed: ${exception.message ?: "Unknown error"}",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Registration failed")
                _uiState.update { 
                    it.copy(
                        error = "Registration failed: ${e.message ?: "Unknown error"}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun initiateGoogleSignIn(onSignInIntentReady: (android.content.Intent) -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val signInIntent = googleSignInHelper.getSignInIntent()
                _uiState.update { it.copy(isLoading = false) }
                onSignInIntentReady(signInIntent)
            } catch (e: Exception) {
                Timber.e(e, "Failed to create Google Sign-In intent")
                _uiState.update { 
                    it.copy(
                        error = "Failed to start Google Sign-In. Please try again.",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun handleGoogleSignIn(onNavigateToUserTypeSelection: (String, String) -> Unit) {
        // This will be called after Google Sign-In is completed
        // The actual sign-in handling will be done in the Activity
    }
    
    fun handleGoogleSignInResult(data: android.content.Intent?, onNavigateToUserTypeSelection: (String, String) -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
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
                                val email = account.email ?: ""
                                val name = account.displayName ?: ""
                                _uiState.update { it.copy(isLoading = false, error = null) }
                                onNavigateToUserTypeSelection(email, name)
                            },
                            onFailure = { exception ->
                                Timber.e(exception, "Google authentication failed")
                                _uiState.update { 
                                    it.copy(
                                        error = "Google authentication failed. Please try again.",
                                        isLoading = false
                                    )
                                }
                            }
                        )
                    } else {
                        Timber.w("Google Sign-In completed but no ID token received")
                        _uiState.update { 
                            it.copy(
                                error = "Google Sign-In incomplete. Please try again.",
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
                        error = "Google Sign-In failed. Please try again.",
                        isLoading = false
                    )
                }
            }
        }
    }
}