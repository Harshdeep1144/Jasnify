package com.harshdeep.jasnify.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.LogoutButton
import com.harshdeep.jasnify.presentation.navigation.HomeTabScreen
import com.harshdeep.jasnify.presentation.util.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.theme.*

@Composable
fun HomeTabContent(mainNavController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text("Home Dashboard", style = JasnifyTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold), color = ContentPrimary)
    }
}

@Composable
fun ChecklistsTabContent() {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary), contentAlignment = Alignment.Center) {
        Text("Checklists Screen", style = JasnifyTheme.typography.displayLarge, color = ContentPrimary)
    }
}

@Composable
fun VendorsTabContent() {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary), contentAlignment = Alignment.Center) {
        Text("Vendors Screen", style = JasnifyTheme.typography.displayLarge, color = ContentPrimary)
    }
}

@Composable
fun InspirationsTabContent() {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary), contentAlignment = Alignment.Center) {
        Text("Inspirations Screen", style = JasnifyTheme.typography.displayLarge, color = ContentPrimary)
    }
}

@Composable
fun ProfileTabContent(
    mainNavController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(BackgroundPrimary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Screen", style = JasnifyTheme.typography.displayLarge, color = ContentPrimary)
        Spacer(Modifier.height(20.dp))
        LogoutButton(
            navController = mainNavController
        )

    }
}










//--------------------------------------------------- Navigation Bottom Bar ---------------------------------------------------





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainNavController: NavHostController,
) {
    SetStatusBarTheme(useDarkIcons = true, statusBarColor = BackgroundPrimary)

    // NavController for managing the internal state of the 5 tabs
    val tabNavController = rememberNavController()

    // The list of items to display in the Bottom Navigation Bar
    val navItems = listOf(
        HomeTabScreen.Home,
        HomeTabScreen.Inspirations,
        HomeTabScreen.Checklists,
        HomeTabScreen.Vendors,
        HomeTabScreen.Profile
    )

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = BackgroundPrimary,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Get the current back stack entry to highlight the selected tab
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    // Determine if the current screen route is part of the destination hierarchy
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
                                screen.title,
                                style = JasnifyTheme.typography.labelSmall
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            // Only navigate if the user clicks a different tab
                            if (!isSelected) {
                                tabNavController.navigate(screen.route) {
                                    popUpTo(tabNavController.graph.startDestinationId) {
                                        saveState = true // Save the state (e.g., scroll position) of the previous tab
                                    }
                                    launchSingleTop = true // Avoid creating multiple copies of the same destination
                                    restoreState = true    // Restore the previously saved state
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
        NavHost(
            navController = tabNavController,
            startDestination = HomeTabScreen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(HomeTabScreen.Home.route) {
                HomeTabContent(mainNavController)
            }
            composable(HomeTabScreen.Checklists.route) {
                ChecklistsTabContent()
            }
            composable(HomeTabScreen.Vendors.route) {
                VendorsTabContent()
            }
            composable(HomeTabScreen.Inspirations.route) {
                InspirationsTabContent()
            }
            composable(HomeTabScreen.Profile.route) {
                ProfileTabContent(mainNavController)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    JasnifyTheme {
        HomeScreen(mainNavController = rememberNavController())
    }
}
