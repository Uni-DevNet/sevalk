package com.sevalk.presentation.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

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