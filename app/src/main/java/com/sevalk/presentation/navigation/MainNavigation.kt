package com.sevalk.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sevalk.presentation.chat.ChatScreen
import com.sevalk.presentation.customer.booking.BookingScreen
import com.sevalk.presentation.customer.booking.MyBookingsScreen
import com.sevalk.presentation.customer.home.HomeScreen
import com.sevalk.presentation.customer.home.ServiceProviderMapScreen
import com.sevalk.presentation.customer.profile.CustomerProfileScreen
import com.sevalk.presentation.customer.profile.UserProfile
import com.sevalk.presentation.provider.home.ProviderHomeScreen
import com.sevalk.presentation.provider.profile.ProviderProfile
import com.sevalk.presentation.provider.profile.ProviderProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    navController: NavController,
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    var isProviderMode by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                NavigationTab.HOME -> {
                    if (isProviderMode) {
                        ProviderHomeScreen(navController = navController)
                    } else {
                        HomeScreen(navController = navController)
                    }
                }
                NavigationTab.SEARCH -> {
                    // Search screen placeholder
                    ServiceProviderMapScreen()
                }
                NavigationTab.BOOKINGS -> {
                    // Bookings screen placeholder  
                    MyBookingsScreen(navController = navController)
                }
                NavigationTab.MESSAGES -> {
                    // Messages screen placeholder
                    ChatScreen(navController = navController)
                }
                NavigationTab.PROFILE -> {
                    if (isProviderMode) {
                        ProviderProfileScreen(
                            initialProviderProfile = ProviderProfile(
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
                            ),
                            onSwitchToCustomerClick = { isProviderMode = false },
                            onLogoutClick = {},
                            onServicesClick = {},
                            onPaymentMethodsClick = {},
                            onPrivacySecurityClick = {},
                            onHelpSupportClick = {}
                        )
                    } else {
                        CustomerProfileScreen(
                            navController = navController,
                            initialUserProfile = UserProfile(
                                name = "John Smith",
                                memberSince = "March 2023",
                                location = "Weligama, Southern Province",
                                totalBookings = 24,
                                completedBookings = 10,
                                rating = 4.8,
                                email = "john.doe@email.com",
                                phoneNumber = "+94 77 123 4567",
                                joinDate = "March 2023"
                            ),
                            onSwitchToProviderClick = { isProviderMode = true },
                            // onEditProfileClick is now handled internally
                            onLogoutClick = {},
                            onFavoritesClick = {},
                            onPaymentMethodsClick = {},
                            onPrivacySecurityClick = {},
                            onHelpSupportClick = {}
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MessagesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Messages Screen")
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Profile Screen")
    }
}
