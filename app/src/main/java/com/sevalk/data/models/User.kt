package com.sevalk.data.models

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val userType: UserType = UserType.CUSTOMER,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserType {
    CUSTOMER,
    SERVICE_PROVIDER
}
