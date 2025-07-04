package com.sevalk.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sevalk.presentation.customer.home.HomeScreen
import com.sevalk.presentation.provider.home.ProviderHomeScreen

@Composable
fun MainNavigationHost(
    navController: NavHostController = rememberNavController()
) {
    var isProviderMode by remember { mutableStateOf(false) }
    var customerSelectedTab by remember { mutableStateOf(CustomerNavigationTab.HOME) }
    var providerSelectedTab by remember { mutableStateOf(ProviderNavigationTab.DASHBOARD) }
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Update mode based on current route
    LaunchedEffect(currentRoute) {
        isProviderMode = currentRoute == Screen.ProviderHome.route
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
                                if (currentRoute != Screen.ProviderHome.route) {
                                    navController.navigate(Screen.ProviderHome.route) {
                                        popUpTo(Screen.Home.route) { inclusive = false }
                                    }
                                }
                            }
                            // Add other provider tab navigation here
                            else -> { /* Handle other provider tabs */ }
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
                                if (currentRoute != Screen.Home.route) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.ProviderHome.route) { inclusive = false }
                                    }
                                }
                            }
                            // Add other customer tab navigation here
                            else -> { /* Handle other customer tabs */ }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                isProviderMode = false
                customerSelectedTab = CustomerNavigationTab.HOME
                HomeScreen(navController = navController)
            }
            
            composable(Screen.ProviderHome.route) {
                isProviderMode = true
                providerSelectedTab = ProviderNavigationTab.DASHBOARD
                ProviderHomeScreen(navController = navController)
            }
            
            // Add other screen routes here
        }
    }
}
