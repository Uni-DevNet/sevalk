package com.sevalk.presentation.customer.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CustomerProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                try {
                    val document = firestore.collection("users").document(user.uid).get().await()
                    if (document.exists()) {
                        val userData = document.data
                        if (userData != null) {
                            _userProfile.value = UserProfile(
                                name = userData["displayName"] as? String ?: "Guest User",
                                memberSince = formatDate(userData["createdAt"] as? Long ?: System.currentTimeMillis()),
                                location = (userData["address"] as? Map<*, *>)?.get("city") as? String ?: "Sri Lanka",
                                totalBookings = 0, // Will implement later
                                completedBookings = 0, // Will implement later
                                rating = 0.0, // Will implement later
                                email = userData["email"] as? String ?: "",
                                phoneNumber = userData["phoneNumber"] as? String ?: "",
                                joinDate = formatDate(userData["createdAt"] as? Long ?: System.currentTimeMillis())
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProfileVM", "Error loading profile", e)
                }
            }
        }
    }

    fun updateUserProfile(name: String, phoneNumber: String) {
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "displayName" to name,
                    "phoneNumber" to phoneNumber,
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection("users").document(currentUser.uid)
                    .update(updates)
                    .await()

                loadUserProfile() // Reload profile after update
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error updating profile", e)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _userProfile.value = null
        Log.d("ProfileVM", "User signed out")
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            Log.e("ProfileVM", "Error formatting date", e)
            "Unknown"
        }
    }
}


