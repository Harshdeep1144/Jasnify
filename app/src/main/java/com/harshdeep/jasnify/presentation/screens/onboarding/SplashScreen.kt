package com.harshdeep.jasnify.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.theme.BackgroundBrand

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.ani_jasnify_logo)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true
    )

    // Trigger navigation only when animation reaches completion (progress == 1f)
    LaunchedEffect(progress) {
        if (progress == 1f) {
            val isLoggedIn = authViewModel.isUserLoggedIn()
            val hasCompletedOnboarding = authViewModel.hasCompletedOnboarding()

            val destination = if (!isLoggedIn || !hasCompletedOnboarding) {
                Screen.OnboardingGraph.route
            } else {
                Screen.MainSkeletonLoading.route
            }

            navController.navigate(destination) {
                popUpTo(Screen.SplashScreen.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBrand),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .aspectRatio(1f)
        )
    }
}