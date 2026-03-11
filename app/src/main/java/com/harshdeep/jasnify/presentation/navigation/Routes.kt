package com.harshdeep.jasnify.presentation.navigation

import androidx.annotation.DrawableRes
import com.harshdeep.jasnify.R

// --- Top-Level Screen Definitions (Routes) ---

sealed class Screen(val route: String) {
    // Helper function for URL encoding the phone number argument
//    fun otpVerificationRoute(phoneNumber: String): String {
//        // Ensure the phone number is safely encoded for the URL parameter
//        val encodedPhoneNumber = java.net.URLEncoder.encode(phoneNumber, "UTF-8")
//        return "otp_verification_screen/$encodedPhoneNumber"
//    }



    // Top-Level Graph Routes
    data object SplashScreen : Screen("splash_screen")
    data object OnboardingGraph : Screen("onboarding_graph")
    data object MainAppGraph : Screen("main_app_graph")




    // Onboarding Screens (Inside OnboardingGraph)
    data object OnboardingType : Screen("onboarding_type_screen")
    data object LoginOrSignUp : Screen("login_or_signup_screen")
    data object EventCreationScreen : Screen("event_creation_screen")
    data object OtpVerification :
        Screen("otp_verification_screen/{phoneNumber}") // Reintroduced with argument



    // Main App Host Screen (Start of MainAppGraph)
    data object MainAppScreen : Screen("main_app_screen")   // for whole home screens



    // --- Bottom Navigation Items  ---
    sealed class HomeTabScreen(val route: String, @DrawableRes val iconResId: Int, val title: String) {
        data object Home : HomeTabScreen("home_tab_root", R.drawable.ic_home, "Home")
        data object Inspirations : HomeTabScreen("inspirations_tab_root", R.drawable.ic_inspirations, "Inspiration")
        data object Checklists : HomeTabScreen("checklists_tab_root", R.drawable.ic_checklists, "Checklist")
        data object Vendors : HomeTabScreen("vendors_tab_root", R.drawable.ic_vendor, "Vendor")
        data object Profile : HomeTabScreen("profile_tab_root", R.drawable.ic_profile, "Profile")
    }

    // --- Home internal Screens (Full Screen / No Bottom Bar) ---
    data object BudgetDetail : Screen("budget_detail_screen")
}