package com.sevalk.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sevalk.presentation.provider.jobs.JobsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    
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
                    // Home screen content - for now showing JobsScreen
                    JobsScreen()
                }
                NavigationTab.SEARCH -> {
                    // Search screen placeholder
                    SearchScreen()
                }
                NavigationTab.BOOKINGS -> {
                    // Bookings screen placeholder  
                    BookingsScreen()
                }
                NavigationTab.MESSAGES -> {
                    // Messages screen placeholder
                    MessagesScreen()
                }
                NavigationTab.PROFILE -> {
                    // Profile screen placeholder
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun SearchScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Search Screen")
    }
}

@Composable
fun BookingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Bookings Screen")
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
