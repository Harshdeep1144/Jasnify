package com.harshdeep.jasnify.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.util.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.theme.*

@Composable
fun HomeTabContent(internalNavController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Dashboard",
            style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = ContentPrimary
        )
    }
}

@Composable fun ChecklistsTabContent() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center)
    {
        Text(
            text = "Checklists Screen",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary
        )
    }
}
@Composable fun VendorsTabContent() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center)
    {
        Text("Vendors Screen",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary)
    }
}
@Composable fun InspirationsTabContent() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center)
    {
        Text("Inspirations Screen",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary)
    }
}

@Composable
fun ProfileTabContent(
    mainNavController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundPrimary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Screen", style = JasnifyTheme.typography.displayLarge, color = ContentPrimary)
        Spacer(Modifier.height(20.dp))
        Button(onClick = {
            authViewModel.logout(context)

            mainNavController.navigate(Screen.LoginOrSignUp.route) {
                popUpTo(Screen.MainAppGraph.route) { inclusive = true }
            }
        }) {
            Text("Logout (Global Nav)")
        }
    }
}







// -------------------------------------------  The Main App Host  ------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainNavController: NavHostController, // Layer 1 Controller (for Logout)
) {
    SetStatusBarTheme(useDarkIcons = true, statusBarColor = BackgroundPrimary)

    // Controller for all internal app navigation (Tabs)
    val internalNavController = rememberNavController()

    val navItems = listOf(
        Screen.HomeTabScreen.Home,
        Screen.HomeTabScreen.Inspirations,
        Screen.HomeTabScreen.Checklists,
        Screen.HomeTabScreen.Vendors,
        Screen.HomeTabScreen.Profile
    )

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = BackgroundPrimary,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.iconResId),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = JasnifyTheme.typography.labelSmall
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                internalNavController.navigate(route = screen.route) {
                                    // Pop up to the start of the graph to avoid multiple copies of the same tab
                                    popUpTo(internalNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ContentBrandDark,
                            selectedTextColor = ContentBrandDark,
                            unselectedIconColor = ContentSecondary,
                            unselectedTextColor = ContentSecondary,
                            indicatorColor = SurfaceSecondary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // This single NavHost manages the 5 tab transitions
        NavHost(
            navController = internalNavController,
            startDestination =  Screen.HomeTabScreen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Always apply padding
        ) {
            composable( route = Screen.HomeTabScreen.Home.route) { HomeTabContent(internalNavController) }
            composable( route = Screen.HomeTabScreen.Checklists.route) { ChecklistsTabContent() }
            composable( route = Screen.HomeTabScreen.Vendors.route) { VendorsTabContent() }
            composable( route = Screen.HomeTabScreen.Inspirations.route) { InspirationsTabContent() }
            composable( route = Screen.HomeTabScreen.Profile.route) { ProfileTabContent(mainNavController) }
        }
    }
}
