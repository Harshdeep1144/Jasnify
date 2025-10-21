package com.harshdeep.jasnify.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.BackgroundBrand
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(200L) // short delay before auth check
        val isLoggedIn = authViewModel.isUserLoggedIn()

        val destination = when {
            !isLoggedIn -> Screen.OnboardingGraph.route
            else -> {
//                delay(50L) // let Firebase initialize user session
                val hasCompletedEventCreation = eventViewModel.checkIfUserHasEventsInDatabase()

                if (hasCompletedEventCreation) {
                    Screen.MainAppScreen.route
                } else {
                    Screen.EventCreationScreen.route
                }
            }
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
