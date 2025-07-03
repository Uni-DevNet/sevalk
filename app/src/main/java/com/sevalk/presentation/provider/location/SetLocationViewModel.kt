package com.sevalk.presentation.provider.location

import android.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sevalk.data.models.LocationMethod
import com.sevalk.data.models.ServiceLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetLocationViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SetLocationUiState())
    val uiState: StateFlow<SetLocationUiState> = _uiState.asStateFlow()
    
    private val _serviceRadius = MutableStateFlow(5f)
    val serviceRadius: StateFlow<Float> = _serviceRadius.asStateFlow()
    
    init {
        initializeLocationMethods()
        loadCurrentLocation()
    }
    
    private fun initializeLocationMethods() {
        val methods = listOf(
            LocationMethod(
                id = "current_location",
                title = "Use Current Location",
                description = "Get location from GPS",
                iconRes = R.drawable.ic_menu_mylocation,
                isSelected = true
            ),
            LocationMethod(
                id = "select_on_map",
                title = "Select on Map",
                description = "Choose your location manually",
                iconRes = R.drawable.ic_dialog_map
            )
        )
        _uiState.value = _uiState.value.copy(locationMethods = methods)
    }
    
    private fun loadCurrentLocation() {
        // Simulate loading current location
        val location = ServiceLocation(
            address = "123 Main Street, Weligama, Southern Province, Sri Lanka",
            city = "Weligama",
            province = "Southern Province",
            country = "Sri Lanka"
        )
        _uiState.value = _uiState.value.copy(primaryLocation = location)
    }
    
    fun selectLocationMethod(methodId: String) {
        val updatedMethods = _uiState.value.locationMethods.map { method ->
            method.copy(isSelected = method.id == methodId)
        }
        _uiState.value = _uiState.value.copy(locationMethods = updatedMethods)
        
        if (methodId == "current_location") {
            getCurrentLocation()
        } else {
            openMapSelection()
        }
    }
    
    fun updateServiceRadius(radius: Float) {
        _serviceRadius.value = radius
    }
    
    fun changeLocation() {
        _uiState.value = _uiState.value.copy(isChangingLocation = true)
    }
    
    fun completeSetup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Simulate API call
            delay(1000)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                setupCompleted = true
            )
        }
    }
    
    private fun getCurrentLocation() {
        // Implement GPS location fetching
    }
    
    private fun openMapSelection() {
        // Implement map selection navigation
    }
}

data class SetLocationUiState(
    val locationMethods: List<LocationMethod> = emptyList(),
    val primaryLocation: ServiceLocation? = null,
    val isLoading: Boolean = false,
    val isChangingLocation: Boolean = false,
    val setupCompleted: Boolean = false,
    val error: String? = null
)
