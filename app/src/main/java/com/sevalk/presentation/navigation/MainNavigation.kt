package com.sevalk.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sevalk.data.models.Job
import com.sevalk.presentation.auth.AuthState
import com.sevalk.presentation.auth.AuthViewModel
import com.sevalk.presentation.chat.ChatScreen
import com.sevalk.presentation.customer.booking.MyBookingsScreen
import com.sevalk.presentation.customer.home.HomeScreen
import com.sevalk.presentation.customer.search.ServiceProviderMapScreen
import com.sevalk.presentation.customer.profile.CustomerProfileScreen
import com.sevalk.presentation.provider.home.ProviderHomeScreen
import com.sevalk.presentation.provider.jobs.CreateServiceBillScreen
import com.sevalk.presentation.provider.jobs.JobsScreen
import com.sevalk.presentation.provider.profile.ProviderProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by remember { derivedStateOf { authViewModel.authState } }
    val isProviderMode by remember { derivedStateOf { authViewModel.isProviderMode } }

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            AuthState.UNAUTHENTICATED -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
            AuthState.FIRST_TIME_USER -> {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
            else -> {
                // Stay on current screen
            }
        }
    }

    var customerSelectedTab by remember { mutableStateOf(CustomerNavigationTab.HOME) }
    var providerSelectedTab by remember { mutableStateOf(ProviderNavigationTab.DASHBOARD) }
    var showCreateBillScreen by remember { mutableStateOf(false) }
    var selectedJobForBill by remember { mutableStateOf<Job?>(null) }
    var selectedServiceType by remember { mutableStateOf<com.sevalk.presentation.components.map.ServiceType?>(null) }
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Function to switch to provider mode
    val switchToProviderMode = {
        authViewModel.updateProviderMode(true)
        // If switching from customer profile, go to provider profile (BUSINESS tab)
        providerSelectedTab = if (customerSelectedTab == CustomerNavigationTab.PROFILE) {
            ProviderNavigationTab.BUSINESS
        } else {
            ProviderNavigationTab.DASHBOARD
        }
    }
    
    // Function to switch to customer mode
    val switchToCustomerMode = {
        authViewModel.updateProviderMode(false)
        customerSelectedTab = if (providerSelectedTab == ProviderNavigationTab.BUSINESS) {
            CustomerNavigationTab.PROFILE
        } else {
            CustomerNavigationTab.HOME
        }
    }

    // Show CreateServiceBillScreen if needed
    if (showCreateBillScreen && selectedJobForBill != null) {
        CreateServiceBillScreen(
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
                        if (tab == CustomerNavigationTab.SEARCH) {
                            selectedServiceType = null // Reset filter if user taps tab
                        }
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
                        ProviderHomeScreen(navController = navController, onSwitchToCustomer = switchToCustomerMode)
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
                        ServiceProviderMapScreen(navController = navController)
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
                            navController = navController,
                            onLogoutClick = {
                                authViewModel.signOut()
                            },
                            onServicesClick = {
                                navController.navigate("provider/services")
                            },
                            onPaymentMethodsClick = {
                                navController.navigate("provider/payments")
                            },
                            onPrivacySecurityClick = {},
                            onHelpSupportClick = {},
                            onSwitchToCustomerClick = switchToCustomerMode
                        )
                    }
                }
            } else {
                when (customerSelectedTab) {
                    CustomerNavigationTab.HOME -> {
                        HomeScreen(
                            navController = navController,
                            onSwitchToProvider = switchToProviderMode,
                            onServiceSelected = { serviceType ->
                                selectedServiceType = serviceType
                                customerSelectedTab = CustomerNavigationTab.SEARCH
                            }
                        )
                    }
                    CustomerNavigationTab.SEARCH -> {
                        ServiceProviderMapScreen(
                            navController = navController,
                            initialServiceType = selectedServiceType
                        )
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
                            onSwitchToProviderClick = switchToProviderMode,
                            onLogoutClick = {
                                authViewModel.signOut()
                            },
                            onFavoritesClick = {},
                            onPaymentMethodsClick = {  },
                            onPrivacySecurityClick = {  },
                            onHelpSupportClick = {  }
                        )
                    }
                }
            }
        }
    }
}