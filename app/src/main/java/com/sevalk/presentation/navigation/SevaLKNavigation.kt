package com.sevalk.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sevalk.domain.payment.PaymentViewModel
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
import com.sevalk.presentation.customer.payment.PaymentSuccessScreen
import com.sevalk.presentation.splash.SplashScreen

@Composable
fun SevaLKNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen - Entry point
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Flow
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
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
        
        composable(Screen.Booking.route) {
            BookingScreen(
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
            val paymentViewModel = hiltViewModel<PaymentViewModel>()
            PaymentScreen(
                viewModel = paymentViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onPaymentSuccess = {
                    navController.navigate("payment_success") {
                        popUpTo("payment") { inclusive = true }
                    }
                }
            )
        }
        
        composable("payment_success") {
            PaymentSuccessScreen(
                amount = 2500.0,
                onBackToHome = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }


}
