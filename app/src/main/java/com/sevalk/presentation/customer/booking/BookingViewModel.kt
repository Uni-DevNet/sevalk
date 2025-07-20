package com.sevalk.presentation.customer.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sevalk.data.models.ServiceProvider
import com.sevalk.data.repositories.ServiceProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val serviceProviderRepository: ServiceProviderRepository
) : ViewModel() {
    
    private val _serviceProvider = MutableStateFlow<ServiceProvider?>(null)
    val serviceProvider: StateFlow<ServiceProvider?> = _serviceProvider.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadServiceProvider(providerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Timber.d("Loading provider with ID: $providerId")
                val provider = serviceProviderRepository.getServiceProviderById(providerId)
                if (provider != null) {
                    _serviceProvider.value = provider
                    Timber.d("Provider loaded successfully: ${provider.businessName}")
                } else {
                    _error.value = "Provider not found"
                    Timber.w("Provider not found for ID: $providerId")
                }
            } catch (e: Exception) {
                _error.value = "Failed to load provider details"
                Timber.e(e, "Error loading provider $providerId")
                _serviceProvider.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
