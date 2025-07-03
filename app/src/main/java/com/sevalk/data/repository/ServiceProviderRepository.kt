package com.sevalk.data.repository

import com.sevalk.presentation.components.map.ServiceProvider
import com.sevalk.presentation.components.map.ServiceType

// Mock data repository for service providers
class ServiceProviderRepository {
    
    companion object {
        fun getServiceProviders(): List<ServiceProvider> {
            return listOf(
                ServiceProvider(
                    id = "1", 
                    name = "Matara Central College", 
                    type = ServiceType.PLUMBING, 
                    latitude = 5.9485, 
                    longitude = 80.5353,
                    rating = 4.5f,
                    description = "Professional plumbing services",
                    phone = "+94 77 123 4567",
                    address = "Main Street, Matara"
                ),
                ServiceProvider(
                    id = "2", 
                    name = "Department of Immigration", 
                    type = ServiceType.ELECTRICAL, 
                    latitude = 5.9475, 
                    longitude = 80.5343,
                    rating = 4.2f,
                    description = "Electrical repair and installation",
                    phone = "+94 77 234 5678",
                    address = "Government Building, Matara"
                ),
                ServiceProvider(
                    id = "3", 
                    name = "Sri Darmawansa Mawatha", 
                    type = ServiceType.CLEANING, 
                    latitude = 5.9495, 
                    longitude = 80.5363,
                    rating = 4.8f,
                    description = "House and office cleaning services",
                    phone = "+94 77 345 6789",
                    address = "Sri Darmawansa Mawatha, Matara"
                ),
                ServiceProvider(
                    id = "4", 
                    name = "Dr. S.A. Wickremasinghe Mawatha", 
                    type = ServiceType.PLUMBING, 
                    latitude = 5.9465, 
                    longitude = 80.5333,
                    rating = 4.0f,
                    description = "Expert plumbing solutions",
                    phone = "+94 77 456 7890",
                    address = "Dr. S.A. Wickremasinghe Mawatha, Matara"
                ),
                ServiceProvider(
                    id = "5", 
                    name = "Samanmal - Matara", 
                    type = ServiceType.ELECTRICAL, 
                    latitude = 5.9455, 
                    longitude = 80.5323,
                    rating = 4.3f,
                    description = "Residential electrical services",
                    phone = "+94 77 567 8901",
                    address = "Samanmal Road, Matara"
                ),
                ServiceProvider(
                    id = "6", 
                    name = "Cargills Food City - Matara 1", 
                    type = ServiceType.CLEANING, 
                    latitude = 5.9445, 
                    longitude = 80.5313,
                    rating = 4.6f,
                    description = "Commercial cleaning services",
                    phone = "+94 77 678 9012",
                    address = "Near Cargills, Matara"
                ),
                ServiceProvider(
                    id = "7", 
                    name = "Weligama Football Stadium", 
                    type = ServiceType.ALL, 
                    latitude = 5.9435, 
                    longitude = 80.5303,
                    rating = 4.1f,
                    description = "Multi-service provider",
                    phone = "+94 77 789 0123",
                    address = "Stadium Road, Weligama"
                )
            )
        }
        
        fun getServiceProvidersByType(type: ServiceType): List<ServiceProvider> {
            return if (type == ServiceType.ALL) {
                getServiceProviders()
            } else {
                getServiceProviders().filter { it.type == type }
            }
        }
        
        fun searchServiceProviders(query: String): List<ServiceProvider> {
            return getServiceProviders().filter { provider ->
                provider.name.contains(query, ignoreCase = true) ||
                provider.description.contains(query, ignoreCase = true) ||
                provider.type.displayName.contains(query, ignoreCase = true)
            }
        }
        
        fun getServiceProviderById(id: String): ServiceProvider? {
            return getServiceProviders().find { it.id == id }
        }
    }
}
