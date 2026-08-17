package com.harshdeep.jasnify.presentation.screens.main.tabs.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.input.pointer.pointerInput
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.Offer
import com.harshdeep.jasnify.domain.model.SubEvent
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.bottomdrawer.OfferBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.SaveListBottomSheet
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.HomeCard
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.VideoPlayer
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.HomeTopBar
import com.harshdeep.jasnify.presentation.components.sections.ExploreCategoriesHorizontal
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.carousels.VenueCarousel
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.sections.vendorCategories
import com.harshdeep.jasnify.presentation.screens.budget.BudgetScreen
import com.harshdeep.jasnify.presentation.screens.invitation_cards.CardsScreen
import com.harshdeep.jasnify.presentation.screens.catering.CateringMenuScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorsTab
import com.harshdeep.jasnify.presentation.screens.venues.VenueDetailScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen
import com.harshdeep.jasnify.presentation.viewmodels.BudgetViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
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
    venueViewModel: VenueViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val activeEvent by eventViewModel.activeEvent.collectAsStateWithLifecycle()
    val isVenuesLoading by venueViewModel.isLoading.collectAsStateWithLifecycle()
    val savedVenuesFromCloud by venueViewModel.savedVenues.collectAsStateWithLifecycle()
    val savedVendorsFromCloud by vendorViewModel.savedVendors.collectAsStateWithLifecycle()

    val venueSavedDestinations = remember(savedVenuesFromCloud) {
        savedVenuesFromCloud.associate { it.venueName to it.destination }
    }

    val vendorSavedDestinations = remember(savedVendorsFromCloud) {
        savedVendorsFromCloud.associate { "${it.vendorName}-${it.category}" to it.destination }
    }

    LaunchedEffect(activeEvent?.id) {
        activeEvent?.id?.let { id ->
            budgetViewModel.setEventId(id)
            venueViewModel.setEventId(id)
            vendorViewModel.setEventId(id)
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

    val remainingFunds = remember(totalBudget, totalSpent) {
        (totalBudget - totalSpent).coerceAtLeast(0.0)
    }
    
    val remainingPercentage = remember(totalBudget, remainingFunds) {
        if (totalBudget > 0) (remainingFunds / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    }

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
        vendorViewModel = vendorViewModel,
        roomViewModel = roomViewModel,
        isVenuesLoading = isVenuesLoading,
        venueSavedDestinations = venueSavedDestinations,
        vendorSavedDestinations = vendorSavedDestinations,
        activeEvent = activeEvent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("ConfigurationScreenWidthHeight", "FrequentlyChangingValue")
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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
    vendorViewModel: VendorViewModel? = null,
    roomViewModel: RoomViewModel? = null,
    isVenuesLoading: Boolean = false,
    venueSavedDestinations: Map<String, String> = emptyMap(),
    vendorSavedDestinations: Map<String, String> = emptyMap(),
    activeEvent: Event? = null
) {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedCategory by remember { mutableStateOf<VendorCategoryItem?>(null) }
    var selectedVenueForDetail by remember { mutableStateOf<Venue?>(null) }
    var selectedVendorForDetail by remember { mutableStateOf<Vendor?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Header media items featuring local drawables, remote Image URLs, and remote Video URLs
    val headerMediaItems = remember {
        listOf(
            HeaderMedia.ImageResource(R.drawable.bg_home),
            HeaderMedia.ImageUrl("https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=1200&q=80"),
            HeaderMedia.ImageUrl("https://images.unsplash.com/photo-1511285560929-80b456fea0bc?auto=format&fit=crop&w=1200&q=80"),
            HeaderMedia.ImageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=1200&q=80"),
            HeaderMedia.VideoUrl("https://www.w3schools.com/html/mov_bbb.mp4")
        )
    }

    // Infinite virtual page configuration for seamless forward looping
    val virtualPageCount = remember(headerMediaItems.size) {
        if (headerMediaItems.size > 1) Int.MAX_VALUE else headerMediaItems.size
    }
    val initialPage = remember(headerMediaItems.size) {
        if (headerMediaItems.size > 1) (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % headerMediaItems.size) else 0
    }

    val headerPagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualPageCount }
    )

    var totalHeaderDragX by remember { mutableFloatStateOf(0f) }

    var showSaveListBottomSheet by remember { mutableStateOf(false) }
    var showOfferSheet by remember { mutableStateOf(false) }
    var offersToShow by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var activeTargetVenue by remember { mutableStateOf<Venue?>(null) }
    var activeTargetVendor by remember { mutableStateOf<Vendor?>(null) }
    var isMySavedListChecked by remember { mutableStateOf(true) }
    var selectedSaveEventId by remember { mutableStateOf<String?>(null) }
    var toastData by remember { mutableStateOf<ToastData?>(null) }
    var lastSavedVenue by remember { mutableStateOf<Venue?>(null) }
    var lastSavedVendor by remember { mutableStateOf<Vendor?>(null) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0.0f) }

    val isAnySheetVisible = showSaveListBottomSheet || showOfferSheet
    val targetScale = if (isAnySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f
    val backdropScale by animateFloatAsState(targetValue = targetScale, animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f), label = "backdropScale")
    val backdropCornerRadius by animateDpAsState(targetValue = if (isAnySheetVisible) CornerExtraLarge else 0.dp, animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy), label = "backdropCornerRadius")

    val isSavedListToast = remember(toastData, lastSavedVenue, lastSavedVendor) {
        toastData?.message?.contains("Saved List") == true && (lastSavedVenue != null || lastSavedVendor != null)
    }

    val isOwner = activeEvent?.ownerId == FirebaseAuth.getInstance().currentUser?.uid
    // For simplicity in HomeTab, we assume owner for now or fetch role if needed.
    // Ideally use RoomViewModel to get exact role, but false is safe for viewers.
    val isViewer = !isOwner

    val handleVenueFavoriteToggle: (Venue) -> Unit = { venue ->
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
                lastSavedVenue = null
                lastSavedVendor = null
                toastData = ToastData("Removed from Saved List", ToastType.DEFAULT)
            }
        } else {
            venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, "mysaved")
            lastSavedVenue = venue
            lastSavedVendor = null
            toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
        }
    }

    val handleVendorFavoriteToggle: (Vendor) -> Unit = { vendor ->
        val alreadySaved = vendorSavedDestinations.containsKey("${vendor.name}-${vendor.category}")
        if (alreadySaved) {
            if (activeEvent?.multiDay == true) {
                activeTargetVendor = vendor
                val currentDestination = vendorSavedDestinations["${vendor.name}-${vendor.category}"]
                isMySavedListChecked = currentDestination == "mysaved"
                selectedSaveEventId = if (currentDestination != "mysaved" && currentDestination != null) currentDestination else null
                showSaveListBottomSheet = true
            } else {
                vendorViewModel?.toggleSaveVendor(vendor, isViewer, null)
                lastSavedVenue = null
                lastSavedVendor = null
                toastData = ToastData("Removed from Saved List", ToastType.DEFAULT)
            }
        } else {
            vendorViewModel?.toggleSaveVendor(vendor, isViewer, "mysaved")
            lastSavedVendor = vendor
            lastSavedVenue = null
            toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
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
                val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                sdf.format(Date(timestamp))
            } ?: "Date TBD"

            TimelineEvent(
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
    val fadeDistancePx = with(density) { visibleBackgroundOffset.toPx() }

    val navigateTo: (String) -> Unit = remember {
        { target ->
            coroutineScope.launch {
                delay(80.milliseconds)
                currentScreen = target
            }
        }
    }

    val lazyListState = rememberLazyListState()

    val scrollOffset by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                fadeDistancePx
            }
        }
    }

    BackHandler(enabled = currentScreen != "home") {
        if (currentScreen == "venue_detail") {
            selectedVenueForDetail = null
            currentScreen = "home"
        } else if (currentScreen == "vendor_detail") {
            selectedVendorForDetail = null
            currentScreen = "home"
        } else {
            selectedCategory = null
            currentScreen = "home"
        }
    }

    val fadeDistancePxVal = fadeDistancePx // Avoid ambiguity in connection

    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

    val homeTabNestedScrollConnection = remember(fadeDistancePxVal, isAnySheetVisible) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (currentScreen != "home" || isAnySheetVisible) return Offset.Zero

                val delta = available.y
                val currentScroll = scrollOffset
                val halfSliderPx = fadeDistancePxVal / 2f

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

    LaunchedEffect(currentScreen, isAnySheetVisible, isBottomBarVisible) {
        if (isAnySheetVisible) {
            onBottomBarVisibilityChange(false)
        } else {
            if (currentScreen == "home") {
                onBottomBarVisibilityChange(isBottomBarVisible)
            } else if (currentScreen != "vendors") {
                onBottomBarVisibilityChange(false)
            }
        }
    }

    // Snap behavior on release
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val currentScroll = scrollOffset
            val halfThreshold = fadeDistancePxVal / 2f

            if (currentScroll > 1f && currentScroll < fadeDistancePxVal - 1f) {
                if (currentScroll >= halfThreshold) {
                    lazyListState.animateScrollToItem(1)
                } else {
                    lazyListState.animateScrollToItem(0)
                }
            }
        }
    }

    val topBarAlphaState = remember {
        derivedStateOf {
            if (fadeDistancePxVal > 0f) {
                (scrollOffset / fadeDistancePxVal).coerceIn(0f, 1f)
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
    ) { screen ->

        if (screen == "home") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = backdropScale
                            scaleY = backdropScale
                            clip = isAnySheetVisible || backdropCornerRadius > 0.dp
                            shape = RoundedCornerShape(backdropCornerRadius.coerceAtLeast(0.dp))
                        }
                        .background(BackgroundPrimary)
                        .then(
                            if (currentScreen == "home") Modifier.nestedScroll(homeTabNestedScrollConnection)
                            else Modifier
                        )
                ) {
                    if (scrollOffset < fadeDistancePxVal) {
                    HeaderMediaSlider(
                        mediaList = headerMediaItems,
                        pagerState = headerPagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .align(Alignment.TopCenter)
                            .graphicsLayer {
                                val currentOffset = scrollOffset
                                translationY = -currentOffset * PARALLAX_RATE
                                alpha = if (fadeDistancePxVal > 0f) {
                                    (1f - (currentOffset / fadeDistancePxVal)).coerceIn(0f, 1f)
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        state = lazyListState
                    ) {
                        item(key = "header_spacer") {
                            Spacer(modifier = Modifier.height(visibleBackgroundOffset))
                        }

                        item(key = "budget_card") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = BackgroundPrimary,
                                        shape = RoundedCornerShape(topStart = CornerExtraLarge, topEnd = CornerExtraLarge)
                                    )
                                    .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 4.dp)
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
                            }
                        }

                        item(key = "row_1_cards") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BackgroundPrimary)
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
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
                        }

                        item(key = "row_2_cards") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BackgroundPrimary)
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
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
                                    heading = "Cards",
                                    illustration = painterResource(R.drawable.ill_cards_and_guests_card),
                                    modifier = Modifier.weight(1f),
                                    cardBgColor = Color(0xFFE8D0CE),
                                    waveColor = Color(0x1A5D0501).copy(alpha = 0.9f),
                                    insightColor = Color(0xFF5D1D1B),
                                    onClick = { navigateTo("cards") }
                                )
                            }
                        }

                        item(key = "explore_divider") {
                            OrDivider(dividerGap = 12.dp, text = "EXPLORE", modifier = Modifier.background(BackgroundPrimary).padding(horizontal = 12.dp, vertical = 8.dp))
                        }

                        item(key = "trending_venues") {
                            VenueCarousel(
                                title = "Trending Venues in Patna",
                                venues = trendingVenues,
                                isLoading = isVenuesLoading,
                                onVenueClick = { venue ->
                                    selectedVenueForDetail = venue
                                    currentScreen = "venue_detail"
                                },
                                cardSize = CompactCardSize.MEDIUM,
                                onFavoriteToggle = handleVenueFavoriteToggle,
                                onSeeAllClick = { navigateTo("venues") },
                                onOfferClick = { venue ->
                                    offersToShow = venue.offers
                                    showOfferSheet = true
                                },
                                modifier = Modifier.background(BackgroundPrimary)
                            )
                        }

                        item(key = "more_venues") {
                            VenueCarousel(
                                title = "More Venues to Explore",
                                venues = exploreVenues,
                                isLoading = isVenuesLoading,
                                onVenueClick = { venue ->
                                    selectedVenueForDetail = venue
                                    currentScreen = "venue_detail"
                                },
                                onFavoriteToggle = handleVenueFavoriteToggle,
                                cardSize = CompactCardSize.MEDIUM,
                                onSeeAllClick = { navigateTo("venues") },
                                onOfferClick = { venue ->
                                    offersToShow = venue.offers
                                    showOfferSheet = true
                                },
                                modifier = Modifier.background(BackgroundPrimary)
                            )
                        }

                        item(key = "dashed_divider") {
                            DashedDivider(modifier = Modifier.background(BackgroundPrimary))
                        }

                        item(key = "vendor_categories") {
                            ExploreCategoriesHorizontal(
                                categories = vendorCategories,
                                onCategoryClick = { category ->
                                    selectedCategory = category
                                    navigateTo("vendors")
                                }
                            )
                        }

                        item(key = "footer") {
                            FooterJansify(modifier = Modifier.background(BackgroundPrimary))
                        }
                    }
                }

                // Drag Gesture Overlay placed on top of Scaffold content
                if (scrollOffset < fadeDistancePxVal) {
                    val topBarInset = 80.dp
                    val overlayHeight = (headerHeight - topBarInset).coerceAtLeast(0.dp)

                    if (overlayHeight > 0.dp) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = topBarInset)
                                .height(overlayHeight)
                                .align(Alignment.TopCenter)
                                .pointerInput(headerPagerState) {
                                    detectHorizontalDragGestures(
                                        onDragStart = { totalHeaderDragX = 0f },
                                        onDragEnd = {
                                            coroutineScope.launch {
                                                if (totalHeaderDragX < -60f) {
                                                    headerPagerState.animateScrollToPage(headerPagerState.currentPage + 1)
                                                } else if (totalHeaderDragX > 60f) {
                                                    headerPagerState.animateScrollToPage(headerPagerState.currentPage - 1)
                                                } else {
                                                    headerPagerState.animateScrollToPage(headerPagerState.currentPage)
                                                }
                                            }
                                        },
                                        onDragCancel = {
                                            coroutineScope.launch {
                                                headerPagerState.animateScrollToPage(headerPagerState.currentPage)
                                            }
                                        },
                                        onHorizontalDrag = { change, dragAmount ->
                                            change.consume()
                                            totalHeaderDragX += dragAmount
                                            coroutineScope.launch {
                                                headerPagerState.dispatchRawDelta(-dragAmount)
                                            }
                                        }
                                    )
                                }
                        )
                    }
                }
            } // End scaling box

            if (showOfferSheet) {
                OfferBottomSheet(
                    offers = offersToShow,
                    onDismiss = { showOfferSheet = false },
                    onProgress = { sheetMotionProgress = it }
                )
            }

            if (showSaveListBottomSheet) {
                    SaveListBottomSheet(
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
                                val newSubEvent = SubEvent(
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
                                activeTargetVendor?.let { vendor ->
                                    vendorViewModel?.toggleSaveVendor(vendor, isViewer, subEventItem.id)
                                }

                                selectedSaveEventId = subEventItem.id
                                isMySavedListChecked = false
                            }
                        },
                        isViewer = isViewer,
                        onDismiss = { 
                            showSaveListBottomSheet = false
                            activeTargetVenue = null
                            activeTargetVendor = null
                        },
                        onDone = {
                            val destination = if (isMySavedListChecked) "mysaved" else selectedSaveEventId
                            activeTargetVenue?.let { venue ->
                                if (destination != null) {
                                    venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, destination)
                                    lastSavedVenue = venue
                                    lastSavedVendor = null
                                    toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
                                } else {
                                    venueViewModel?.toggleSaveVenue(venue.name, venue.id, isViewer, null)
                                    lastSavedVenue = null
                                    lastSavedVendor = null
                                    toastData = ToastData("Removed from Saved List", ToastType.DEFAULT)
                                }
                            }
                            activeTargetVendor?.let { vendor ->
                                if (destination != null) {
                                    vendorViewModel?.toggleSaveVendor(vendor, isViewer, destination)
                                    lastSavedVendor = vendor
                                    lastSavedVenue = null
                                    toastData = ToastData("Added to Saved List!", ToastType.DEFAULT)
                                } else {
                                    vendorViewModel?.toggleSaveVendor(vendor, isViewer, null)
                                    lastSavedVendor = null
                                    lastSavedVenue = null
                                    toastData = ToastData("Removed from Saved List", ToastType.DEFAULT)
                                }
                            }
                            showSaveListBottomSheet = false
                            activeTargetVenue = null
                            activeTargetVendor = null
                        },
                        onProgress = { sheetMotionProgress = it }
                    )
                }

                AnimatedVisibility(
                    visible = toastData?.message != null && isSavedListToast,
                    enter = slideInVertically(initialOffsetY = { it + 500 }),
                    exit = slideOutVertically(targetOffsetY = { it + 500 }),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    toastData?.let { data ->
                        CustomToast(
                            message = data.message ?: "",
                            type = data.type,
                            leadingIcon = painterResource(id = R.drawable.ic_heart_filled),
                            buttonText = if (activeEvent?.multiDay == true) "Change" else null,
                            onButtonClick = if (activeEvent?.multiDay == true) {
                                {
                                    toastData = null
                                    if (lastSavedVenue != null) {
                                        val venue = lastSavedVenue!!
                                        activeTargetVenue = venue
                                        activeTargetVendor = null
                                        val currentDest = venueSavedDestinations[venue.name]
                                        isMySavedListChecked = currentDest == "mysaved"
                                        selectedSaveEventId = if (currentDest != "mysaved" && currentDest != null) currentDest else null
                                        showSaveListBottomSheet = true
                                    } else if (lastSavedVendor != null) {
                                        val vendor = lastSavedVendor!!
                                        activeTargetVendor = vendor
                                        activeTargetVenue = null
                                        val currentDest = vendorSavedDestinations["${vendor.name}-${vendor.category}"]
                                        isMySavedListChecked = currentDest == "mysaved"
                                        selectedSaveEventId = if (currentDest != "mysaved" && currentDest != null) currentDest else null
                                        showSaveListBottomSheet = true
                                    }
                                }
                            } else null
                        )
                    }
                }

                AnimatedVisibility(
                    visible = toastData?.message != null && !isSavedListToast,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    toastData?.let { data ->
                        CustomToast(
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
                            onVenueClick = { venue ->
                                selectedVenueForDetail = venue
                            },
                            onChatClick = { venue ->
                                val merchantId = venue.merchantId.ifBlank { "unknown_merchant" }
                                val itemId = venue.id.ifBlank { "unknown_venue" }
                                mainNavController.navigate("chat_screen/$merchantId/$itemId?itemType=Venue")
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
                    "cards" -> {
                        CardsScreen(
                            onBackClick = { currentScreen = "home" },
                        )
                    }
                    "vendors" -> {
                        VendorsTab(
                            mainNavController = mainNavController,
                            internalNavController = internalNavController,
                            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                            onChatClick = { vendor ->
                                val merchantId = vendor.merchantId.ifBlank { "unknown_merchant" }
                                val itemId = vendor.id.ifBlank { "unknown_vendor" }
                                mainNavController.navigate("chat_screen/$merchantId/$itemId?itemType=Vendor")
                            },
                            initialCategory = selectedCategory,
                            onBackClick = {
                                selectedCategory = null
                                currentScreen = "home"
                            },
                            eventViewModel = eventViewModel ?: hiltViewModel(),
                            roomViewModel = roomViewModel ?: hiltViewModel(),
                            vendorViewModel = vendorViewModel ?: hiltViewModel()
                        )
                    }
                    "venue_detail" -> {
                        selectedVenueForDetail?.let { venue ->
                            VenueDetailScreen(
                                venueDetail = venue,
                                onBackClick = {
                                    selectedVenueForDetail = null
                                    currentScreen = "home"
                                },
                                onFavoriteToggle = {
                                    handleVenueFavoriteToggle(venue)
                                },
                                onChatClick = { venueChat ->
                                    val merchantId = venueChat.merchantId.ifBlank { "unknown_merchant" }
                                    val itemId = venueChat.id.ifBlank { "unknown_venue" }
                                    mainNavController.navigate("chat_screen/$merchantId/$itemId?itemType=Venue")
                                }
                            )
                        }
                    }
                    "vendor_detail" -> {
                        selectedVendorForDetail?.let { vendor ->
                            VendorDetailScreen(
                                vendorDetail = vendor,
                                onBackClick = {
                                    selectedVendorForDetail = null
                                    currentScreen = "home"
                                },
                                onChatClick = { vendorChat ->
                                    val merchantId =
                                        vendorChat.merchantId.ifBlank { "unknown_merchant" }
                                    val itemId = vendorChat.id.ifBlank { "unknown_vendor" }
                                    mainNavController.navigate("chat_screen/$merchantId/$itemId?itemType=Vendor")
                                },
                                onFavoriteToggle = { handleVendorFavoriteToggle(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderMediaSlider(
    mediaList: List<HeaderMedia>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    autoSlideIntervalMs: Long = 4000L
) {
    if (mediaList.isEmpty()) return

    val context = LocalContext.current

    // Continuous auto-slide forward loop (always moving left-to-right)
    LaunchedEffect(pagerState, mediaList.size) {
        if (mediaList.size > 1) {
            while (true) {
                delay(autoSlideIntervalMs.milliseconds)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = pagerState.currentPage + 1
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
        val actualIndex = page % mediaList.size

        Box(modifier = Modifier.fillMaxSize()) {
            when (val media = mediaList[actualIndex]) {
                is HeaderMedia.ImageResource -> {
                    Image(
                        painter = painterResource(id = media.resId),
                        contentDescription = "Header Slide Image ${actualIndex + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is HeaderMedia.ImageUrl -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(media.url)
                            .crossfade(true)
                            .placeholder(R.drawable.bg_home)
                            .error(R.drawable.bg_home)
                            .build(),
                        contentDescription = "Header Slide Remote Image ${actualIndex + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is HeaderMedia.VideoResource -> {
                    VideoPlayer(
                        videoUrl = "android.resource://" + context.packageName + "/" + media.resId,
                        modifier = Modifier.fillMaxSize(),
                        isMuted = true,
                        autoPlay = true,
                        isLooping = true
                    )
                }
                is HeaderMedia.VideoUrl -> {
                    VideoPlayer(
                        videoUrl = media.url,
                        modifier = Modifier.fillMaxSize(),
                        isMuted = true,
                        autoPlay = true,
                        isLooping = true
                    )
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