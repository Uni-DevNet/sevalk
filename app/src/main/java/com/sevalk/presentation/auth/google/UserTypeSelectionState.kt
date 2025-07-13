package com.sevalk.presentation.auth.google

import com.sevalk.data.models.UserType

data class UserTypeSelectionState(
    val selectedUserType: UserType = UserType.CUSTOMER,
    val isLoading: Boolean = false,
    val error: String? = null
)
