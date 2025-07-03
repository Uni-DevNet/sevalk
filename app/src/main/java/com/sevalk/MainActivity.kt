package com.sevalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sevalk.presentation.customer.booking.BookingScreen
import com.sevalk.presentation.customer.home.ServiceProviderMapScreen
import com.sevalk.ui.theme.SevaLKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SevaLKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //ServiceProviderMapScreen()
                    BookingScreen()
                }
            }
        }
    }
}