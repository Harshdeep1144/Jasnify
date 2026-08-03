package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.annotation.SuppressLint
import android.os.Build
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
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
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

private const val PARALLAX_RATE = 0.5f

sealed class HeaderMedia {
    data class ImageResource(val resId: Int) : HeaderMedia()
    data class ImageUrl(val url: String) : HeaderMedia()
    data class VideoResource(val resId: Int) : HeaderMedia()
    data class VideoUrl(val url: String) : HeaderMedia()
}

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
    val savedVenuesFromCloud by venueViewModel.savedVenues.collectAsStateWithLifecycle()

    val venueSavedDestinations = remember(savedVenuesFromCloud) {
        savedVenuesFromCloud.associate { it.venueName to it.destination }
    }

    LaunchedEffect(Unit) {
        eventViewModel.fetchUserEvents()
    }

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

    val formatBudgetShorthand: (Double) -> String = remember {
        { amount ->
            when {
                amount >= 10_000_000 -> "${(amount / 10_000_000).toLong()}Cr"
                amount >= 100_000 -> "${(amount / 100_000).toLong()}L"
                amount >= 1000 -> "${(amount / 1000).toLong()}k"
                else -> "${amount.toLong()}"
            }
        }
    }

    val amountText = remember(remainingFunds) { "₹${formatBudgetShorthand(remainingFunds)}" }

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
            } catch (_: Exception) {
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
        venueViewModel = venueViewModel,
        isVenuesLoading = isVenuesLoading,
        venueSavedDestinations = venueSavedDestinations,
        activeEvent = activeEvent
    )
}

