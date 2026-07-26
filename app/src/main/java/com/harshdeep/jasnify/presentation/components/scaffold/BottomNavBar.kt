package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.*

@Composable
fun BottomNavBar(
    navController: NavHostController,
    style: NavBarStyleOption = NavBarStyleOption.BASIC
) {
    val navItems = listOf(
        Screen.HomeTabScreen.Home,
        Screen.HomeTabScreen.Inspirations,
        Screen.HomeTabScreen.Checklists,
        Screen.HomeTabScreen.Vendors,
        Screen.HomeTabScreen.Profile
    )

    if (style == NavBarStyleOption.BASIC) {
        BottomAppBar(
            containerColor = SurfacePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .height(93.dp)
                .shadow(elevation = 20.dp)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            navItems.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.iconResId),
                            contentDescription = screen.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(text = screen.title, style = JasnifyTheme.typography.labelSmall)
                    },
                    selected = isSelected,
                    alwaysShowLabel = true,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                        indicatorColor = SurfaceBrandSecondary
                    )
                )
            }
        }
    } else {
        PillBottomNavBar(navController = navController, navItems = navItems)
    }
}

@Composable
fun PillBottomNavBar(navController: NavHostController, navItems: List<Screen.HomeTabScreen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = ContentPrimary.copy(alpha = 0.20f)
                ),
            shape = CircleShape,
            color = SurfacePrimary,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        val backgroundColor = if (isSelected) SurfaceBrandPrimary else Color.Transparent
                        val contentColor = if (isSelected) ContentInvPrimary else ContentSecondary

                        Box(
                            modifier = Modifier
                                .size(width = 75.dp, height = 56.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .clickable {
                                    if (!isSelected) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = screen.iconResId),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp),
                                tint = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    Column {
        BottomNavBar(navController = rememberNavController(), style = NavBarStyleOption.PILL_SHAPED)
        BottomNavBar(navController = rememberNavController(), style = NavBarStyleOption.BASIC)
    }
}
