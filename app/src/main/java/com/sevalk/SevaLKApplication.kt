package com.sevalk

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sevalk.utils.Constants
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class SevaLKApplication : Application() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    companion object {
        var isNetworkAvailable by mutableStateOf(true)
            private set

        var currentUserId by mutableStateOf<String?>(null)
            private set

        var isProviderMode by mutableStateOf(false)
            private set

        fun updateNetworkStatus(isAvailable: Boolean) {
            isNetworkAvailable = isAvailable
        }

        fun updateCurrentUser(userId: String?) {
            currentUserId = userId
        }

        fun updateProviderMode(isProvider: Boolean) {
            isProviderMode = isProvider
        }
    }


    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase FIRST - before any dependency injection that might use it
        initializeFirebase()

        // Initialize logging
        initializeLogging()

        // Setup Firebase listeners
        setupFirebaseListeners()

        Timber.d("SevaLK Application initialized successfully")
    }

    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In production, you might want to plant a different tree
            // that logs to Crashlytics or another service
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Log to crash reporting service in production
                    // FirebaseCrashlytics.getInstance().log(message)
                }
            })
        }
    }

    private fun initializeFirebase() {
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Configure Firebase Realtime Database with offline persistence
        // THIS MUST BE DONE BEFORE ANY OTHER USAGE OF FirebaseDatabase
        try {
            val realtimeDatabase = FirebaseDatabase.getInstance()
            realtimeDatabase.setPersistenceEnabled(true)
            Timber.d("Firebase Realtime Database persistence enabled")
        } catch (e: Exception) {
            Timber.e(e, "Failed to enable Firebase Realtime Database persistence")
        }

        // Configure Firestore settings for offline persistence
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings
    }

    private fun setupFirebaseListeners() {
        // Listen for auth state changes
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            updateCurrentUser(user?.uid)

            if (user != null) {
                Timber.d("User authenticated: ${user.uid}")
                // Initialize user-specific services
                initializeUserServices(user.uid)
            } else {
                Timber.d("User signed out")
                // Clean up user-specific data
                cleanupUserServices()
            }
        }
    }

    private fun initializeUserServices(userId: String) {
        // Check if user is a service provider
        FirebaseFirestore.getInstance()
            .collection(Constants.COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userType = document.getString("userType")
                    updateProviderMode(userType == Constants.USER_TYPE_PROVIDER)
                    Timber.d("User type determined: $userType")
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Failed to get user type")
            }
    }

    private fun cleanupUserServices() {
        // Reset global state
        updateCurrentUser(null)
        updateProviderMode(false)

        // Clear any cached data
        // cacheManager.clearUserCache()
    }

    override fun onTerminate() {
        super.onTerminate()
        Timber.d("SevaLK Application terminated")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.w("Low memory warning - clearing caches")
        // Clear non-essential caches
        // cacheManager.clearNonEssentialCache()
    }
}