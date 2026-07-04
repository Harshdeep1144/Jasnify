package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.*

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navItems = listOf(
        Screen.HomeTabScreen.Home,
        Screen.HomeTabScreen.Inspirations,
        Screen.HomeTabScreen.Checklists,
        Screen.HomeTabScreen.Vendors,
        Screen.HomeTabScreen.Profile
    )

    BottomAppBar(
        containerColor = SurfacePrimary,
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .shadow(elevation = 20.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
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
                    Text(text = screen.title, style = JasnifyTheme.typography.labelSmall)
                },
                selected = isSelected,
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
}