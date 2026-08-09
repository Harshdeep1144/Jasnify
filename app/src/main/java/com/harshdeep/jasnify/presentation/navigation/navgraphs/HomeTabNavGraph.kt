package com.harshdeep.jasnify.presentation.navigation.navgraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.composable
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.home.tabs.*

import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions

private val tabOrder = listOf(
    Screen.HomeTabScreen.Home.route,
    Screen.HomeTabScreen.Vendors.route,
    Screen.HomeTabScreen.Checklists.route,
    Screen.HomeTabScreen.Guests.route,
    Screen.HomeTabScreen.Profile.route
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun NavGraphBuilder.homeNavGraph(
    mainNavController: NavHostController,
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    eventViewModel: EventViewModel
) {
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        val initialRoute = initialState.destination.route
        val targetRoute = targetState.destination.route
        val initialIndex = tabOrder.indexOf(initialRoute)
        val targetIndex = tabOrder.indexOf(targetRoute)
        
        if (initialIndex != -1 && targetIndex != -1) {
            if (targetIndex > initialIndex) {
                ScreenTransitions.SlideInFromRightTransition
            } else {
                ScreenTransitions.SlideInFromLeftTransition
            }
        } else {
            fadeIn(tween(300))
        }
    }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        val initialRoute = initialState.destination.route
        val targetRoute = targetState.destination.route
        val initialIndex = tabOrder.indexOf(initialRoute)
        val targetIndex = tabOrder.indexOf(targetRoute)
        
        if (initialIndex != -1 && targetIndex != -1) {
            if (targetIndex > initialIndex) {
                ScreenTransitions.SlideOutToLeftTransition
            } else {
                ScreenTransitions.SlideOutToRightTransition
            }
        } else {
            fadeOut(tween(300))
        }
    }

    composable(
        route = Screen.HomeTabScreen.Home.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        HomeTab(
            mainNavController = mainNavController,
            internalNavController = navController,
            onMenuClick = {
                mainNavController.navigate((Screen.EventDetail.route))
            },
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            eventViewModel = eventViewModel
        )
    }

    composable(
        route = Screen.HomeTabScreen.Checklists.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
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

    composable(
        route = Screen.HomeTabScreen.Vendors.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        VendorsTab(
            mainNavController = mainNavController,
            internalNavController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            onChatClick = { vendor ->
                val merchantId = vendor.merchantId.ifBlank { "unknown_merchant" }
                val itemId = vendor.id.ifBlank { "unknown_vendor" }
                mainNavController.navigate("chat_screen/$merchantId/$itemId?itemType=Vendor")
            },
            onBackClick = {
                navController.navigate(Screen.HomeTabScreen.Home.route) {
                    popUpTo(Screen.HomeTabScreen.Home.route) { inclusive = true }
                }
            }
        )
    }

    composable(
        route = Screen.HomeTabScreen.Guests.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        GuestsTab(
            onBottomBarVisibilityChange = onBottomBarVisibilityChange
        )
    }

    composable(
        route = Screen.HomeTabScreen.Profile.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {
        ProfileTab(
            mainNavController = mainNavController,
            internalNavController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange
        )
    }
}