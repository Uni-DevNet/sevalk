package com.sevalk.presentation.provider.service

import androidx.lifecycle.ViewModel
import com.sevalk.data.models.PricingModel
import com.sevalk.data.models.Service
import com.sevalk.data.models.ServiceCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServiceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    /**
     * Toggles the selection state of a service.
     * If a service is selected, its price input field becomes visible.
     */
    fun onServiceSelected(categoryId: String, serviceId: Int, isSelected: Boolean) {
        _uiState.update { currentState ->
            val updatedCategories = currentState.serviceCategories.map { category ->
                if (category.name == categoryId) {
                    category.copy(services = category.services.map { service ->
                        if (service.id == serviceId) {
                            service.copy(isSelected = isSelected, isExpanded = isSelected)
                        } else {
                            service
                        }
                    })
                } else {
                    category
                }
            }
            currentState.copy(serviceCategories = updatedCategories)
        }
    }

    /**
     * Updates the price for a given service.
     */
    fun onPriceChanged(categoryId: String, serviceId: Int, newPrice: String) {
        // Allow only numeric input for price
        if (newPrice.all { it.isDigit() }) {
            _uiState.update { currentState ->
                val updatedCategories = currentState.serviceCategories.map { category ->
                    if (category.name == categoryId) {
                        category.copy(services = category.services.map { service ->
                            if (service.id == serviceId) {
                                service.copy(price = newPrice)
                            } else {
                                service
                            }
                        })
                    } else {
                        category
                    }
                }
                currentState.copy(serviceCategories = updatedCategories)
            }
        }
    }

    /**
     * Toggles the expanded/collapsed state of a service category.
     */
    fun onCategoryToggled(categoryId: String) {
        _uiState.update { currentState ->
            val updatedCategories = currentState.serviceCategories.map {
                if (it.name == categoryId) it.copy(isExpanded = !it.isExpanded) else it
            }
            currentState.copy(serviceCategories = updatedCategories)
        }
    }

    /**
     * Updates the search query.
     * This triggers a filter on the displayed services.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Updates the service provider name.
     */
    fun onProviderNameChanged(name: String) {
        _uiState.update { it.copy(serviceProviderName = name) }
    }


    /**
     * Loads the initial list of services and categories.
     * In a real app, this would come from a repository (network/database).
     */
    private fun loadInitialData() {
        val initialCategories = listOf(
            ServiceCategory(
                name = "Home Services",
                services = listOf(
                    Service(1, "Plumbing", "Fixing and installing water systems like pipes and faucets.", PricingModel.HOURLY),
                    Service(2, "Cleaning (Residential)", "General home cleaning services.", PricingModel.DAILY_FIXED),
                    Service(3, "Painting & Decorating", "Interior and exterior painting, wall design.", PricingModel.PER_SQ_FT),
                    Service(4, "Appliance Repair", "Repairing home appliances like refrigerators, washing machines.", PricingModel.FIXED)
                )
            ),
            ServiceCategory(
                name = "Education & Tutoring",
                services = listOf(
                    Service(5, "Math Tutoring", "Helping students understand and solve math problems.", PricingModel.HOURLY),
                    Service(6, "Music Lessons", "Teaching instruments or vocals.", PricingModel.HOURLY),
                    Service(7, "Test Prep (SAT, ACT, etc.)", "Guiding students in preparation for exams.", PricingModel.HOURLY),
                    Service(8, "Academic Writing Help", "Assisting with essays and academic papers.", PricingModel.FIXED)
                )
            ),
            ServiceCategory(
                name = "Personal Care & Wellness",
                services = listOf(
                    Service(9, "Hair Styling & Cutting", "Haircuts and styling for men, women, and children.", PricingModel.FIXED),
                    Service(10, "Massage Therapy", "Relaxation and therapeutic massages.", PricingModel.HOURLY),
                    Service(11, "Personal Training", "Fitness training and workout guidance.", PricingModel.HOURLY),
                    Service(12, "Child Care/Babysitting", "Looking after children at home.", PricingModel.DAILY_FIXED)
                )
            ),
            // Add other categories here...
        )
        _uiState.value = ServiceUiState(serviceCategories = initialCategories)
    }
}