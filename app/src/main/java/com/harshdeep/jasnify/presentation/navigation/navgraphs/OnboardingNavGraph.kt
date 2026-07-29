package com.harshdeep.jasnify.presentation.navigation.navgraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.screens.onboarding.OnboardingType
import com.harshdeep.jasnify.presentation.screens.onboarding.authentication.LoginOrSignup
import com.harshdeep.jasnify.presentation.screens.onboarding.EventCreation

// Custom Transition Duration for a smooth onboarding experience
private const val TRANSITION_DURATION = 450

// Shared animations for onboarding screens
private val smoothSlideInFromRight = slideInHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    initialOffsetX = { it / 4 }
) + fadeIn(tween(TRANSITION_DURATION))

private val smoothSlideOutToLeft = slideOutHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    targetOffsetX = { -it / 4 }
) + fadeOut(tween(TRANSITION_DURATION))

private val smoothSlideInFromLeft = slideInHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    initialOffsetX = { -it / 4 }
) + fadeIn(tween(TRANSITION_DURATION))

private val smoothSlideOutToRight = slideOutHorizontally(
    animationSpec = tween(TRANSITION_DURATION),
    targetOffsetX = { it / 4 }
) + fadeOut(tween(TRANSITION_DURATION))

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.onboardingNavGraph(mainNavController: NavHostController) {
    navigation(
        startDestination = Screen.OnboardingType.route,
        route = Screen.OnboardingGraph.route
    ) {
        // 1. Selection Screen: User chooses role (Host/Vendor)
        composable(
            route = Screen.OnboardingType.route,
            enterTransition = { smoothSlideInFromRight },
            exitTransition = { smoothSlideOutToLeft },
            popEnterTransition = { smoothSlideInFromLeft },
            popExitTransition = { smoothSlideOutToRight }
        ) {
            OnboardingType(mainNavController)
        }

        // 2. Authentication Screen: Login or Signup
        composable(
            route = Screen.LoginOrSignUp.route,
            arguments = listOf(
                androidx.navigation.navArgument("eventId") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { smoothSlideInFromRight },
            exitTransition = { smoothSlideOutToLeft },
            popEnterTransition = { smoothSlideInFromLeft },
            popExitTransition = { smoothSlideOutToRight }
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            LoginOrSignup(mainNavController, eventId)
        }

        // 3. Event Creation: Setup the first wedding/event details
        composable(
            route = Screen.EventCreationScreen.route,
            arguments = listOf(
                androidx.navigation.navArgument("fromProfile") {
                    type = androidx.navigation.NavType.BoolType
                    defaultValue = false
                }
            ),
            enterTransition = { smoothSlideInFromRight },
            exitTransition = { smoothSlideOutToLeft },
            popEnterTransition = { smoothSlideInFromLeft },
            popExitTransition = { smoothSlideOutToRight }
        ) { backStackEntry ->
            val fromProfile = backStackEntry.arguments?.getBoolean("fromProfile") ?: false
            EventCreation(mainNavController, fromProfile = fromProfile)
        }

        // 4. OTP Verification (Optional/Nested inside Auth flow)
        composable(
            route = Screen.OtpVerification.route,
            enterTransition = { smoothSlideInFromRight },
            exitTransition = { smoothSlideOutToLeft },
            popEnterTransition = { smoothSlideInFromLeft },
            popExitTransition = { smoothSlideOutToRight }
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            // OtpVerificationScreen(phoneNumber, mainNavController)
        }
    }
}