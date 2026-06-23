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

        // --- Modular Feature Graphs ---

        // Venue Feature Graph
        venueNavGraph(mainNavController)

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


        // Budget Feature Graph
        navigation(
            startDestination = Screen.BudgetRoot.route,
            route = Screen.BudgetGraph.route
        ) {
            composable(Screen.BudgetRoot.route) {
                BudgetScreen()
            }
            // Add BudgetCategoryDetail composable here later
        }
    }
}