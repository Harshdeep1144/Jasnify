package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.HomeCard
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.HomeTopBar
import com.harshdeep.jasnify.presentation.components.sections.VendorsCarousel
import com.harshdeep.jasnify.presentation.screens.budget.BudgetScreen
import com.harshdeep.jasnify.presentation.screens.catering.CateringMenuScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen
import com.harshdeep.jasnify.theme.BackgroundPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val FADE_DISTANCE_DP = 160.dp
private val HEADER_HEIGHT = 350.dp
private const val PARALLAX_RATE = 0.5f

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("FrequentlyChangingValue")
@Composable
fun HomeTab(
    onMenuClick: () -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit
) {
    var currentScreen by remember { mutableStateOf("home") }
    val coroutineScope = rememberCoroutineScope()

    // Professional touch response: A tiny delay of 80ms allows the ripple animation to render
    val navigateTo: (String) -> Unit = remember {
        { target ->
            coroutineScope.launch {
                delay(80.milliseconds)
                currentScreen = target
            }
        }
    }

    LaunchedEffect(currentScreen) {
        onBottomBarVisibilityChange(currentScreen == "home")
    }

    BackHandler(enabled = currentScreen != "home") {
        currentScreen = "home"
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(220))
                    .togetherWith(fadeOut(animationSpec = tween(220)))
            },
            label = "screen_transition",
            modifier = Modifier.fillMaxSize()
        ) { screen ->

            // Professional Spring Animation
            val boundsTransform = remember {
                { _: Rect, _: Rect ->
                    spring<Rect>(
                        dampingRatio = 0.85f, // Clean micro-elasticity response
                        stiffness = 380f      // Instantaneous but natural feeling transition speeds
                    )
                }
            }

            if (screen == "home") {
                val scrollState = rememberScrollState()
                val fadeDistancePx = with(LocalDensity.current) { FADE_DISTANCE_DP.toPx() }

                val topBarAlpha by remember {
                    derivedStateOf {
                        (scrollState.value / fadeDistancePx).coerceIn(0f, 1f)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundPrimary)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bg_home),
                        contentDescription = "Background image of a crowd",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HEADER_HEIGHT)
                            .align(Alignment.TopCenter)
                            .graphicsLayer {
                                val scrollOffset = scrollState.value
                                translationY = -scrollOffset * PARALLAX_RATE
                                alpha = (1f - (scrollOffset / fadeDistancePx)).coerceIn(0f, 1f)
                            }
                    )

                    Scaffold(
                        topBar = {
                            HomeTopBar(
                                title = "Taylor & Travis’s Wedding",
                                dateString = "2026-11-21",
                                alpha = topBarAlpha,
                                onMenuClick = onMenuClick
                            )
                        },
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .verticalScroll(scrollState)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp, 12.dp, 12.dp, 0.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Spacer(modifier = Modifier.height(HEADER_HEIGHT - 210.dp))

                                // Expanding flawlessly from the top and bottom edges outward to the height of the screen.
                                BudgetTrackerCard(
                                    insight = "See your budget",
                                    heading = "Budget Tracker",
                                    illustration = painterResource(R.drawable.ill_budget_tracker_card),
                                    progress = 0.45f,
                                    amountText = "₹46L",
                                    labelText = "left",
                                    onClick = { navigateTo("budget") },
                                    modifier = Modifier.sharedBounds(
                                        rememberSharedContentState(key = "budget"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        boundsTransform = boundsTransform,
                                        resizeMode = ResizeMode.scaleToBounds(ContentScale.FillWidth, Alignment.Center)
                                    )
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Expand outwards symmetrically in all directions (top, bottom, left, and right) to smoothly take over the viewport.
                                    HomeCard(
                                        insight = "Delicious and Elegant",
                                        heading = "Catering Menu",
                                        illustration = painterResource(R.drawable.ill_catering_menu_card),
                                        modifier = Modifier
                                            .weight(1f)
                                            .sharedBounds(
                                                rememberSharedContentState(key = "catering"),
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = boundsTransform,
                                                resizeMode = ResizeMode.scaleToBounds(ContentScale.Crop, Alignment.Center)
                                            ),
                                        cardBgColor = Color(0xFFC4D4C2),
                                        waveColor = Color(0x1A14570C).copy(alpha = 0.9f),
                                        insightColor = Color(0xFF47671A),
                                        onClick = { navigateTo("catering") }
                                    )
                                    HomeCard(
                                        insight = "Perfect Event Spaces",
                                        heading = "Venue",
                                        illustration = painterResource(R.drawable.ill_venue_card),
                                        modifier = Modifier
                                            .weight(1f)
                                            .sharedBounds(
                                                rememberSharedContentState(key = "venue"),
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = boundsTransform,
                                                resizeMode = ResizeMode.scaleToBounds(ContentScale.Crop, Alignment.Center)
                                            ),
                                        cardBgColor = Color(0xFFD3CDE8),
                                        waveColor = Color(0x1A2C186C).copy(alpha = 0.9f),
                                        insightColor = Color(0xFF6448D6),
                                        onClick = { navigateTo("venue") }
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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

                                OrDivider(dividerGap = 12.dp, text = "EXPLORE")
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                VendorsCarousel(
                                    title = "Trending Venues in Patna",
                                    vendors = MockData.sampleVenues1,
                                    onVendorClick = { },
                                    onFavoriteToggle = { },
                                    onOfferClick = { }
                                )

                                Spacer(Modifier.height(12.dp))

                                VendorsCarousel(
                                    title = "Trending Venues in Patna",
                                    vendors = MockData.sampleVenues2,
                                    onVendorClick = { },
                                    onFavoriteToggle = { },
                                    onOfferClick = { }
                                )
                            }
                            FooterJansify()
                        }
                    }
                }
            } else {
                // Zero-flicker transition when navigating backwards.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedBounds(
                            rememberSharedContentState(key = screen),
                            animatedVisibilityScope = this@AnimatedContent,
                            boundsTransform = boundsTransform,
                            resizeMode = if (screen == "budget") {
                                ResizeMode.scaleToBounds(ContentScale.FillWidth, Alignment.Center)
                            } else {
                                ResizeMode.scaleToBounds(ContentScale.Crop, Alignment.Center)
                            }
                        )
                ) {
                    when (screen) {
                        "budget" -> BudgetScreen(
                            onBackClick = { currentScreen = "home" },
                        )
                        "venue" -> VenueScreen(
                            onVenueClick = {},
                            onBackClick = { currentScreen = "home" },
                            isScreenActive = currentScreen == "venue" // FIX: Track screen activity state
                        )
                        "catering" -> CateringMenuScreen(
                            onBackClick = { currentScreen = "home" },
                        )
                    }
                }
            }
        }
    }
}