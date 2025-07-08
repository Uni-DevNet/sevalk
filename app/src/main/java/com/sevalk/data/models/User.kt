package com.sevalk.data.models

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val phoneNumber: String = "",
    val userType: UserType = UserType.CUSTOMER,
    val address: Address? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deviceTokens: List<String> = emptyList(), // For push notifications
    val preferences: UserPreferences = UserPreferences()
)

enum class UserType(val displayName: String) {
    CUSTOMER("I'm looking for services (Customer)"),
    SERVICE_PROVIDER("I want to offer services (Provider)")
}
