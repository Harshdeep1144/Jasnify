package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.*

/**
 * Custom 360-degree drop shadow modifier.
 * Native Android `Modifier.shadow` uses a top-down light source that only casts shadows downward.
 * This custom draw method renders both an ambient halo (all 4 sides) and a directional spot shadow.
 */
fun Modifier.pill360Shadow(
    ambientColor: Color = Color.Black.copy(alpha = 0.08f),
    ambientBlur: Dp = 12.dp,
    ambientSpread: Dp = 2.dp,
    spotColor: Color = Color.Black.copy(alpha = 0.14f),
    spotBlur: Dp = 16.dp,
    spotOffsetY: Dp = 4.dp
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val cornerRadius = size.height / 2f

        // 1. Ambient 360-degree halo shadow (casts evenly on top, bottom, left, right)
        val ambientPaint = Paint()
        val frameworkAmbientPaint = ambientPaint.asFrameworkPaint()
        if (ambientBlur.toPx() > 0) {
            frameworkAmbientPaint.maskFilter = android.graphics.BlurMaskFilter(
                ambientBlur.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }
        frameworkAmbientPaint.color = ambientColor.toArgb()

        canvas.drawRoundRect(
            left = -ambientSpread.toPx(),
            top = -ambientSpread.toPx(),
            right = size.width + ambientSpread.toPx(),
            bottom = size.height + ambientSpread.toPx(),
            radiusX = cornerRadius,
            radiusY = cornerRadius,
            paint = ambientPaint
        )

        // 2. Directional spot shadow (adds downward depth)
        val spotPaint = Paint()
        val frameworkSpotPaint = spotPaint.asFrameworkPaint()
        if (spotBlur.toPx() > 0) {
            frameworkSpotPaint.maskFilter = android.graphics.BlurMaskFilter(
                spotBlur.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }
        frameworkSpotPaint.color = spotColor.toArgb()

        canvas.drawRoundRect(
            left = 0f,
            top = spotOffsetY.toPx(),
            right = size.width,
            bottom = size.height + spotOffsetY.toPx(),
            radiusX = cornerRadius,
            radiusY = cornerRadius,
            paint = spotPaint
        )
    }
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    style: NavBarStyleOption = NavBarStyleOption.PILL_SHAPED
) {
    val navItems = listOf(
        Screen.HomeTabScreen.Home,
        Screen.HomeTabScreen.Inspirations,
        Screen.HomeTabScreen.Checklists,
        Screen.HomeTabScreen.Vendors,
        Screen.HomeTabScreen.Profile
    )

    if (style == NavBarStyleOption.BASIC) {
        BasicBottomNavBar(
            navController = navController,
            navItems = navItems,
            modifier = modifier
        )
    } else {
        PillBottomNavBar(
            navController = navController,
            navItems = navItems,
            modifier = modifier
        )
    }
}

@Composable
private fun BasicBottomNavBar(
    navController: NavHostController,
    navItems: List<Screen.HomeTabScreen>,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        containerColor = SurfacePrimary,
        modifier = modifier
            .fillMaxWidth()
            .height(93.dp)
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
}

@Composable
fun PillBottomNavBar(
    navController: NavHostController,
    navItems: List<Screen.HomeTabScreen>,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine currently selected index
    val selectedIndex = remember(currentDestination, navItems) {
        val index = navItems.indexOfFirst { screen ->
            currentDestination?.hierarchy?.any { it.route == screen.route } == true
        }
        if (index != -1) index else 0
    }

    // Smooth physics spring animation for the sliding tab indicator
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "SlidingTabIndicatorAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.25f to BackgroundPrimary.copy(alpha = 0.15f),
                        0.55f to BackgroundPrimary.copy(alpha = 0.65f),
                        0.80f to BackgroundPrimary.copy(alpha = 0.92f),
                        1.00f to BackgroundPrimary
                    )
                )
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
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
                    .padding(horizontal = 6.dp, vertical = 5.dp)
            ) {
                val containerWidth = maxWidth
                val tabCount = navItems.size
                val tabWidth = containerWidth / tabCount

                // Active brand pill background that slides across tabs
                Box(
                    modifier = Modifier
                        .offset(x = tabWidth * animatedIndex)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(SurfaceBrandPrimary)
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, screen ->
                        val isSelected = index == selectedIndex

                        // Animated icon tint color
                        val targetContentColor = if (isSelected) ContentInvPrimary else ContentSecondary
                        val animatedContentColor by animateColorAsState(
                            targetValue = targetContentColor,
                            animationSpec = tween(durationMillis = 200),
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