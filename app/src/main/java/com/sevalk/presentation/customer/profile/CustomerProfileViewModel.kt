package com.sevalk.presentation.customer.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.repositories.ImageRepository
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
    private val auth: FirebaseAuth,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage

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
                                joinDate = formatDate(userData["createdAt"] as? Long ?: System.currentTimeMillis()),
                                profileImageUrl = userData["profileImageUrl"] as? String
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

    fun uploadProfileImage(imageUri: Uri) {
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            _isUploadingImage.value = true
            try {
                // Get current profile image URL to delete old image if exists
                val currentProfile = _userProfile.value
                val oldImageUrl = currentProfile?.profileImageUrl
                
                val result = imageRepository.uploadProfileImage(imageUri, currentUser.uid)
                result.onSuccess { imageUrl ->
                    // Update Firestore with new profile image URL
                    firestore.collection("users").document(currentUser.uid)
                        .update("profileImageUrl", imageUrl)
                        .await()

                    // Delete old image if it exists
                    oldImageUrl?.let { oldUrl ->
                        try {
                            val fileName = extractFileNameFromUrl(oldUrl)
                            if (fileName.isNotEmpty()) {
                                imageRepository.deleteProfileImage(fileName)
                            }
                        } catch (e: Exception) {
                            Log.w("ProfileVM", "Could not delete old profile image", e)
                        }
                    }

                    loadUserProfile() // Reload profile to show new image
                    Log.d("ProfileVM", "Profile image uploaded successfully: $imageUrl")
                }.onFailure { exception ->
                    Log.e("ProfileVM", "Error uploading profile image", exception)
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error uploading profile image", e)
            } finally {
                _isUploadingImage.value = false
            }
        }
    }
    
    private fun extractFileNameFromUrl(url: String): String {
        return try {
            url.substringAfterLast("/")
        } catch (e: Exception) {
            ""
        }
    }
}


