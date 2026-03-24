package com.harshdeep.jasnify.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.components.scaffold.BottomNavBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.navigation.navgraphs.homeNavGraph
import com.harshdeep.jasnify.presentation.util.SetStatusBarTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    mainNavController: NavHostController,
) {
    SetStatusBarTheme(useDarkIcons = true, statusBarColor = Color.Transparent)

    val internalNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = internalNavController)
        },
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
    ) { paddingValues ->
        NavHost(
            navController = internalNavController,
            startDestination = Screen.HomeTabScreen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // All tab routes are now managed in this extension function
            homeNavGraph(
                mainNavController = mainNavController,
                internalNavController = internalNavController
            )
        }
    }
}