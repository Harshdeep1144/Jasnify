package com.harshdeep.jasnify.presentation.navigation

import androidx.annotation.DrawableRes
import com.harshdeep.jasnify.R

sealed class Screen(val route: String) {

    // --- Top-Level Graph Routes ---
    data object SplashScreen : Screen("splash_graph")
    data object OnboardingGraph : Screen("onboarding_graph")
    data object MainAppGraph : Screen("main_app_graph")

    // --- Feature Graph Routes ---
    data object VenueGraph : Screen("venue_graph")

    // --- Onboarding Screens ---
    data object OnboardingType : Screen("onboarding_type_screen")
    data object LoginOrSignUp : Screen("login_or_signup_screen?eventId={eventId}")
    data object EventCreationScreen : Screen("event_creation_screen?fromProfile={fromProfile}")
    data object OtpVerification : Screen("otp_verification_screen/{phoneNumber}")

    // --- Main App Host (Contains Bottom Nav) ---
    data object MainAppScreen : Screen("main_app_host_screen")

    // --- Bottom Navigation Tab Items ---
    sealed class HomeTabScreen(val route: String, @DrawableRes val iconResId: Int, val title: String) {
        data object Home : HomeTabScreen("home_tab_root", R.drawable.ic_home, "Home")
        data object Guests : HomeTabScreen("guests_tab_root", R.drawable.ic_guests, "Guests")
        data object Checklists : HomeTabScreen("checklists_tab_root", R.drawable.ic_checklists, "Checklist")
        data object Vendors : HomeTabScreen("vendors_tab_root", R.drawable.ic_vendor, "Vendor")
        data object Profile : HomeTabScreen("profile_tab_root", R.drawable.ic_profile, "Profile")
    }

    // --- Feature Specific Screens (Inside their respective graphs) ---

    // Event Details Screen
    data object EventDetail : Screen("even_detail_screen")

    // Venue Feature
    data object VenueRoot : Screen("venue_root_screen?tab={tab}")
    data object VenueDetail : Screen("venue_detail_screen/{venueId}")
    data object LocationSelector : Screen("location_selector_screen")

    // Vendor Feature
    data object VendorDetail : Screen("vendor_detail_screen/{vendorId}")

    // Budget Feature
    data object BudgetRoot : Screen("budget_root_screen")

    // Catering Feature
    data object CateringRoot : Screen("catering_root_screen")

    // Cards Feature
    data object CardsRoot : Screen("cards_root_screen")

    // Messaging
    data object ChatScreen : Screen("chat_screen/{merchantId}/{itemId}?itemType={itemType}")
}