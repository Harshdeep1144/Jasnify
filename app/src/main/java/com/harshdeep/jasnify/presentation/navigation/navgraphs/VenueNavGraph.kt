package com.harshdeep.jasnify.presentation.navigation.navgraphs

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.venues.LocationScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen

private const val ANIM_DURATION = 400

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
        ) {
            VenueScreen(
                onVenueClick = { venue ->
                    // Navigate to a Venue Detail screen if create later
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
            LocationScreen(
                initialSearches = listOf("Patna", "New Delhi", "Mumbai", "Haryana", "Noida", "Pune"),
                onBackClick = {
                    if (mainNavController.previousBackStackEntry != null) {
                        mainNavController.popBackStack()
                    }
                }
            )
        }
    }
}