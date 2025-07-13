package com.sevalk.presentation.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sevalk.data.models.UserType
import com.sevalk.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationState())
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()

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
}