package com.sevalk.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sevalk.presentation.chat.ChatScreen
import com.sevalk.presentation.customer.booking.BookingScreen
import com.sevalk.presentation.customer.booking.MyBookingsScreen
import com.sevalk.presentation.customer.home.HomeScreen
import com.sevalk.presentation.customer.home.ServiceProviderMapScreen
import com.sevalk.presentation.customer.profile.CustomerProfileScreen
import com.sevalk.presentation.customer.profile.UserProfile
import com.sevalk.presentation.provider.home.ProviderHomeScreen
import com.sevalk.presentation.provider.jobs.JobsScreen
import com.sevalk.presentation.provider.profile.ProviderProfile
import com.sevalk.presentation.provider.profile.ProviderProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    navController: NavController,
) {
    var customerSelectedTab by remember { mutableStateOf(CustomerNavigationTab.HOME) }
    var providerSelectedTab by remember { mutableStateOf(ProviderNavigationTab.DASHBOARD) }
    var isProviderMode by remember { mutableStateOf(false) }
    var showCreateBillScreen by remember { mutableStateOf(false) }
    var selectedJobForBill by remember { mutableStateOf<com.sevalk.data.models.Job?>(null) }
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Function to switch to provider mode
    val switchToProviderMode = {
        isProviderMode = true
        // If switching from customer profile, go to provider profile (BUSINESS tab)
        providerSelectedTab = if (customerSelectedTab == CustomerNavigationTab.PROFILE) {
            ProviderNavigationTab.BUSINESS
        } else {
            ProviderNavigationTab.DASHBOARD
        }
    }
    
    // Function to switch to customer mode
    val switchToCustomerMode = {
        isProviderMode = false
        customerSelectedTab = if (providerSelectedTab == ProviderNavigationTab.BUSINESS) {
            CustomerNavigationTab.PROFILE
        } else {
            CustomerNavigationTab.HOME
        }
    }

    // Show CreateServiceBillScreen if needed
    if (showCreateBillScreen && selectedJobForBill != null) {
        com.sevalk.presentation.provider.jobs.CreateServiceBillScreen(
            job = selectedJobForBill!!,
            onBackClick = {
                showCreateBillScreen = false
                selectedJobForBill = null
            },
            onConfirmBill = {
                // Handle bill confirmation
                showCreateBillScreen = false
                selectedJobForBill = null
            }
        )
        return
    }

    Scaffold(
        bottomBar = {
            if (isProviderMode) {
                ProviderBottomNavigationBar(
                    selectedTab = providerSelectedTab,
                    onTabSelected = { tab ->
                        providerSelectedTab = tab
                        when (tab) {
                            ProviderNavigationTab.DASHBOARD -> {
                                // Stay on current screen, just update tab
                            }
                            ProviderNavigationTab.JOBS -> {
                                // Navigate to provider jobs screen
                            }
                            ProviderNavigationTab.SCHEDULE -> {
                                // Navigate to provider schedule screen
                            }
                            ProviderNavigationTab.MESSAGES -> {
                                // Navigate to provider messages screen
                            }
                            ProviderNavigationTab.BUSINESS -> {
                                // Navigate to provider business/profile screen
                            }
                        }
                    }
                )
            } else {
                CustomerBottomNavigationBar(
                    selectedTab = customerSelectedTab,
                    onTabSelected = { tab ->
                        customerSelectedTab = tab
                        when (tab) {
                            CustomerNavigationTab.HOME -> {
                                // Stay on current screen, just update tab
                            }
                            CustomerNavigationTab.SEARCH -> {
                                // Navigate to search screen
                            }
                            CustomerNavigationTab.BOOKINGS -> {
                                // Navigate to bookings screen
                            }
                            CustomerNavigationTab.MESSAGES -> {
                                // Navigate to messages screen
                            }
                            CustomerNavigationTab.PROFILE -> {
                                // Navigate to profile screen
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isProviderMode) {
                when (providerSelectedTab) {
                    ProviderNavigationTab.DASHBOARD -> {
                        ProviderHomeScreen(navController = navController)
                    }
                    ProviderNavigationTab.JOBS -> {
                        JobsScreen(
                            onNavigateToCreateBill = { job ->
                                selectedJobForBill = job
                                showCreateBillScreen = true
                            }
                        )
                    }
                    ProviderNavigationTab.SCHEDULE -> {
                        // Schedule screen placeholder
                        ServiceProviderMapScreen()
                    }
                    ProviderNavigationTab.MESSAGES -> {
                        ChatScreen(
                            onChatItemClick = { chatItem ->
                                navController.navigate("inbox/${chatItem.name}")
                            }
                        )
                    }
                    ProviderNavigationTab.BUSINESS -> {
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
                            onLogoutClick = {},
                            onServicesClick = {},
                            onPaymentMethodsClick = {},
                            onPrivacySecurityClick = {},
                            onHelpSupportClick = {},
                            navController = navController,
                            onSwitchToCustomerClick = switchToCustomerMode
                        )
                    }
                }
            } else {
                when (customerSelectedTab) {
                    CustomerNavigationTab.HOME -> {
                        HomeScreen(
                            navController = navController,
                            onSwitchToProvider = switchToProviderMode
                        )
                    }
                    CustomerNavigationTab.SEARCH -> {
                        ServiceProviderMapScreen()
                    }
                    CustomerNavigationTab.BOOKINGS -> {
                        MyBookingsScreen(navController = navController)
                    }
                    CustomerNavigationTab.MESSAGES -> {
                        ChatScreen(
                            onChatItemClick = { chatItem ->
                                navController.navigate("inbox/${chatItem.name}")
                            }
                        )
                    }
                    CustomerNavigationTab.PROFILE -> {
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
                            onSwitchToProviderClick = switchToProviderMode,
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