package com.sevalk.presentation.auth.login

sealed class LoginEvent {
    data class GoogleSignIn(val onNavigateToUserTypeSelection: (String, String) -> Unit, val onLoginSuccess: () -> Unit) : LoginEvent()
}
