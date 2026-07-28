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
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(500L.milliseconds) // Wait for auth state to stabilize
        var isLoggedIn = authViewModel.isUserLoggedIn()
        
        // If not immediately logged in, wait a bit longer (Firebase initialization)
        if (!isLoggedIn) {
            delay(500L.milliseconds)
            isLoggedIn = authViewModel.isUserLoggedIn()
        }

        val destination = when {
            !isLoggedIn -> Screen.OnboardingGraph.route
            else -> {
                // 1. Check local cache for immediate redirection
                val cachedEventId = eventViewModel.getLocalActiveEventId()
                if (cachedEventId != null) {
                    android.util.Log.d("SplashScreen", "Found cached eventId: $cachedEventId. Redirecting to MainApp.")
                    eventViewModel.fetchAndSetActiveEvent(cachedEventId)
                    Screen.MainAppScreen.route
                } else {
                    // 2. Fallback to deep database check
                    android.util.Log.d("SplashScreen", "No cache. Performing participation check...")
                    val hasEventParticipation = eventViewModel.checkIfUserParticipatesInAnyEvent()
                    if (hasEventParticipation) {
                        android.util.Log.d("SplashScreen", "Participation confirmed. Redirecting to MainApp.")
                        Screen.MainAppScreen.route
                    } else {
                        android.util.Log.d("SplashScreen", "No events found. Redirecting to Creation.")
                        Screen.EventCreationScreen.route
                    }
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
