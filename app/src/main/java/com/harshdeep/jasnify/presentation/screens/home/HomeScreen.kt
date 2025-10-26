package com.harshdeep.jasnify.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.scaffold.HomeTopBar
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.presentation.util.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.AuthViewModel
import com.harshdeep.jasnify.theme.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.HomeCard
import androidx.compose.foundation.layout.WindowInsets
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import kotlin.math.roundToInt

private val FADE_DISTANCE_DP = 160.dp
private val HEADER_HEIGHT = 350.dp
private const val PARALLAX_RATE = 0.5f // Smaller rate means slower scroll/more parallax

@Composable
fun HomeTabContent(internalNavController: NavHostController) {

    val scrollState = rememberScrollState()
    val fadeDistancePx = with(LocalDensity.current) { FADE_DISTANCE_DP.toPx() }
    val scrollOffset = scrollState.value

    // Alpha for TopBar and Background Image (Fade effect)
    val topBarAlpha = (scrollOffset / fadeDistancePx).coerceIn(0f, 1f)
    val bgImageAlpha = (1f - (scrollOffset / fadeDistancePx)).coerceIn(0f, 1f)

    // Parallax calculation: Image moves slower than the content (PARALLAX_RATE)
    val parallaxOffset = (scrollOffset * PARALLAX_RATE).roundToInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSecondary)
    ) {
        // ---  Background Image with Parallax and Alpha Fade ---
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = "Background image of a crowd",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(HEADER_HEIGHT)
                .align(Alignment.TopCenter)
                // Apply the parallax offset for a 'sticky'/slow-scroll effect
                .offset { IntOffset(x = 0, y = -parallaxOffset) }
                .alpha(bgImageAlpha)
        )


        Scaffold(
            topBar = {
                HomeTopBar(alpha = topBarAlpha)
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column (
                modifier = Modifier.fillMaxWidth()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ){
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(12.dp, 12.dp, 12.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Spacer ensures the cards start below the large background image
                    Spacer(modifier = Modifier.height(HEADER_HEIGHT - 210.dp))

                    BudgetTrackerCard(
                        insight = "See your budget",
                        heading = "Budget Tracker",
                        illustration = painterResource(R.drawable.ill_budget_tracker_card),
                        progress = 0.45f,
                        amountText = "₹46L",
                        labelText = "left",
                        onClick = {}
                    )


                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HomeCard(
                            insight = "Delicious and Elegant",
                            heading = "Catering Menu",
                            illustration = painterResource(R.drawable.ill_catering_menu_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFC4D4C2),
                            waveColor = Color(0x1A14570C).copy(alpha = 0.9f),
                            insightColor = Color(0xFF47671A),
                            onClick = {}
                        )
                        HomeCard(
                            insight = "Perfect Event Spaces",
                            heading = "Venue",
                            illustration = painterResource(R.drawable.ill_venue_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFD3CDE8),
                            waveColor = Color(0x1A2C186C).copy(alpha = 0.9f),
                            insightColor = Color(0xFF6448D6),
                            onClick = {}
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HomeCard(
                            insight = "Capture and Smile",
                            heading = "Moments",
                            illustration = painterResource(R.drawable.ill_moments_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFC3D4E8),
                            waveColor = Color(0x1A014594).copy(alpha = 0.9f),
                            insightColor = Color(0xFF3D58B4),
                            onClick = {}
                        )

                        HomeCard(
                            insight = "Invite and Celebrate",
                            heading = "Cards & Guests",
                            illustration = painterResource(R.drawable.ill_cards_and_guests_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFE8D0CE),
                            waveColor = Color(0x1A5D0501).copy(alpha = 0.9f),
                            insightColor = Color(0xFF5D1D1B),
                            onClick = {}
                        )
                    }

                    OrDivider(dividerGap = 12.dp, text = "Explore")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HomeCard(
                            insight = "Plan and Manage.",
                            heading = "Budget Tracker",
                            illustration = painterResource(R.drawable.ill_moments_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFC4D4C2),
                            waveColor = Color(0x1A14570C).copy(alpha = 0.9f),
                            insightColor = Color(0xFF47671A),
                            onClick = {}
                        )

                        HomeCard(
                            insight = "Organize. Schedule. Relax.",
                            heading = "Planner",
                            illustration = painterResource(R.drawable.ill_moments_card),
                            modifier = Modifier.weight(1f),
                            cardBgColor = Color(0xFFC4D4C2),
                            waveColor = Color(0x1A14570C).copy(alpha = 0.9f),
                            insightColor = Color(0xFF47671A),
                            onClick = {}
                        )
                    }
                }
                FooterJansify()
            }
        }
    }
}


@Composable
fun ChecklistsTabContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            text = "Checklists Screen",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary
        )
    }
}


@Composable
fun VendorsTabContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            "Vendors Screen",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary
        )
    }
}

@Composable
fun InspirationsTabContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            "Inspirations Screen",
            style = JasnifyTheme.typography.displayLarge,
            color = ContentPrimary
        )
    }
}

@Composable
fun ProfileTabContent(
    mainNavController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
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
    mainNavController: NavHostController,
) {
    SetStatusBarTheme(useDarkIcons = true, statusBarColor = Color.Transparent)

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
                containerColor = SurfacePrimary,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
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
                                    popUpTo(internalNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
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
                            indicatorColor = ContentBrandDark.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
    ) { paddingValues ->
        // This single NavHost manages the 5 tab transitions
        NavHost(
            navController = internalNavController,
            startDestination = Screen.HomeTabScreen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Always apply padding
        ) {
            composable(route = Screen.HomeTabScreen.Home.route) {
                HomeTabContent(
                    internalNavController
                )
            }
            composable(route = Screen.HomeTabScreen.Checklists.route) { ChecklistsTabContent() }
            composable(route = Screen.HomeTabScreen.Vendors.route) { VendorsTabContent() }
            composable(route = Screen.HomeTabScreen.Inspirations.route) { InspirationsTabContent() }
            composable(route = Screen.HomeTabScreen.Profile.route) {
                ProfileTabContent(
                    mainNavController
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {

    val testNavController = rememberNavController()
    HomeScreen(testNavController)
}