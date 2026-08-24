package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.NavBarStyleOption
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val DefaultNavItems = listOf(
    Screen.HomeTabScreen.Home,
    Screen.HomeTabScreen.Vendors,
    Screen.HomeTabScreen.Checklists,
    Screen.HomeTabScreen.Guests,
    Screen.HomeTabScreen.Profile
)

private val ZeroInsets = WindowInsets(0, 0, 0, 0)

@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    style: NavBarStyleOption = NavBarStyleOption.PILL_SHAPED
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var lastSelectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var isNavSettled by rememberSaveable { mutableStateOf(false) }

    val selectedIndex = remember(currentDestination) {
        val effectiveDest = currentDestination ?: navController.currentDestination
        val index = DefaultNavItems.indexOfFirst { screen ->
            effectiveDest?.hierarchy?.any { it.route == screen.route } == true
        }

        if (index != -1) {
            if (!isNavSettled && index == 0 && lastSelectedIndex != 0) {
                lastSelectedIndex
            } else {
                isNavSettled = true
                lastSelectedIndex = index
                index
            }
        } else {
            lastSelectedIndex
        }
    }

    BottomNavBarContent(
        selectedIndex = selectedIndex,
        onItemSelected = { screen ->
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        navItems = DefaultNavItems,
        style = style,
        modifier = modifier
    )
}

@Composable
fun BottomNavBarContent(
    selectedIndex: Int,
    onItemSelected: (Screen.HomeTabScreen) -> Unit,
    navItems: List<Screen.HomeTabScreen>,
    style: NavBarStyleOption,
    modifier: Modifier = Modifier,
    applyPadding: Boolean = true
) {
    if (style == NavBarStyleOption.BASIC) {
        BasicBottomNavBar(
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            navItems = navItems,
            modifier = modifier,
            applyPadding = applyPadding
        )
    } else {
        PillBottomNavBar(
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            navItems = navItems,
            modifier = modifier,
            applyPadding = applyPadding
        )
    }
}

@Composable
private fun BasicBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Screen.HomeTabScreen) -> Unit,
    navItems: List<Screen.HomeTabScreen>,
    modifier: Modifier = Modifier,
    applyPadding: Boolean = true
) {
    BottomAppBar(
        containerColor = SurfacePrimary,
        windowInsets = if (applyPadding) BottomAppBarDefaults.windowInsets else ZeroInsets,
        modifier = modifier
            .fillMaxWidth()
            .height(if (applyPadding) 93.dp else 80.dp)
            .shadow(elevation = 20.dp)
    ) {
        navItems.forEachIndexed { index, screen ->
            val isSelected = index == selectedIndex

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
                        onItemSelected(screen)
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

@Composable
fun PillBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Screen.HomeTabScreen) -> Unit,
    navItems: List<Screen.HomeTabScreen>,
    modifier: Modifier = Modifier,
    applyPadding: Boolean = true
) {
    var isInitialComposition by remember { mutableStateOf(true) }

    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = if (isInitialComposition) {
            snap()
        } else {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "SlidingTabIndicatorAnimation",
        finishedListener = { isInitialComposition = false }
    )

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        isInitialComposition = false
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (applyPadding) {
                    Modifier
                        .background(brush = BottomGradientBrush)
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .width(368.dp)
                .height(62.dp)
                .pill360Shadow(
                    ambientColor = Color.Black.copy(alpha = 0.10f),
                    ambientBlur = 12.dp,
                    ambientSpread = 2.dp,
                    spotColor = Color.Black.copy(alpha = 0.15f),
                    spotBlur = 18.dp,
                    spotOffsetY = 4.dp
                ),
            shape = CircleShape,
            color = SurfacePrimary,
            tonalElevation = 0.dp,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                val tabCount = navItems.size
                val tabWidth = maxWidth / tabCount

                Box(
                    modifier = Modifier
                        .offset(x = tabWidth * animatedIndex)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(SurfaceBrandSecondary)
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, screen ->
                        val isSelected = index == selectedIndex

                        val animatedContentColor by animateColorAsState(
                            targetValue = if (isSelected) ContentBrandDark else ContentSecondary,
                            animationSpec = tween(durationMillis = 150),
                            label = "TabContentColorAnimation"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (!isSelected) {
                                        onItemSelected(screen)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = screen.iconResId),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp),
                                tint = animatedContentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8FA)
@Composable
fun PillBottomNavBarPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA)),
        verticalArrangement = Arrangement.Bottom
    ) {
        BottomNavBar(
            navController = rememberNavController(),
            style = NavBarStyleOption.PILL_SHAPED
        )
    }
}