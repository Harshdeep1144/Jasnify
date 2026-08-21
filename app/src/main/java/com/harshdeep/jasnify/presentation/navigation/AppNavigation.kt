package com.harshdeep.jasnify.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.navigation.navgraphs.mainAppNavGraph
import com.harshdeep.jasnify.presentation.navigation.navgraphs.onboardingNavGraph
import com.harshdeep.jasnify.presentation.screens.onboarding.SplashScreen
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    // The main controller that handles full-screen transitions and graph switching
    val mainNavController = rememberNavController()

    Surface(color = BackgroundPrimary) {
        NavHost(
            navController = mainNavController,
            startDestination = Screen.SplashScreen.route
        ) {
            composable(Screen.SplashScreen.route) {
                SplashScreen(
                    navController = mainNavController,
                    authViewModel = authViewModel,
                    eventViewModel = eventViewModel
                )
            }

            // --- Layer 1: Onboarding/Auth Flow ---
            onboardingNavGraph(mainNavController)

            // --- Layer 2: Main App Flow (Bottom Nav + Deep Feature Screens) ---
            mainAppNavGraph(mainNavController)
        }
    }
}
