package com.harshdeep.jasnify.presentation.navigation.navgraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.harshdeep.jasnify.presentation.screens.others.ChatScreen
import com.harshdeep.jasnify.presentation.screens.catering.CateringMenuScreen
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.home.HomeScreen
import com.harshdeep.jasnify.presentation.screens.budget.BudgetScreen
import com.harshdeep.jasnify.presentation.screens.home.EventDetailsScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen
import com.harshdeep.jasnify.presentation.screens.venues.LocationScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import androidx.hilt.navigation.compose.hiltViewModel

object NavAnimations {
    private const val DURATION = 250
    val slideInFromRight: EnterTransition =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(DURATION)
        ) + fadeIn(animationSpec = tween(DURATION))

    val slideOutToLeft: ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(DURATION)
        )

    val slideInFromLeft: EnterTransition =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(DURATION)
        ) + fadeIn(animationSpec = tween(DURATION))

    val slideOutToRight: ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(DURATION)
        )
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun NavGraphBuilder.mainAppNavGraph(mainNavController: NavHostController) {
    navigation(
        startDestination = Screen.MainAppScreen.route,
        route = Screen.MainAppGraph.route
    ) {
        // The core Host screen containing the Bottom Navigation Scaffold
        composable(
            route = Screen.MainAppScreen.route + "?eventId={eventId}",
            arguments = listOf(
                androidx.navigation.navArgument("eventId") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(500)) }
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            HomeScreen(mainNavController = mainNavController, joinedEventId = eventId)
        }

        // --- Event Details Graphs ---
        composable(
            route = Screen.EventDetail.route,
            enterTransition = { NavAnimations.slideInFromLeft },
            exitTransition = { NavAnimations.slideOutToRight },
            popEnterTransition = { NavAnimations.slideInFromRight },
            popExitTransition = { NavAnimations.slideOutToLeft }
        ) { entry ->
            val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
            val eventViewModel: EventViewModel = hiltViewModel(graphEntry)
            EventDetailsScreen(
                onBackClick = {
                    if (mainNavController.popBackStack()) {
                        // success
                    }
                },
                onReviewVenues = {
                    mainNavController.navigate("venue_root_screen?tab=saved")
                },
                onReviewVendors = {
                    // Navigate to vendors tab if needed, but the request says saved section in venue screen
                    mainNavController.navigate("venue_root_screen?tab=saved")
                },
                eventViewModel = eventViewModel
            )
        }

        // Venue Feature
        composable(
            route = Screen.VenueRoot.route,
            arguments = listOf(
                androidx.navigation.navArgument("tab") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = "explore"
                }
            )
        ) { entry ->
            val savedStateHandle = entry.savedStateHandle
            val selectedLocation by savedStateHandle.getStateFlow("selected_location", "City, State").collectAsState()
            val initialTab = entry.arguments?.getString("tab") ?: "explore"

            val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
            val eventViewModel: EventViewModel = hiltViewModel(graphEntry)

            VenueScreen(
                selectedLocation = selectedLocation,
                initialTab = initialTab,
                onVenueClick = { /* Handle venue click */ },
                onChatClick = { venue ->
                    val merchantId = venue.merchantId.ifBlank { "unknown_merchant" }
                    val venueId = venue.id.ifBlank { "unknown_venue" }
                    mainNavController.navigate("chat_screen/$merchantId/$venueId")
                },
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                },
                eventViewModel = eventViewModel
            )
        }

        // Messaging
        composable(
            route = Screen.ChatScreen.route,
            arguments = listOf(
                androidx.navigation.navArgument("merchantId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("venueId") { type = androidx.navigation.NavType.StringType }
            )
        ) { entry ->
            val merchantId = entry.arguments?.getString("merchantId")
            val venueId = entry.arguments?.getString("venueId")
            ChatScreen(
                merchantId = merchantId,
                venueId = venueId,
                onBackClick = { mainNavController.popBackStack() }
            )
        }

        // Location Selector Feature
        composable(
            route = Screen.LocationSelector.route,
        ) {
            val previousBackStackEntry = mainNavController.previousBackStackEntry

            LocationScreen(
                initialSearches = emptyList(),
                currentAddress = previousBackStackEntry?.savedStateHandle?.get<String>("selected_location") ?: "City, State",
                onAddressSelected = { selectedAddress ->
                    previousBackStackEntry?.savedStateHandle?.set("selected_location", selectedAddress)
                    mainNavController.popBackStack()
                },
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }

        // Catering Feature
        composable(
            route = Screen.CateringRoot.route,
        ) { entry ->
            val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
            val eventViewModel: EventViewModel = hiltViewModel(graphEntry)
            
            CateringMenuScreen(
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                },
                eventViewModel = eventViewModel
            )
        }

        // Budget Feature
        composable(Screen.BudgetRoot.route) { entry ->
            val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
            val eventViewModel: EventViewModel = hiltViewModel(graphEntry)
            
            BudgetScreen(
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                },
                eventViewModel = eventViewModel
            )
        }
    }
}
