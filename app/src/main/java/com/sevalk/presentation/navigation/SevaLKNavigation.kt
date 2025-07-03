package com.sevalk.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sevalk.presentation.auth.login.LoginScreen
import com.sevalk.presentation.auth.registration.RegistrationScreen
import com.sevalk.presentation.auth.welcome.WelcomeScreen
import com.sevalk.presentation.chat.ChatScreen
import com.sevalk.presentation.customer.booking.BookingScreen
import com.sevalk.presentation.customer.home.HomeScreen
import com.sevalk.presentation.customer.services.ServiceListScreen
import com.sevalk.presentation.onboarding.OnboardingScreen
import com.sevalk.presentation.provider.dashboard.ProviderDashboardScreen
import com.sevalk.presentation.provider.jobs.JobsScreen
import com.sevalk.presentation.provider.location.SetLocationScreen
import com.sevalk.presentation.provider.profile.ProviderProfile
import com.sevalk.presentation.provider.profile.ProviderProfileScreen
import com.sevalk.presentation.provider.home.ProviderHomeScreen

@Composable
fun SevaLKNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Flow
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.Registration.route)
                },
                onLoginSuccess = {
                    // Navigate based on user type
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Registration.route) {
            RegistrationScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        
        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Customer Flow
        composable(Screen.Home.route) {
            MainNavigation(
                navController = navController
            )
        }
        
        composable(Screen.ServiceList.route) {
            ServiceListScreen(

            )
        }
        
        composable(Screen.Booking.route) {
            BookingScreen(

            )
        }
        
        // Provider Flow
        composable(Screen.CustomerHome.route) {
            HomeScreen(
                navController = navController
            )
        }
        
        composable(Screen.ProviderProfile.route) {
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
                )
                ,
                onSwitchToCustomerClick = { true },
                onLogoutClick = { /* Handle logout */ },
                onServicesClick = { /* Handle services */ },
                onPaymentMethodsClick = { /* Handle payments */ },
                onPrivacySecurityClick = { /* Handle privacy */ },
                onHelpSupportClick = { /* Handle help */ }
            )
        }
        
        // Shared Screens
        composable(Screen.Chat.route) {
            ChatScreen(
                navController = navController
            )
        }

        composable(Screen.ProviderHome.route) {
            ProviderHomeScreen(navController = navController)
        }
    }
}
