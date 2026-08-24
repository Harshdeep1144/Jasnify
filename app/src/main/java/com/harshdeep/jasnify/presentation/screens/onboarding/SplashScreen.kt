package com.harshdeep.jasnify.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.theme.BackgroundBrand
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(200L.milliseconds) // Minimal delay for auth state to stabilize
        val isLoggedIn = authViewModel.isUserLoggedIn()
        val hasCompletedOnboarding = authViewModel.hasCompletedOnboarding()

        val destination = if (!isLoggedIn || !hasCompletedOnboarding) {
            Screen.OnboardingGraph.route
        } else {
            // Logged in and finished onboarding? Move immediately to the Main Loading Skeleton.
            Screen.MainSkeletonLoading.route
        }

        navController.navigate(destination) {
            popUpTo(Screen.SplashScreen.route) { inclusive = true }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBrand),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_app),
            contentDescription = "App Logo",
            Modifier.size(130.dp)
        )
    }
}
