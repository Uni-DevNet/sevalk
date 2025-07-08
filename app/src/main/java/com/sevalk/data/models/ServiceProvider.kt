package com.sevalk.data.models

data class ServiceProvider(
    val id: String = "",
    val userId: String = "", // Reference to User
    val businessName: String = "",
    val description: String = "",
    val serviceCategories: List<String> = emptyList(), // Category IDs
    val services: List<ServiceOffering> = emptyList(),
    val serviceRadius: Double = 10.0, // in kilometers
    val serviceLocation: ServiceLocation = ServiceLocation(),
    val experience: Int = 0, // Years of experience
    val certifications: List<String> = emptyList(),
    val rating: Float = 0.0f,
    val totalReviews: Int = 0,
    val completedJobs: Int = 0,
    val responseTime: String = "", // e.g., "Within 1 hour"
    val isVerified: Boolean = false,
    val isAvailable: Boolean = true,
    val pricing: PricingInfo = PricingInfo(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: ProviderStatus = ProviderStatus.PENDING
)

data class ServiceOffering(
    val serviceId: String = "",
    val serviceName: String = "",
    val description: String = "",
    val basePrice: Double = 0.0,
    val pricingModel: PricingModel = PricingModel.FIXED,
    val estimatedDuration: String = "", // e.g., "2-3 hours"
    val isActive: Boolean = true
)

data class PricingInfo(
    val minimumCharge: Double = 0.0,
    val travelFee: Double = 0.0,
    val emergencyRate: Double = 0.0, // Additional charge for urgent jobs
    val discounts: List<Discount> = emptyList()
)

data class Discount(
    val type: DiscountType = DiscountType.PERCENTAGE,
    val value: Double = 0.0,
    val description: String = "",
    val minOrderValue: Double = 0.0
)

enum class ProviderStatus {
    PENDING, APPROVED, SUSPENDED, REJECTED
}

enum class DiscountType {
    PERCENTAGE, FIXED_AMOUNT
}

enum class PaymentMethod {
    CASH, CARD, BANK_TRANSFER, DIGITAL_WALLET
}