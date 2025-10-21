package com.harshdeep.jasnify.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.screens.home.HomeScreen
import com.harshdeep.jasnify.presentation.screens.onboarding.SplashScreen
import com.harshdeep.jasnify.presentation.screens.onboarding.OnboardingType
import com.harshdeep.jasnify.presentation.screens.onboarding.authentication.LoginOrSignup
//import com.harshdeep.jasnify.presentation.screens.onboarding.authentication.OtpVerification
import com.harshdeep.jasnify.presentation.screens.onboarding.EventCreation
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel

// Standard duration for a fast, modern transition (e.g., 300ms)
private const val TRANSITION_DURATION = 300

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        // --- Shared Transition Logic ---
        val slideInFromRight = slideInHorizontally(
            animationSpec = tween(TRANSITION_DURATION),
            initialOffsetX = { fullWidth -> fullWidth } // Screen starts off-screen to the right
        ) + fadeIn(tween(TRANSITION_DURATION))

        val slideOutToLeft = slideOutHorizontally(
            animationSpec = tween(TRANSITION_DURATION),
            targetOffsetX = { fullWidth -> -fullWidth } // Screen moves off-screen to the left
        ) + fadeOut(tween(TRANSITION_DURATION))

        val slideInFromLeft = slideInHorizontally(
            animationSpec = tween(TRANSITION_DURATION),
            initialOffsetX = { fullWidth -> -fullWidth } // Screen starts off-screen to the left
        ) + fadeIn(tween(TRANSITION_DURATION))

        val slideOutToRight = slideOutHorizontally(
            animationSpec = tween(TRANSITION_DURATION),
            targetOffsetX = { fullWidth -> fullWidth } // Screen moves off-screen to the right
        ) + fadeOut(tween(TRANSITION_DURATION))

        // SplashScreen (Handles which screen to show when app opens)
        composable(Screen.SplashScreen.route) {
            SplashScreen(
                navController = navController,
                authViewModel = authViewModel,
                eventViewModel = eventViewModel
            )
        }

        // OnboardingType
        composable(
            route = Screen.OnboardingType.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            OnboardingType(navController)
        }

        // LoginOrSignUp
        composable(
            route = Screen.LoginOrSignUp.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            LoginOrSignup(navController)
        }

//        composable(
//            route = Screen.OtpVerification.route,
//            arguments = listOf(
//                navArgument("phoneNumber") {
//                    type = NavType.StringType
//                    nullable = false
//                }
//            ),
//            enterTransition = { slideInFromRight },
//            exitTransition = { slideOutToLeft },
//            popEnterTransition = { slideInFromLeft },
//            popExitTransition = { slideOutToRight }
//        ) { backStackEntry ->
//            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
//            OtpVerification(navController = navController, phoneNumber = phoneNumber)
//        }

        // Event Creation
        composable(
            route = Screen.EventCreationScreen.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            EventCreation(navController)
        }

        // NEW: Main Home Screen
        composable(
            route = Screen.HomeScreen.route,
            // Use subtle transitions for the main screen
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
            HomeScreen(navController)
        }
    }
}