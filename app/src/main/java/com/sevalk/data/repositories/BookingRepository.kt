package com.sevalk.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.models.Booking
import com.sevalk.data.models.BookingEvent
import com.sevalk.data.models.BookingPricing
import com.sevalk.data.models.BookingStatus
import com.sevalk.data.models.BookingTimelineEvent
import com.sevalk.utils.Constants
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Result<String>
    suspend fun getBookingById(bookingId: String): Result<Booking?>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
}

class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookingRepository {
    
    override suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val bookingId = UUID.randomUUID().toString()
            val bookingWithId = booking.copy(
                id = bookingId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                timeline = listOf(
                    BookingTimelineEvent(
                        id = UUID.randomUUID().toString(),
                        event = BookingEvent.CREATED,
                        timestamp = System.currentTimeMillis(),
                        description = "Booking request created",
                        performedBy = booking.customerId
                    )
                )
            )
            
            val bookingData = bookingToMap(bookingWithId)
            
            firestore.collection(Constants.COLLECTION_BOOKINGS)
                .document(bookingId)
                .set(bookingData)
                .await()
            
            Timber.d("Booking created successfully with ID: $bookingId")
            Result.success(bookingId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create booking")
            Result.failure(e)
        }
    }
    
    override suspend fun getBookingById(bookingId: String): Result<Booking?> {
        return try {
            val document = firestore.collection(Constants.COLLECTION_BOOKINGS)
                .document(bookingId)
                .get()
                .await()
            
            if (document.exists()) {
                val bookingData = document.data
                if (bookingData != null) {
                    val booking = mapToBooking(bookingData)
                    Result.success(booking)
                } else {
                    Result.success(null)
                }
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get booking")
            Result.failure(e)
        }
    }
    
    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            
            firestore.collection(Constants.COLLECTION_BOOKINGS)
                .document(bookingId)
                .update(updates)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update booking status")
            Result.failure(e)
        }
    }
    
    private fun bookingToMap(booking: Booking): Map<String, Any> {
        return mapOf(
            "id" to booking.id,
            "customerId" to booking.customerId,
            "providerId" to booking.providerId,
            "serviceId" to booking.serviceId,
            "serviceName" to booking.serviceName,
            "description" to booking.description,
            "serviceLocation" to mapOf(
                "address" to booking.serviceLocation.address,
                "city" to booking.serviceLocation.city,
                "province" to booking.serviceLocation.province,
                "country" to booking.serviceLocation.country,
                "latitude" to booking.serviceLocation.latitude,
                "longitude" to booking.serviceLocation.longitude
            ),
            "scheduledDate" to booking.scheduledDate,
            "scheduledTime" to booking.scheduledTime,
            "estimatedDuration" to booking.estimatedDuration,
            "pricing" to mapOf(
                "basePrice" to booking.pricing.basePrice,
                "totalAmount" to booking.pricing.totalAmount,
                "paymentStatus" to booking.pricing.paymentStatus.name
            ),
            "status" to booking.status.name,
            "priority" to booking.priority.name,
            "specialInstructions" to booking.specialInstructions,
            "attachments" to booking.attachments,
            "createdAt" to booking.createdAt,
            "updatedAt" to booking.updatedAt,
            "timeline" to booking.timeline.map { event ->
                mapOf(
                    "id" to event.id,
                    "event" to event.event.name,
                    "timestamp" to event.timestamp,
                    "description" to event.description,
                    "performedBy" to event.performedBy
                )
            }
        )
    }
    
    private fun mapToBooking(data: Map<String, Any>): Booking {
        // Implementation for converting map back to Booking object
        // This is a simplified version - you might want to add more robust parsing
        return Booking(
            id = data["id"] as? String ?: "",
            customerId = data["customerId"] as? String ?: "",
            providerId = data["providerId"] as? String ?: "",
            serviceId = data["serviceId"] as? String ?: "",
            serviceName = data["serviceName"] as? String ?: "",
            description = data["description"] as? String ?: "",
            scheduledDate = data["scheduledDate"] as? Long ?: 0L,
            scheduledTime = data["scheduledTime"] as? String ?: "",
            status = BookingStatus.valueOf(data["status"] as? String ?: "PENDING"),
            createdAt = data["createdAt"] as? Long ?: 0L,
            updatedAt = data["updatedAt"] as? Long ?: 0L
        )
    }
}
