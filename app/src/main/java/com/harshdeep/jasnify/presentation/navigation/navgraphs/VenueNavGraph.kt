package com.harshdeep.jasnify.presentation.navigation.navgraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.venues.LocationScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen

private const val ANIM_DURATION = 400

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun NavGraphBuilder.venueNavGraph(mainNavController: NavHostController) {
    navigation(
        startDestination = Screen.VenueRoot.route,
        route = Screen.VenueGraph.route
    ) {
        composable(
            route = Screen.VenueRoot.route,
            enterTransition = { fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) { entry ->
            // 1. Observe the selected location from SavedStateHandle with a default fallback address
            val savedStateHandle = entry.savedStateHandle
            val selectedLocation by savedStateHandle.getStateFlow("selected_location", "Patna, Bihar").collectAsState()

            VenueScreen(
                selectedLocation = selectedLocation, // Pass the dynamic value to the Composable screen!
                onVenueClick = { venue ->
                    // Navigate to a Venue Detail screen if created later
                },
                onLocationSelectorClick = { mainNavController.navigate(Screen.LocationSelector.route) },
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = Screen.LocationSelector.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) + fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutVertically(targetOffsetY = { it }) + fadeOut() }
        ) {
            val previousBackStackEntry = mainNavController.previousBackStackEntry

            LocationScreen(
                initialSearches = emptyList(),
                currentAddress = previousBackStackEntry?.savedStateHandle?.get<String>("selected_location") ?: "Patna, Bihar",
                onAddressSelected = { selectedAddress ->
                    // 2. Set the address inside the savedStateHandle and pop back safely
                    previousBackStackEntry?.savedStateHandle?.set("selected_location", selectedAddress)
                    mainNavController.popBackStack() // Correct navigation flow (instead of redundant .navigate)
                },
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }
    }
}