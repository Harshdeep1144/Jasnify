package com.harshdeep.jasnify.presentation.navigation

import com.harshdeep.jasnify.R

sealed class Screen(val route: String) {
    // Helper function to create the actual route string
    fun otpVerificationRoute(phoneNumber: String): String {
        val encodedPhoneNumber = java.net.URLEncoder.encode(phoneNumber, "UTF-8")
        return "otp_verification/$encodedPhoneNumber"
    }

    object SplashScreen : Screen("splash_screen")
    object OnboardingType : Screen("onboarding_type")
    object LoginOrSignUp : Screen("login_or_signup")
    object OtpVerification : Screen("otp_verification/{phoneNumber}")
    object EventCreationScreen : Screen("event_create")
    object HomeScreen : Screen("main_home_flow")

}


sealed class HomeTabScreen(val route: String, val title: String, val iconResId: Int) {
    object Home : HomeTabScreen("home_dashboard", "Home", R.drawable.ic_home)
    object Checklists : HomeTabScreen("home_checklists", "Checklists", R.drawable.ic_checklists)
    object Vendors : HomeTabScreen("home_vendors", "Vendors", R.drawable.ic_vendor)
    object Inspirations : HomeTabScreen("home_inspirations", "Inspirations", R.drawable.ic_inspirations)
    object Profile : HomeTabScreen("home_profile", "Profile", R.drawable.ic_profile)
}