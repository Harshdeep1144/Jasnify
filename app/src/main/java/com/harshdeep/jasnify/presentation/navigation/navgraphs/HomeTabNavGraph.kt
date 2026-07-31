package com.harshdeep.jasnify.presentation.navigation.navgraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.runtime.LaunchedEffect
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.home.tabs.*

import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeNavGraph(
    mainNavController: NavHostController,
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    eventViewModel: EventViewModel
) {
    composable(route = Screen.HomeTabScreen.Home.route) {
        HomeTab(
            mainNavController = mainNavController,
            onMenuClick = {
                mainNavController.navigate((Screen.EventDetail.route))
            },
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            eventViewModel = eventViewModel
        )
    }

    composable(route = Screen.HomeTabScreen.Checklists.route) {
        ChecklistsTab(
            mainNavController = mainNavController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            eventViewModel = eventViewModel,
            onBackClick = {
                navController.navigate(Screen.HomeTabScreen.Home.route) {
                    popUpTo(Screen.HomeTabScreen.Home.route) { inclusive = true }
                }
            }
        )
    }

    composable(route = Screen.HomeTabScreen.Vendors.route) {
        LaunchedEffect(Unit) { onBottomBarVisibilityChange(true) }
        VendorsTab(mainNavController = mainNavController)
    }

    composable(route = Screen.HomeTabScreen.Inspirations.route) {
        LaunchedEffect(Unit) { onBottomBarVisibilityChange(true) }
        InspirationsTab()
    }

    composable(route = Screen.HomeTabScreen.Profile.route) {
        ProfileTab(
            mainNavController = mainNavController,
            internalNavController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange
        )
    }
}