package com.sevalk.data.models

data class ServiceProvider(
    val id: String = "",
    val userId: String = "", // Reference to User
    val businessName: String = "",
    val description: String = "",
    val services: List<Service> = emptyList(),
    val serviceRadius: Double = 10.0, // in kilometers
    val serviceLocation: ServiceLocation = ServiceLocation(),
    val experience: Int = 0, // Years of experience
    val rating: Float = 0.0f,
    val totalReviews: Int = 0,
    val completedJobs: Int = 0,
    val responseTime: String = "", // e.g., "Within 1 hour"
    val isVerified: Boolean = false,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: ProviderStatus = ProviderStatus.PENDING
)

enum class ProviderStatus {
    PENDING, APPROVED, SUSPENDED, REJECTED
}

