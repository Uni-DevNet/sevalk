package com.sevalk.data.models

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val userType: UserType = UserType.CUSTOMER,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserType(val displayName: String) {
    CUSTOMER("I'm looking for services (Customer)"),
    SERVICE_PROVIDER("I want to offer services (Provider)")
}
