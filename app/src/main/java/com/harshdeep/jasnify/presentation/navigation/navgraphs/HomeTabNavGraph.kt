package com.harshdeep.jasnify.presentation.navigation.navgraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.home.tabs.*

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeNavGraph(
    mainNavController: NavHostController,
    internalNavController: NavHostController
) {
    composable(route = Screen.HomeTabScreen.Home.route) {
        HomeTab(
            onBudgetClick = {
                // Navigate to the Budget FEATURE GRAPH
                mainNavController.navigate(Screen.BudgetGraph.route)
            },
            onVenueClick = {
                // Navigate to the Venue FEATURE GRAPH
                mainNavController.navigate(Screen.VenueGraph.route)
            },
            onCateringClick = {
                mainNavController.navigate((Screen.CateringRoot.route))
            }
        )
    }

    composable(route = Screen.HomeTabScreen.Checklists.route) {
        ChecklistsTab()
    }

    composable(route = Screen.HomeTabScreen.Vendors.route) {
        VendorsTab()
    }

    composable(route = Screen.HomeTabScreen.Inspirations.route) {
        InspirationsTab()
    }

    composable(route = Screen.HomeTabScreen.Profile.route) {
        ProfileTab(mainNavController = mainNavController)
    }
}