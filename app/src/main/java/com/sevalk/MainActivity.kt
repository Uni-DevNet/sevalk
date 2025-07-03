// MainActivity.kt
package com.sevalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.sevalk.presentation.customer.profile.CustomerProfileScreen
import com.sevalk.presentation.customer.profile.UserProfile
import com.sevalk.presentation.provider.profile.ProviderProfile
import com.sevalk.presentation.provider.profile.ProviderProfileScreen
import com.sevalk.ui.theme.SevaLKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SevaLKTheme {
                SevaLKApp()
            }
        }
    }
}

@Composable
private fun SevaLKApp() {
    var isCustomerView by remember { mutableStateOf(true) }

    // Sample data for customer profile
    val sampleUserProfile = UserProfile(
        name = "John Smith",
        memberSince = "March 2023",
        location = "Weligama, Southern Province",
        totalBookings = 24,
        completedBookings = 10,
        rating = 4.8,
        email = "john.doe@email.com",
        phoneNumber = "+94 77 123 4567",
        joinDate = "March 2023"
    )

    // Sample data for provider profile
    val sampleProviderProfile = ProviderProfile(
        name = "John Plumbing",
        memberSince = "March 2023",
        completedJobs = 43,
        totalJobs = 327,
        location = "Weligama, Southern Province",
        totalEarnings = "LKR 45,600",
        email = "john.obus@email.com",
        phoneNumber = "+44 77 123 4567",
        isAvailable = true,
        responseTime = "1 hour"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isCustomerView) {
                    CustomerProfileScreen(
                        initialUserProfile = sampleUserProfile,
                        onSwitchToProviderClick = { isCustomerView = false },
                        onLogoutClick = { /* Handle logout */ },
                        onFavoritesClick = { /* Handle favorites */ },
                        onPaymentMethodsClick = { /* Handle payment methods */ },
                        onPrivacySecurityClick = { /* Handle privacy */ },
                        onHelpSupportClick = { /* Handle help */ }
                    )
                } else {
                    ProviderProfileScreen(
                        initialProviderProfile = sampleProviderProfile,
                        onSwitchToCustomerClick = { isCustomerView = true },
                        onLogoutClick = { /* Handle logout */ },
                        onServicesClick = { /* Handle services */ },
                        onPaymentMethodsClick = { /* Handle payments */ },
                        onPrivacySecurityClick = { /* Handle privacy */ },
                        onHelpSupportClick = { /* Handle help */ }
                    )
                }
            }
        }
    }
}