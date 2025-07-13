package com.sevalk.presentation.auth.google

import com.sevalk.data.models.UserType

sealed class UserTypeSelectionEvent {
    data class UserTypeChanged(val userType: UserType) : UserTypeSelectionEvent()
    data class CreateAccount(
        val email: String,
        val fullName: String,
        val onNavigateToServiceSelection: () -> Unit,
        val onNavigateToHome: () -> Unit
    ) : UserTypeSelectionEvent()
}