@SuppressLint("ConfigurationScreenWidthHeight", "FrequentlyChangingValue")
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
    venueViewModel: VenueViewModel? = null,
    isVenuesLoading: Boolean = false,
    venueSavedDestinations: Map<String, String> = emptyMap(),
    activeEvent: com.harshdeep.jasnify.domain.model.Event? = null
) {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedCategory by remember { mutableStateOf<VendorCategoryItem?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val headerMediaItems = remember {
        listOf(
            HeaderMedia.ImageResource(R.drawable.bg_home),
            HeaderMedia.ImageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800"),
            HeaderMedia.VideoUrl("https://www.w3schools.com/html/mov_bbb.mp4"),
        )
    }

    var showSaveListBottomSheet by remember { mutableStateOf(false) }
    var activeTargetVenue by remember { mutableStateOf<com.harshdeep.jasnify.domain.model.Venue?>(null) }
    var isMySavedListChecked by remember { mutableStateOf(true) }
    var selectedSaveEventId by remember { mutableStateOf<String?>(null) }
    var toastData by remember { mutableStateOf<com.harshdeep.jasnify.presentation.components.others.ToastData?>(null) }
    var lastSavedVenue by remember { mutableStateOf<com.harshdeep.jasnify.domain.model.Venue?>(null) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val isViewer = remember(activeEvent) { false }

    val handleFavoriteToggle: (com.harshdeep.jasnify.domain.model.Venue) -> Unit = { venue ->
        val alreadySaved = venueSavedDestinations.containsKey(venue.name)
        if (alreadySaved) {
            if (activeEvent?.multiDay == true) {
                activeTargetVenue = venue
                val currentDestination = venueSavedDestinations[venue.name]
                isMySavedListChecked = currentDestination == "mysaved"
                selectedSaveEventId = if (currentDestination != "mysaved" && currentDestination != null) currentDestination else null
                showSaveListBottomSheet = true
            } else {
                venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, null)
                toastData = com.harshdeep.jasnify.presentation.components.others.ToastData("Removed from Saved List", com.harshdeep.jasnify.presentation.components.others.ToastType.DEFAULT)
            }
        } else {
            venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, "mysaved")
            lastSavedVenue = venue
            toastData = com.harshdeep.jasnify.presentation.components.others.ToastData("Added to Saved List!", com.harshdeep.jasnify.presentation.components.others.ToastType.DEFAULT)
        }
    }

    val trendingVenues = remember(venueSavedDestinations) {
        MockData.sampleVenues1.map { it.copy(favorite = venueSavedDestinations.containsKey(it.name)) }
    }

    val exploreVenues = remember(venueSavedDestinations) {
        MockData.sampleVenues2.map { it.copy(favorite = venueSavedDestinations.containsKey(it.name)) }
    }

    val timelineEvents = remember(activeEvent) {
        activeEvent?.subEvents?.map { subEvent ->
            val formattedDate = subEvent.date?.let { timestamp ->
                val sdf = java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault())
                sdf.format(java.util.Date(timestamp))
            } ?: "Date TBD"

            com.harshdeep.jasnify.domain.model.TimelineEvent(
                id = subEvent.id,
                date = formattedDate,
                event = subEvent.name,
                venues = emptyList()
            )
        } ?: emptyList()
    }

    LaunchedEffect(toastData?.message) {
        if (toastData?.message != null) {
            delay(3000.milliseconds)
            toastData = null
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeight = configuration.screenHeightDp.dp

    val headerHeight = remember(screenHeight) { screenHeight * 0.42f }
    val visibleBackgroundOffset = remember(screenHeight) { screenHeight * 0.24f }

    val navigateTo: (String) -> Unit = remember {
        { target ->
            coroutineScope.launch {
                delay(80.milliseconds)
                currentScreen = target
            }
        }
    }

    val homeScrollState = rememberScrollState()

    BackHandler(enabled = currentScreen != "home") {
        selectedCategory = null
        currentScreen = "home"
    }

    val fadeDistancePx = with(density) { visibleBackgroundOffset.toPx() }

    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

    val homeTabNestedScrollConnection = remember(fadeDistancePx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (currentScreen != "home") return Offset.Zero

                val delta = available.y
                val currentScroll = homeScrollState.value.toFloat()
                val halfSliderPx = fadeDistancePx / 2f

                if (delta > 0) {
                    if (scrollAccumulator < 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                } else if (delta < 0) {
                    if (scrollAccumulator > 0) scrollAccumulator = 0f
                    scrollAccumulator += delta
                }

                if (currentScroll < halfSliderPx) {
                    if (!isBottomBarVisible) {
                        isBottomBarVisible = true
                        onBottomBarVisibilityChange(true)
                    }
                    scrollAccumulator = 0f
                } else {
                    if (scrollAccumulator < -150f && isBottomBarVisible) {
                        isBottomBarVisible = false
                        onBottomBarVisibilityChange(false)
                        scrollAccumulator = 0f
                    } else if (scrollAccumulator > 150f && !isBottomBarVisible) {
                        isBottomBarVisible = true
                        onBottomBarVisibilityChange(true)
                        scrollAccumulator = 0f
                    }
                }

                return Offset.Zero
            }
        }
    }

    LaunchedEffect(currentScreen) {
        if (currentScreen == "home") {
            onBottomBarVisibilityChange(isBottomBarVisible)
        } else if (currentScreen != "vendors") {
            onBottomBarVisibilityChange(false)
        }
    }

    // Snap behavior on release
    LaunchedEffect(homeScrollState.isScrollInProgress) {
        if (!homeScrollState.isScrollInProgress) {
            val currentScroll = homeScrollState.value.toFloat()
            val halfThreshold = fadeDistancePx / 2f

            if (currentScroll > 1f && currentScroll < fadeDistancePx - 1f) {
                if (currentScroll >= halfThreshold) {
                    homeScrollState.animateScrollTo(fadeDistancePx.toInt())
                } else {
                    homeScrollState.animateScrollTo(0)
                }
            }
        }
    }

    val topBarAlphaState = remember {
        derivedStateOf {
            if (fadeDistancePx > 0f) {
                (homeScrollState.value / fadeDistancePx).coerceIn(0f, 1f)
            } else 0f
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(220))
                .togetherWith(fadeOut(animationSpec = tween(220)))
        },
        label = "screen_transition",
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (currentScreen == "home") Modifier.nestedScroll(homeTabNestedScrollConnection)
                else Modifier
            )
    ) { screen ->

        if (screen == "home") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPrimary)
            ) {
                if (homeScrollState.value < fadeDistancePx) {
                    HeaderMediaSlider(
                        mediaList = headerMediaItems,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .align(Alignment.TopCenter)
                            .graphicsLayer {
                                val scrollOffset = homeScrollState.value
                                translationY = -scrollOffset * PARALLAX_RATE
                                alpha = if (fadeDistancePx > 0f) {
                                    (1f - (scrollOffset / fadeDistancePx)).coerceIn(0f, 1f)
                                } else 1f
                            }
                    )
                }

                Scaffold(
                    topBar = {
                        HomeTopBar(
                            title = eventName,
                            dateString = eventDateString,
                            alpha = topBarAlphaState.value,
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
                            .verticalScroll(homeScrollState)
                    ) {
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
                                    venues = trendingVenues,
                                    isLoading = isVenuesLoading,
                                    onVenueClick = { navigateTo("venues") },
                                    cardSize = CompactCardSize.MEDIUM,
                                    onFavoriteToggle = handleFavoriteToggle,
                                    onSeeAllClick = { },
                                    onOfferClick = { }
                                )

                                VenueCarousel(
                                    title = "More Venues to Explore",
                                    venues = exploreVenues,
                                    isLoading = isVenuesLoading,
                                    onVenueClick = { navigateTo("venues") },
                                    onFavoriteToggle = handleFavoriteToggle,
                                    cardSize = CompactCardSize.MEDIUM,
                                    onSeeAllClick = { },
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

                if (showSaveListBottomSheet) {
                    com.harshdeep.jasnify.presentation.components.bottomdrawer.SaveListBottomSheet(
                        timelineEvents = timelineEvents,
                        isMySavedListChecked = isMySavedListChecked,
                        onMySavedListToggled = { checked ->
                            isMySavedListChecked = checked
                            if (checked) {
                                selectedSaveEventId = null
                            }
                        },
                        selectedEventId = selectedSaveEventId,
                        onEventSelected = { eventId ->
                            selectedSaveEventId = eventId
                            if (eventId != null) {
                                isMySavedListChecked = false
                            }
                        },
                        onAddNewEvent = { subEventItem ->
                            activeEvent?.let { event ->
                                val newSubEvent = com.harshdeep.jasnify.domain.model.SubEvent(
                                    id = subEventItem.id,
                                    name = subEventItem.name,
                                    date = subEventItem.date,
                                    completed = subEventItem.isCompleted
                                )
                                val updatedEvent = event.copy(subEvents = event.subEvents + newSubEvent)
                                eventViewModel?.updateEvent(updatedEvent)

                                activeTargetVenue?.let { venue ->
                                    venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, subEventItem.id)
                                }

                                selectedSaveEventId = subEventItem.id
                                isMySavedListChecked = false
                            }
                        },
                        isViewer = isViewer,
                        onDismiss = { showSaveListBottomSheet = false },
                        onDone = {
                            activeTargetVenue?.let { venue ->
                                val destination = if (isMySavedListChecked) "mysaved" else selectedSaveEventId
                                if (destination != null) {
                                    venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, destination)
                                    lastSavedVenue = venue
                                    toastData = com.harshdeep.jasnify.presentation.components.others.ToastData("Added to Saved List!", com.harshdeep.jasnify.presentation.components.others.ToastType.DEFAULT)
                                } else {
                                    venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, null)
                                    toastData = com.harshdeep.jasnify.presentation.components.others.ToastData("Removed from Saved List", com.harshdeep.jasnify.presentation.components.others.ToastType.DEFAULT)
                                }
                            }
                            showSaveListBottomSheet = false
                            activeTargetVenue = null
                        },
                        onProgress = { sheetMotionProgress = it }
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = toastData?.message != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    toastData?.let { data ->
                        com.harshdeep.jasnify.presentation.components.others.CustomToast(
                            message = data.message ?: "",
                            type = data.type
                        )
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


@SuppressLint("UseKtx")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderMediaSlider(
    mediaList: List<HeaderMedia>,
    modifier: Modifier = Modifier,
    autoSlideIntervalMs: Long = 4000L
) {
    if (mediaList.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { mediaList.size })
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    // Auto-slide loop
    LaunchedEffect(pagerState, mediaList.size) {
        if (!isPreview && mediaList.size > 1) {
            while (true) {
                delay(autoSlideIntervalMs.milliseconds)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % mediaList.size
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(durationMillis = 800)
                    )
                }
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val media = mediaList[page]) {
                is HeaderMedia.ImageResource -> {
                    Image(
                        painter = painterResource(id = media.resId),
                        contentDescription = "Header Slide Image ${page + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is HeaderMedia.ImageUrl -> {
                    if (isPreview) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_home),
                            contentDescription = "Header Image URL Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(media.url)
                                .crossfade(true)
                                .placeholder(R.drawable.bg_home)
                                .error(R.drawable.bg_home)
                                .build(),
                            contentDescription = "Header Slide Remote Image ${page + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                is HeaderMedia.VideoResource -> {
                    if (isPreview) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_home),
                            contentDescription = "Header Video Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setMediaController(null)
                                    val uri =
                                        ("android.resource://" + ctx.packageName + "/" + media.resId).toUri()
                                    setVideoURI(uri)
                                    setOnPreparedListener { mp ->
                                        mp.isLooping = true
                                        mp.setVolume(0f, 0f)
                                        start()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                is HeaderMedia.VideoUrl -> {
                    if (isPreview) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_home),
                            contentDescription = "Header Web Video Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setMediaController(null)
                                    setVideoURI(media.url.toUri())
                                    setOnPreparedListener { mp ->
                                        mp.isLooping = true
                                        mp.setVolume(0f, 0f)
                                        start()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Overlay box to pass touch events to HorizontalPager
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            )
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