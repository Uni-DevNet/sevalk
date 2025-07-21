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
    suspend fun getBookingsByCustomerId(customerId: String): Result<List<Booking>>
    suspend fun getProviderById(providerId: String): Result<Map<String, Any>?>
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
    
    override suspend fun getBookingsByCustomerId(customerId: String): Result<List<Booking>> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_BOOKINGS)
                .whereEqualTo("customerId", customerId)
                .get()
                .await()
            
            val bookings = querySnapshot.documents.mapNotNull { document ->
                document.data?.let { data ->
                    mapToBooking(data)
                }
            }.sortedByDescending { it.createdAt } // Sort on client side by creation date
            
            Timber.d("Retrieved ${bookings.size} bookings for customer: $customerId")
            Result.success(bookings)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get bookings for customer: $customerId")
            Result.failure(e)
        }
    }
    
    override suspend fun getProviderById(providerId: String): Result<Map<String, Any>?> {
        return try {
            val document = firestore.collection(Constants.COLLECTION_USERS)
                .document(providerId)
                .get()
                .await()
            
            if (document.exists()) {
                Result.success(document.data)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get provider details")
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
        try {
            // Parse service location
            val locationData = data["serviceLocation"] as? Map<String, Any> ?: emptyMap()
            val serviceLocation = com.sevalk.data.models.ServiceLocation(
                address = locationData["address"] as? String ?: "",
                city = locationData["city"] as? String ?: "",
                province = locationData["province"] as? String ?: "",
                country = locationData["country"] as? String ?: "Sri Lanka",
                latitude = (locationData["latitude"] as? Number)?.toDouble() ?: 0.0,
                longitude = (locationData["longitude"] as? Number)?.toDouble() ?: 0.0
            )
            
            // Parse pricing
            val pricingData = data["pricing"] as? Map<String, Any> ?: emptyMap()
            val pricing = BookingPricing(
                basePrice = (pricingData["basePrice"] as? Number)?.toDouble() ?: 0.0,
                totalAmount = (pricingData["totalAmount"] as? Number)?.toDouble() ?: 0.0,
                paymentStatus = try {
                    com.sevalk.data.models.PaymentStatus.valueOf(
                        pricingData["paymentStatus"] as? String ?: "PENDING"
                    )
                } catch (e: Exception) {
                    com.sevalk.data.models.PaymentStatus.PENDING
                }
            )
            
            // Parse timeline
            val timelineData = data["timeline"] as? List<Map<String, Any>> ?: emptyList()
            val timeline = timelineData.map { eventData ->
                BookingTimelineEvent(
                    id = eventData["id"] as? String ?: UUID.randomUUID().toString(),
                    event = try {
                        BookingEvent.valueOf(eventData["event"] as? String ?: "CREATED")
                    } catch (e: Exception) {
                        BookingEvent.CREATED
                    },
                    timestamp = (eventData["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                    description = eventData["description"] as? String ?: "",
                    performedBy = eventData["performedBy"] as? String ?: ""
                )
            }
            
            return Booking(
                id = data["id"] as? String ?: "",
                customerId = data["customerId"] as? String ?: "",
                providerId = data["providerId"] as? String ?: "",
                serviceId = data["serviceId"] as? String ?: "",
                serviceName = data["serviceName"] as? String ?: "",
                description = data["description"] as? String ?: "",
                serviceLocation = serviceLocation,
                scheduledDate = (data["scheduledDate"] as? Number)?.toLong() ?: 0L,
                scheduledTime = data["scheduledTime"] as? String ?: "",
                estimatedDuration = ((data["estimatedDuration"] as? Number)?.toInt() ?: 60).toString(),
                pricing = pricing,
                status = try {
                    BookingStatus.valueOf(data["status"] as? String ?: "PENDING")
                } catch (e: Exception) {
                    BookingStatus.PENDING
                },
                priority = try {
                    com.sevalk.data.models.BookingPriority.valueOf(
                        data["priority"] as? String ?: "NORMAL"
                    )
                } catch (e: Exception) {
                    com.sevalk.data.models.BookingPriority.NORMAL
                },
                specialInstructions = data["specialInstructions"] as? String ?: "",
                attachments = (data["attachments"] as? List<String>) ?: emptyList(),
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L,
                timeline = timeline
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping booking data: $data")
            // Return a default booking with available data
            return Booking(
                id = data["id"] as? String ?: "",
                customerId = data["customerId"] as? String ?: "",
                serviceName = data["serviceName"] as? String ?: "Unknown Service",
                status = BookingStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
