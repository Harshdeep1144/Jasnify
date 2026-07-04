package com.harshdeep.jasnify.presentation.navigation.navgraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.runtime.LaunchedEffect
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.home.tabs.*

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeNavGraph(
    mainNavController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit
) {
    composable(route = Screen.HomeTabScreen.Home.route) {
        HomeTab(
            onMenuClick = {
                mainNavController.navigate((Screen.EventDetail.route))
            },
            onBottomBarVisibilityChange = onBottomBarVisibilityChange
        )
    }

    composable(route = Screen.HomeTabScreen.Checklists.route) {
        LaunchedEffect(Unit) { onBottomBarVisibilityChange(true) }
        ChecklistsTab()
    }

    composable(route = Screen.HomeTabScreen.Vendors.route) {
        LaunchedEffect(Unit) { onBottomBarVisibilityChange(true) }
        VendorsTab()
    }

    composable(route = Screen.HomeTabScreen.Inspirations.route) {
        LaunchedEffect(Unit) { onBottomBarVisibilityChange(true) }
        InspirationsTab()
    }

    composable(route = Screen.HomeTabScreen.Profile.route) {
        LaunchedEffect(Unit) { onBottomBarVisibilityChange(true) }
        ProfileTab(mainNavController = mainNavController)
    }
}