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
import com.harshdeep.jasnify.presentation.screens.catering.CateringMenuScreen
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.home.HomeScreen
import com.harshdeep.jasnify.presentation.screens.budget.BudgetScreen
import com.harshdeep.jasnify.presentation.screens.home.EventDetailsScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen
import com.harshdeep.jasnify.presentation.screens.venues.LocationScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun NavGraphBuilder.mainAppNavGraph(mainNavController: NavHostController) {
    navigation(
        startDestination = Screen.MainAppScreen.route,
        route = Screen.MainAppGraph.route
    ) {
        // The core Host screen containing the Bottom Navigation Scaffold
        composable(
            route = Screen.MainAppScreen.route,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(500)) }
        ) {
            HomeScreen(mainNavController = mainNavController)
        }

        // --- Event Details Graphs ---
        composable(
            route = Screen.EventDetail.route,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(500)) }
        ) {
            EventDetailsScreen(
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }

        // Venue Feature
        composable(
            route = Screen.VenueRoot.route,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(500)) }
        ) { entry ->
            val savedStateHandle = entry.savedStateHandle
            val selectedLocation by savedStateHandle.getStateFlow("selected_location", "City, State").collectAsState()

            VenueScreen(
                selectedLocation = selectedLocation,
                onVenueClick = { /* Handle venue click */ },
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }

        // Location Selector Feature
        composable(
            route = Screen.LocationSelector.route,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(500)) }
        ) {
            val previousBackStackEntry = mainNavController.previousBackStackEntry

            LocationScreen(
                initialSearches = listOf("Patna", "Delhi", "Mumbai"),
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
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(500)) }
        ) {
            CateringMenuScreen(
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }

        // Budget Feature
        composable(Screen.BudgetRoot.route) {
            BudgetScreen(
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }
    }
}