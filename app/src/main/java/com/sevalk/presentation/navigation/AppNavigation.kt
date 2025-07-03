package com.sevalk.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.sevalk.presentation.splash.SplashScreen
import com.sevalk.presentation.onboarding.OnboardingScreen
import com.sevalk.presentation.customer.home.ServiceProviderMapScreen
import com.sevalk.presentation.customer.booking.BookingScreen

enum class Screen {
    SPLASH,
    ONBOARDING,
    SEARCH,
    BOOKING
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    
    when (currentScreen) {
        Screen.SPLASH -> {
            SplashScreen(
                onNavigateToOnboarding = {
                    currentScreen = Screen.ONBOARDING
                },
                modifier = modifier
            )
        }
        
        Screen.ONBOARDING -> {
            OnboardingScreen(
                onGetStarted = {
                    currentScreen = Screen.SEARCH
                },
                modifier = modifier
            )
        }
        
        Screen.SEARCH -> {
            ServiceProviderMapScreen(
                onNavigateToBooking = {
                    currentScreen = Screen.BOOKING
                }
            )
        }
        
        Screen.BOOKING -> {
            BookingScreen(
                onNavigateBack = {
                    currentScreen = Screen.SEARCH
                },
                modifier = modifier
            )
        }
    }
}
