package com.harshdeep.jasnify.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.harshdeep.jasnify.presentation.screens.onboarding.OnboardingType
import com.harshdeep.jasnify.presentation.screens.onboarding.authentication.LoginOrSignup
import com.harshdeep.jasnify.presentation.screens.onboarding.EventCreation
import com.harshdeep.jasnify.presentation.screens.home.HomeScreen

// Increased duration for a smoother, less abrupt feel
private const val TRANSITION_DURATION = 450

// Reverting to the combined Slide and Fade, but with a smoother duration
private val smoothSlideInFromRight = slideInHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    initialOffsetX = { it / 4 } // Starts closer to the screen for a less dramatic slide
) + fadeIn(tween(TRANSITION_DURATION))

private val smoothSlideOutToLeft = slideOutHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    targetOffsetX = { -it / 4 } // Exits slower
) + fadeOut(tween(TRANSITION_DURATION))

private val smoothSlideInFromLeft = slideInHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    initialOffsetX = { -it / 4 }
) + fadeIn(tween(TRANSITION_DURATION))

private val smoothSlideOutToRight = slideOutHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    targetOffsetX = { it / 4 }
) + fadeOut(tween(TRANSITION_DURATION))

// ------------------------------------------

// ONBOARDING NAVIGATION GRAPH
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.onboardingNavGraph(mainNavController: NavHostController) {
    navigation(
        startDestination = Screen.OnboardingType.route,
        route = Screen.OnboardingGraph.route
    ) {
        composable(
            route = Screen.OnboardingType.route,
            // Using the smoother, combined transitions
            enterTransition = { smoothSlideInFromRight },
            exitTransition = { smoothSlideOutToLeft },
            popEnterTransition = { smoothSlideInFromLeft },
            popExitTransition = { smoothSlideOutToRight }
        ) {
            OnboardingType(mainNavController)
        }

        composable(
            route = Screen.LoginOrSignUp.route,
            // Using the smoother, combined transitions
            enterTransition = { smoothSlideInFromRight },
            exitTransition = { smoothSlideOutToLeft },
            popEnterTransition = { smoothSlideInFromLeft },
            popExitTransition = { smoothSlideOutToRight }
        ) {
            LoginOrSignup(mainNavController) // Must navigate to MainAppGraph on success
        }

        composable(
            route = Screen.EventCreationScreen.route,
            enterTransition = { smoothSlideInFromRight },
            exitTransition = { smoothSlideOutToLeft },
            popEnterTransition = { smoothSlideInFromLeft },
            popExitTransition = { smoothSlideOutToRight }
        ) {
            EventCreation(mainNavController) // Must navigate to MainAppGraph on complete
        }
    }
}

// ------------------------------------------

// MAIN APP NAVIGATION GRAPH
fun NavGraphBuilder.mainAppNavGraph(mainNavController: NavHostController) {
    navigation(
        startDestination = Screen.MainAppScreen.route,
        route = Screen.MainAppGraph.route
    ) {
        composable(
            route = Screen.MainAppScreen.route,
            // Keeping the Fade for the main screen is still the most professional choice
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
            HomeScreen(mainNavController = mainNavController)
        }
    }
}