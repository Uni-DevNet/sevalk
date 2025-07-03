package com.sevalk

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SevaLKApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global resources or configurations here if needed
        Log.d("SevaLKApp", "SevaLKApplication started")
    }
}