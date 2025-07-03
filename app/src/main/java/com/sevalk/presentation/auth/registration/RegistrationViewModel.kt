package com.sevalk.presentation.auth.registration

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel : ViewModel() {
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
                // Here you would add validation logic for each step
                _uiState.update { it.copy(currentStep = it.currentStep + 1, error = null) }
            }
            RegistrationEvent.PreviousStep -> {
                if (_uiState.value.currentStep > 1) {
                    _uiState.update { it.copy(currentStep = it.currentStep - 1, error = null) }
                }
            }
            RegistrationEvent.SubmitRegistration -> {
                // Here you would call your repository/API to create the account
                println("Registration Submitted: ${_uiState.value}")
            }
        }
    }
}