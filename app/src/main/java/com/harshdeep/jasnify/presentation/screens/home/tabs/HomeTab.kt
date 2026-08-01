package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
 import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.HomeCard
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.HomeTopBar
import com.harshdeep.jasnify.presentation.components.sections.ExploreCategoriesHorizontal
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.sections.VenueCarousel
import com.harshdeep.jasnify.presentation.components.sections.vendorCategories
import com.harshdeep.jasnify.presentation.screens.budget.BudgetScreen
import com.harshdeep.jasnify.presentation.screens.catering.CateringMenuScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

private const val PARALLAX_RATE = 0.5f

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@SuppressLint("FrequentlyChangingValue")
@Composable
fun HomeTab(
    mainNavController: NavHostController,
    internalNavController: NavHostController,
    onMenuClick: () -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel(),
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    venueViewModel: VenueViewModel = hiltViewModel()
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val isVenuesLoading by venueViewModel.isLoading.collectAsStateWithLifecycle()

    // Fetch user events on mount to ensure real-time updates are active
    LaunchedEffect(Unit) {
        eventViewModel.fetchUserEvents()
    }

    // Sync eventId to BudgetViewModel to get accurate budget summary
    LaunchedEffect(activeEvent?.id) {
        activeEvent?.id?.let { id ->
            budgetViewModel.setEventId(id)
        }
    }

    val budgetEntity by budgetViewModel.budgetSettings.collectAsStateWithLifecycle()
    val expensesEntities by budgetViewModel.expenses.collectAsStateWithLifecycle()

    val totalSpent = remember(expensesEntities) {
        expensesEntities.sumOf { it.amount }
    }

    val totalBudget = remember(budgetEntity, activeEvent) {
        budgetEntity?.totalBudget ?: activeEvent?.budget ?: 0.0
    }

    val remainingFunds = (totalBudget - totalSpent).coerceAtLeast(0.0)
    val remainingPercentage = if (totalBudget > 0) (remainingFunds / totalBudget).toFloat().coerceIn(0f, 1f) else 0f

    // Budget formatting logic (100, 1k, 45L, 23Cr) - No decimal points
    fun formatBudgetShorthand(amount: Double): String {
        return when {
            amount >= 10_000_000 -> "${(amount / 10_000_000).toLong()}Cr"
            amount >= 100_000 -> "${(amount / 100_000).toLong()}L"
            amount >= 1000 -> "${(amount / 1000).toLong()}k"
            else -> "${amount.toLong()}"
        }
    }

    val amountText = remember(remainingFunds) { "₹${formatBudgetShorthand(remainingFunds)}" }

    // Date formatting for the top bar - Using java.time for better consistency with HomeTopBar
    val eventDateString = remember(activeEvent) {
        val now = System.currentTimeMillis()
        val effectiveDate = if (activeEvent?.multiDay == true) {
            val dates = activeEvent?.subEvents?.mapNotNull { it.date } ?: emptyList()
            if (dates.isEmpty()) null
            else {
                val upcoming = dates.filter { it >= now }.minOrNull()
                upcoming ?: dates.maxOrNull()
            }
        } else {
            activeEvent?.date
        }

        effectiveDate?.let {
            try {
                Instant.ofEpochMilli(it)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } catch (e: Exception) {
                ""
            }
        } ?: ""
    }

    HomeTabContent(
        eventName = activeEvent?.name ?: "",
        eventDateString = eventDateString,
        remainingPercentage = remainingPercentage,
        amountText = amountText,
        mainNavController = mainNavController,
        internalNavController = internalNavController,
        onMenuClick = onMenuClick,
        onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        eventViewModel = eventViewModel,
        isVenuesLoading = isVenuesLoading
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeTabContent(
    eventName: String,
    eventDateString: String,
    remainingPercentage: Float,
    amountText: String,
    mainNavController: NavHostController,
    internalNavController: NavHostController,
    onMenuClick: () -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    eventViewModel: EventViewModel? = null,
    isVenuesLoading: Boolean = false
) {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedCategory by remember { mutableStateOf<VendorCategoryItem?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Determine proportions based on device screen height dynamically
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeight = configuration.screenHeightDp.dp

    // Calculate dynamic heights relative to overall screen height
    val headerHeight = remember(screenHeight) { screenHeight * 0.42f }
    val visibleBackgroundOffset = remember(screenHeight) { screenHeight * 0.24f }

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
        selectedCategory = null
        currentScreen = "home"
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(220))
                .togetherWith(fadeOut(animationSpec = tween(220)))
        },
        label = "screen_transition",
        modifier = Modifier.fillMaxSize()
    ) { screen ->

        if (screen == "home") {
            val scrollState = rememberScrollState()
            val fadeDistancePx = with(density) { visibleBackgroundOffset.toPx() }

            val topBarAlpha by remember {
                derivedStateOf {
                    if (fadeDistancePx > 0f) {
                        (scrollState.value / fadeDistancePx).coerceIn(0f, 1f)
                    } else 0f
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
                        .height(headerHeight)
                        .align(Alignment.TopCenter)
                        .graphicsLayer {
                            val scrollOffset = scrollState.value
                            translationY = -scrollOffset * PARALLAX_RATE
                            alpha = if (fadeDistancePx > 0f) {
                                (1f - (scrollOffset / fadeDistancePx)).coerceIn(0f, 1f)
                            } else 1f
                        }
                )

                Scaffold(
                    topBar = {
                        HomeTopBar(
                            title = eventName,
                            dateString = eventDateString,
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
                        // The spacing spacer height is bound directly to the dynamic visible offset
                        Spacer(modifier = Modifier.height(visibleBackgroundOffset))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = BackgroundPrimary,
                                    shape = RoundedCornerShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp, 12.dp, 12.dp, 0.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BudgetTrackerCard(
                                    insight = "See your budget",
                                    heading = "Budget Tracker",
                                    illustration = painterResource(R.drawable.ill_budget_tracker_card),
                                    progress = remainingPercentage,
                                    amountText = amountText,
                                    labelText = "left",
                                    onClick = { navigateTo("budget") }
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                                        onClick = { navigateTo("catering") }
                                    )
                                    HomeCard(
                                        insight = "Perfect Event Spaces",
                                        heading = "Venue",
                                        illustration = painterResource(R.drawable.ill_venue_card),
                                        modifier = Modifier.weight(1f),
                                        cardBgColor = Color(0xFFD3CDE8),
                                        waveColor = Color(0x1A2C186C).copy(alpha = 0.9f),
                                        insightColor = Color(0xFF6448D6),
                                        onClick = { navigateTo("venues") }
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
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                VenueCarousel(
                                    title = "Trending Venues in Patna",
                                    venues = MockData.sampleVenues1,
                                    isLoading = isVenuesLoading,
                                    onVenueClick = { navigateTo("venues") },
                                    onFavoriteToggle = { },
                                    onOfferClick = { }
                                )

                                VenueCarousel(
                                    title = "More Venues to Explore",
                                    venues = MockData.sampleVenues2,
                                    isLoading = isVenuesLoading,
                                    onVenueClick = { navigateTo("venues") },
                                    onFavoriteToggle = { },
                                    onOfferClick = { }
                                )
                            }

                            DashedDivider()

                            ExploreCategoriesHorizontal(
                                categories = vendorCategories,
                                onCategoryClick = { category ->
                                    selectedCategory = category
                                    navigateTo("vendors")
                                }
                            )

                            FooterJansify()
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                when (screen) {
                    "budget" -> eventViewModel?.let { vm ->
                        BudgetScreen(
                            onBackClick = { currentScreen = "home" },
                            eventViewModel = vm
                        )
                    }
                    "venues" -> eventViewModel?.let { vm ->
                        VenueScreen(
                            selectedLocation = "City, State",
                            onVenueClick = {},
                            onChatClick = { venue ->
                                val merchantId = venue.merchantId.ifBlank { "unknown_merchant" }
                                val venueId = venue.id.ifBlank { "unknown_venue" }
                                mainNavController.navigate("chat_screen/$merchantId/$venueId")
                            },
                            onBackClick = { currentScreen = "home" },
                            eventViewModel = vm
                        )
                    }
                    "catering" -> eventViewModel?.let { vm ->
                        CateringMenuScreen(
                            onBackClick = { currentScreen = "home" },
                            eventViewModel = vm
                        )
                    }
                    "vendors" -> {
                        VendorsTab(
                            mainNavController = mainNavController,
                            internalNavController = internalNavController,
                            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                            initialCategory = selectedCategory,
                            onBackClick = {
                                selectedCategory = null
                                currentScreen = "home"
                            },
                            eventViewModel = eventViewModel ?: hiltViewModel()
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeTabContentPreview() {
    val context = LocalContext.current
    HomeTabContent(
        eventName = "Taylor & Travis’s Wedding",
        eventDateString = "2026-11-20",
        remainingPercentage = 0.65f,
        amountText = "₹46L",
        mainNavController = NavHostController(context),
        internalNavController = NavHostController(context),
        onMenuClick = {},
        onBottomBarVisibilityChange = {}
    )
}