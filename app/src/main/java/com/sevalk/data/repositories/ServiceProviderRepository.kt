package com.sevalk.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.models.ServiceProvider
import com.sevalk.data.models.Service
import com.sevalk.presentation.components.map.ServiceProvider as MapServiceProvider
import com.sevalk.presentation.components.map.ServiceType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import timber.log.Timber

class ServiceProviderRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun getServiceType(service: Service?): ServiceType {
        return when (service?.name?.uppercase()) {
            "PLUMBING" -> ServiceType.PLUMBING
            "ELECTRICAL" -> ServiceType.ELECTRICAL
            "CLEANING", "CLEANING (RESIDENTIAL)" -> ServiceType.CLEANING
            else -> ServiceType.ALL
        }
    }

    suspend fun getServiceProviders(): List<MapServiceProvider> {
        return try {
            val snapshot = firestore.collection("service_providers")
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    
                    // Extract location data
                    val serviceLocation = data["serviceLocation"] as? Map<String, Any>
                    val latitude = serviceLocation?.get("latitude") as? Double ?: 0.0
                    val longitude = serviceLocation?.get("longitude") as? Double ?: 0.0
                    
                    // Extract services array
                    val services = data["services"] as? List<Map<String, Any>> ?: emptyList()
                    val firstService = services.firstOrNull()
                    
                    // Get service name from first service
                    val serviceName = firstService?.get("name") as? String
                    
                    Timber.d("Provider ${document.id} service: $serviceName")
                    
                    MapServiceProvider(
                        id = document.id,
                        name = data["businessName"] as? String ?: "Unknown",
                        type = getServiceTypeFromName(serviceName),
                        latitude = latitude,
                        longitude = longitude,
                        rating = (data["rating"] as? Double)?.toFloat() ?: 0f,
                        description = data["description"] as? String ?: "",
                        phone = data["phone"] as? String ?: ""
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Error mapping provider document ${document.id}")
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting providers")
            emptyList()
        }
    }

    private fun getServiceTypeFromName(serviceName: String?): ServiceType {
        return when (serviceName?.uppercase()) {
            "PLUMBING" -> ServiceType.PLUMBING
            "ELECTRICAL", "ELECTRICAL REPAIR" -> ServiceType.ELECTRICAL
            "CLEANING", "CLEANING (RESIDENTIAL)" -> ServiceType.CLEANING
            else -> {
                Timber.d("Unknown service type: $serviceName")
                ServiceType.ALL
            }
        }
    }

    suspend fun searchServiceProviders(query: String): List<MapServiceProvider> {
        return try {
            val snapshot = firestore.collection("service_providers")
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    
                    // Extract provider data
                    val businessName = data["businessName"] as? String ?: return@mapNotNull null
                    val description = data["description"] as? String ?: ""
                    val services = data["services"] as? List<Map<String, Any>> ?: emptyList()
                    
                    // Check if provider matches search query
                    val matchesQuery = query.isEmpty() || 
                        businessName.contains(query, ignoreCase = true) ||
                        description.contains(query, ignoreCase = true) ||
                        services.any { service -> 
                            (service["name"] as? String)?.contains(query, ignoreCase = true) == true
                        }
                    
                    if (!matchesQuery) return@mapNotNull null
                    
                    // Extract location data
                    val serviceLocation = data["serviceLocation"] as? Map<String, Any>
                    val latitude = serviceLocation?.get("latitude") as? Double ?: 0.0
                    val longitude = serviceLocation?.get("longitude") as? Double ?: 0.0
                    
                    // Get service type from first service
                    val firstService = services.firstOrNull()
                    val serviceName = firstService?.get("name") as? String
                    
                    MapServiceProvider(
                        id = document.id,
                        name = businessName,
                        type = getServiceTypeFromName(serviceName),
                        latitude = latitude,
                        longitude = longitude,
                        rating = (data["rating"] as? Double)?.toFloat() ?: 0f,
                        description = description,
                        phone = data["phone"] as? String ?: ""
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Error mapping provider document ${document.id}")
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error searching providers")
            emptyList()
        }
    }
}