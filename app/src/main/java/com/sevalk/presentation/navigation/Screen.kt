package com.sevalk.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Registration : Screen("registration")
    object UserTypeSelection : Screen("user_type_selection/{email}/{name}") {
        fun createRoute(email: String, name: String) = "user_type_selection/$email/$name"
    }
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object CustomerHome : Screen("CustomerHome")
    object ServiceList : Screen("service_list")
    object Booking : Screen("booking")
    object Chat : Screen("chat")
    object Inbox : Screen("inbox")
    object ProviderProfile : Screen("provider_profile")
    object ProviderHome : Screen("provider_home")
    object ServiceSelection : Screen("service_selection")
    object SetLocation : Screen("set_location")
    object CreateServiceBill : Screen("create_service_bill")
    object BookingDetails : Screen("booking_details/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_details/$bookingId"
    }
}
