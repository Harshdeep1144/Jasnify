package com.harshdeep.jasnify.presentation.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import com.harshdeep.jasnify.presentation.components.scaffold.BottomNavBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.navigation.navgraphs.homeNavGraph
import com.harshdeep.jasnify.presentation.util.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.UIViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    mainNavController: NavHostController,
    joinedEventId: String? = null
) {
    val mainGraphEntry = remember(mainNavController) { mainNavController.getBackStackEntry(Screen.MainAppGraph.route) }
    val uiViewModel: UIViewModel = hiltViewModel(mainGraphEntry)
    val eventViewModel: EventViewModel = hiltViewModel(mainGraphEntry)
    SetStatusBarTheme(useDarkIcons = true, statusBarColor = Color.Transparent)
    val context = LocalContext.current
    val navBarStyle by uiViewModel.navBarStyle.collectAsState()

    // Handle joined event ID if provided
    LaunchedEffect(joinedEventId) {
        joinedEventId?.let { id ->
            eventViewModel.fetchAndSetActiveEvent(id)
        }
    }

    val internalNavController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(true) }

    // Intercept back button to prevent navigating back to onboarding if the backstack wasn't cleared
    BackHandler(enabled = true) {
        if (!internalNavController.popBackStack()) {
            // If internal nav can't pop, we are at the root of the home screen.
            // We should exit the activity to ensure we don't go back to onboarding/splash.
            (context as? Activity)?.finish()
        }
    }

    Scaffold(
        bottomBar = {
            // Animated visibility wraps the bottom bar to provide slide and fade animations
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(durationMillis = 200)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(durationMillis = 200))
            ) {
                BottomNavBar(navController = internalNavController, style = navBarStyle)
            }
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
    ) { paddingValues ->
        NavHost(
            navController = internalNavController,
            startDestination = Screen.HomeTabScreen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = if (showBottomBar && navBarStyle == NavBarStyleOption.BASIC) {
                        paddingValues.calculateBottomPadding()
                    } else {
                        0.dp
                    }
                )
        ) {
            // All tab routes are now managed in this extension function
            homeNavGraph(
                mainNavController = mainNavController,
                navController = internalNavController,
                onBottomBarVisibilityChange = { showBottomBar = it },
                eventViewModel = eventViewModel
            )
        }
    }
}