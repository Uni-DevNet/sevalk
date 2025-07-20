package com.sevalk.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sevalk.presentation.auth.google.UserTypeSelectionScreen
import com.sevalk.presentation.auth.login.LoginScreen
import com.sevalk.presentation.auth.registration.RegistrationScreen
import com.sevalk.presentation.auth.welcome.WelcomeScreen
import com.sevalk.presentation.chat.ChatScreen
import com.sevalk.presentation.chat.InboxScreen
import com.sevalk.presentation.customer.booking.BookingDetailsScreen
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
import com.sevalk.presentation.provider.service.ServiceSelectionScreen
import com.sevalk.presentation.customer.payment.PaymentScreen

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
                },
                onNavigateToUserTypeSelection = { email, name ->
                    navController.navigate(Screen.UserTypeSelection.createRoute(email, name))
                }
            )
        }
        
        composable(Screen.Registration.route) {
            RegistrationScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToServiceSelection = {
                    navController.navigate(Screen.ServiceSelection.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                },
                onNavigateToUserTypeSelection = { email, name ->
                    navController.navigate(Screen.UserTypeSelection.createRoute(email, name))
                }
            )
        }
        
        composable(Screen.UserTypeSelection.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            UserTypeSelectionScreen(
                userEmail = email,
                userName = name,
                onNavigateToServiceSelection = {
                    navController.navigate(Screen.ServiceSelection.route) {
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ServiceSelection.route) {
            ServiceSelectionScreen(
                onNavigateToNext = {
                    navController.navigate(Screen.SetLocation.route)
                }
            )
        }
        
        composable(Screen.SetLocation.route) {
            SetLocationScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
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
        
        composable(
            Screen.Booking.route,
            arguments = listOf(
                navArgument("providerId") { nullable = true },
                navArgument("providerName") { nullable = true },
                navArgument("rating") { nullable = true },
                navArgument("serviceType") { nullable = true },
                navArgument("hourlyRate") { nullable = true },
                navArgument("completedJobs") { nullable = true }
            )
        ) { backStackEntry ->
            BookingScreen(
                providerId = backStackEntry.arguments?.getString("providerId"),
                providerName = backStackEntry.arguments?.getString("providerName"),
                rating = backStackEntry.arguments?.getString("rating")?.toFloatOrNull(),
                serviceType = backStackEntry.arguments?.getString("serviceType"),
                hourlyRate = backStackEntry.arguments?.getString("hourlyRate")?.toFloatOrNull(),
                completedJobs = backStackEntry.arguments?.getString("completedJobs")?.toIntOrNull(),
                onNavigateBack = {
                    navController.popBackStack()
                }
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
                ),
                onLogoutClick = {},
                onServicesClick = {},
                onPaymentMethodsClick = {},
                onPrivacySecurityClick = {},
                onHelpSupportClick = {},
                navController = navController,
                onSwitchToCustomerClick = {}
            )
        }
        
        // Shared Screens
        composable(Screen.Chat.route) {
            ChatScreen(
                onChatItemClick = { chatItem ->
                    navController.navigate("inbox/${chatItem.name}")
                }
            )
        }

        composable(Screen.ProviderHome.route) {
            ProviderHomeScreen(navController = navController)
        }

        composable("inbox/{contactName}") { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName") ?: ""
            InboxScreen(
                contactName = contactName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("booking_details/{bookingId}") { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailsScreen(
                navController = navController,
                bookingId = bookingId
            )
        }

        composable("payment/{bookingId}") { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            PaymentScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPayClick = { paymentMethod, cardDetails ->
                    // Handle payment completion - navigate back to bookings or show success
                    navController.navigate("home") {
                        popUpTo("booking") { inclusive = true }
                    }
                }
            )
        }
    }
}
