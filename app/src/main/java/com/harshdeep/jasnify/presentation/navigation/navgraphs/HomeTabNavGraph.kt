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
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions
import com.harshdeep.jasnify.presentation.screens.main.tabs.checklist.ChecklistsTab
import com.harshdeep.jasnify.presentation.screens.main.tabs.guests.GuestsTab
import com.harshdeep.jasnify.presentation.screens.main.tabs.home.HomeTab
import com.harshdeep.jasnify.presentation.screens.main.tabs.profile.ProfileTab
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorsTab

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
    ) { entry ->
        val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
        HomeTab(
            mainNavController = mainNavController,
            internalNavController = navController,
            onMenuClick = {
                mainNavController.navigate((Screen.EventDetail.route))
            },
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            eventViewModel = hiltViewModel(graphEntry),
            budgetViewModel = hiltViewModel(graphEntry),
            venueViewModel = hiltViewModel(graphEntry),
            vendorViewModel = hiltViewModel(graphEntry),
            roomViewModel = hiltViewModel(graphEntry)
        )
    }

    composable(
        route = Screen.HomeTabScreen.Checklists.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) { entry ->
        val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
        ChecklistsTab(
            mainNavController = mainNavController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            viewModel = hiltViewModel(graphEntry),
            eventViewModel = hiltViewModel(graphEntry),
            roomViewModel = hiltViewModel(graphEntry),
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
    ) { entry ->
        val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
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
            },
            eventViewModel = hiltViewModel(graphEntry),
            roomViewModel = hiltViewModel(graphEntry),
            vendorViewModel = hiltViewModel(graphEntry)
        )
    }

    composable(
        route = Screen.HomeTabScreen.Guests.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) { entry ->
        val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
        GuestsTab(
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            roomViewModel = hiltViewModel(graphEntry),
            eventViewModel = hiltViewModel(graphEntry),
            guestViewModel = hiltViewModel(graphEntry),
            onBackClick = {
                navController.navigate(Screen.HomeTabScreen.Home.route) {
                    popUpTo(Screen.HomeTabScreen.Home.route) { inclusive = true }
                }
            }
        )
    }

    composable(
        route = Screen.HomeTabScreen.Profile.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) { entry ->
        val graphEntry = remember(entry) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
        ProfileTab(
            mainNavController = mainNavController,
            internalNavController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            authViewModel = hiltViewModel(graphEntry),
            profileViewModel = hiltViewModel(graphEntry),
            eventViewModel = hiltViewModel(graphEntry),
            enquiryViewModel = hiltViewModel(graphEntry)
        )
    }
}