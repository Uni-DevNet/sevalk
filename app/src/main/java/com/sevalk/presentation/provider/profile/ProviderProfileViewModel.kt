package com.sevalk.presentation.provider.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.models.ServiceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import java.text.NumberFormat
import java.util.*

sealed class ProviderProfileState {
    object Loading : ProviderProfileState()
    data class Success(val profile: ProviderProfile) : ProviderProfileState()
    data class Error(val message: String) : ProviderProfileState()
}

@HiltViewModel
class ProviderProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProviderProfileState>(ProviderProfileState.Loading)
    val profileState: StateFlow<ProviderProfileState> = _profileState

    init {
        loadProviderProfile()
    }

    fun loadProviderProfile() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                
                // First get the provider document
                val providerDoc = firestore.collection("providers")
                    .document(userId)
                    .get()
                    .await()
                
                val provider = providerDoc.data?.let { ServiceProvider.fromMap(it) }
                    ?: throw Exception("Provider data not found")

                // Then get the user document for additional details
                val userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                val profile = ProviderProfile(
                    name = provider.businessName,
                    memberSince = formatDate(provider.createdAt),
                    completedJobs = provider.completedJobs,
                    totalJobs = provider.totalJobs,
                    location = "${provider.city}, ${provider.province}, ${provider.serviceLocation.country}",
                    totalEarnings = formatCurrency(provider.totalEarnings),
                    email = auth.currentUser?.email ?: "",
                    phoneNumber = userDoc.getString("phoneNumber") ?: "",
                    isAvailable = provider.isAvailable,
                    responseTime = provider.responseTime
                )

                _profileState.value = ProviderProfileState.Success(profile)
            } catch (e: Exception) {
                _profileState.value = ProviderProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun updateAvailabilityStatus(isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                firestore.collection("providers")
                    .document(userId)
                    .update("isAvailable", isAvailable)
                    .await()
                
                loadProviderProfile() // Reload profile after update
            } catch (e: Exception) {
                _profileState.value = ProviderProfileState.Error("Failed to update availability")
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        return android.text.format.DateFormat.format("MMMM yyyy", date).toString()
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance()
        format.currency = Currency.getInstance("LKR")
        return format.format(amount)
    }
}
