package com.sevalk.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Registration : Screen("registration")
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object ProviderDashboard : Screen("provider_dashboard")
    object ServiceList : Screen("service_list")
    object Booking : Screen("booking")
    object Chat : Screen("chat")
    object ProviderProfile : Screen("provider_profile")
    object ProviderHome : Screen("provider_home")
}
