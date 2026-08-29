package com.harshdeep.jasnify.presentation.screens.main.tabs.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.Offer
import com.harshdeep.jasnify.domain.model.SubEvent
import com.harshdeep.jasnify.domain.model.TimelineEvent
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.bottomdrawer.selection.OfferBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.selection.SaveListBottomSheet
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.navigation.ScreenTransitions
import com.harshdeep.jasnify.presentation.screens.budget.BudgetScreen
import com.harshdeep.jasnify.presentation.screens.catering.CateringMenuScreen
import com.harshdeep.jasnify.presentation.screens.invitation_cards.CardsScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorDetailScreen
import com.harshdeep.jasnify.presentation.screens.main.tabs.vendors.VendorsTab
import com.harshdeep.jasnify.presentation.screens.moments.MomentsScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueDetailScreen
import com.harshdeep.jasnify.presentation.screens.venues.VenueScreen
import com.harshdeep.jasnify.presentation.utils.SetStatusBarTheme
import com.harshdeep.jasnify.presentation.viewmodels.CardViewModel
import com.harshdeep.jasnify.presentation.viewmodels.EventViewModel
import com.harshdeep.jasnify.presentation.viewmodels.MomentsViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import androidx.core.net.toUri

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("FrequentlyChangingValue", "ConfigurationScreenWidthHeight")
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
    momentsViewModel: MomentsViewModel? = null,
    cardViewModel: CardViewModel? = null,
    isVenuesLoading: Boolean = false,
    venueSavedDestinations: Map<String, String> = emptyMap(),
    vendorSavedDestinations: Map<String, String> = emptyMap(),
    allVenues: List<Venue> = emptyList(),
    activeEvent: Event? = null,
    homeConfig: com.harshdeep.jasnify.domain.model.HomeScreenConfig? = null
) {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedCategory by remember { mutableStateOf<VendorCategoryItem?>(null) }
    var selectedVenueForDetail by remember { mutableStateOf<Venue?>(null) }
    var selectedVendorForDetail by remember { mutableStateOf<Vendor?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val headerMediaItems = remember(homeConfig) {
        val hardcodedDefaults = listOf(
            HeaderMedia.ImageResource(R.drawable.app_hero_display_default)
        )

        val remoteItems = homeConfig?.topSlider?.filter { it.isActive && it.mediaUrl.isNotBlank() }?.map {
            val url = it.mediaUrl.trim()
            val cleanUrl = url.substringBefore('?').lowercase()

            when {
                cleanUrl.endsWith(".mp4") || cleanUrl.endsWith(".webm") || cleanUrl.endsWith(".mkv") -> {
                    HeaderMedia.VideoUrl(url, it.actionType, it.targetRoute, it.contentColor, it.autoSlideDuration)
                }
                cleanUrl.endsWith(".json") -> {
                    HeaderMedia.LottieUrl(url, it.actionType, it.targetRoute, it.contentColor, it.autoSlideDuration)
                }
                cleanUrl.endsWith(".gif") -> {
                    HeaderMedia.GifUrl(url, it.actionType, it.targetRoute, it.contentColor, it.autoSlideDuration)
                }
                else -> {
                    HeaderMedia.ImageUrl(url, it.actionType, it.targetRoute, it.contentColor, it.autoSlideDuration)
                }
            }
        } ?: emptyList()

        remoteItems.ifEmpty {
            hardcodedDefaults
        }
    }

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

    val targetHeaderColor = remember(headerPagerState.currentPage, headerMediaItems) {
        if (headerMediaItems.isEmpty()) Color.White
        else {
            val actualIndex = headerPagerState.currentPage % headerMediaItems.size
            val colorHex = headerMediaItems[actualIndex].contentColor
            if (colorHex.isNullOrBlank()) Color.White
            else {
                try {
                    Color(colorHex.toColorInt())
                } catch (_: Exception) {
                    Color.White
                }
            }
        }
    }

    val currentHeaderColor by animateColorAsState(
        targetValue = targetHeaderColor,
        animationSpec = tween(durationMillis = 600),
        label = "headerColorTransition"
    )

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
    val backdropScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )
    val backdropCornerRadius by animateDpAsState(
        targetValue = if (isAnySheetVisible) 32.dp else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    val isSavedListToast = remember(toastData, lastSavedVenue, lastSavedVendor) {
        toastData?.message?.contains("Saved List") == true && (lastSavedVenue != null || lastSavedVendor != null)
    }

    val isOwner = activeEvent?.ownerId == FirebaseAuth.getInstance().currentUser?.uid
    val isViewer = !isOwner

    val handleVenueFavoriteToggle: (Venue) -> Unit = remember(venueSavedDestinations, activeEvent, isViewer) {
        { venue ->
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
    }

    val handleVendorFavoriteToggle: (Vendor) -> Unit = remember(vendorSavedDestinations, activeEvent, isViewer) {
        { vendor ->
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
    }

    val trendingVenues = remember(allVenues, venueSavedDestinations) {
        allVenues.take(5).map { it.copy(favorite = venueSavedDestinations.containsKey(it.name)) }
    }

    val exploreVenues = remember(allVenues, venueSavedDestinations) {
        allVenues.drop(5).map { it.copy(favorite = venueSavedDestinations.containsKey(it.name)) }
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

    val headerHeight = remember(screenHeight) { screenHeight * 0.46f }
    val visibleBackgroundOffset = remember(screenHeight) { screenHeight * 0.28f }
    val fadeDistancePx = with(density) { visibleBackgroundOffset.toPx() }

    val context = LocalContext.current
    val navigateTo: (String) -> Unit = remember(context) {
        { target ->
            coroutineScope.launch {
                delay(80.milliseconds)
                
                when {
                    target.startsWith("http://") || target.startsWith("https://") -> {
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW,
                                target.toUri())
                            context.startActivity(intent)
                        } catch (_: Exception) { }
                    }
                    target.startsWith("vendors_") || target == "vendors" -> {
                        currentScreen = "vendors"
                    }
                    target.startsWith("cards") -> {
                        currentScreen = "cards"
                    }
                    target.startsWith("venues") -> {
                        currentScreen = "venues"
                    }
                    else -> {
                        currentScreen = target
                    }
                }
            }
        }
    }

    val lazyListState = rememberLazyListState()

    // Smooth lambda provider to eliminate scroll offset recompositions at root
    val getScrollOffset: () -> Float = remember(lazyListState, fadeDistancePx) {
        {
            if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                fadeDistancePx
            }
        }
    }

    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollAccumulator by remember { mutableFloatStateOf(0f) }

    val currentIsAnySheetVisible by rememberUpdatedState(isAnySheetVisible)
    val currentScreenState by rememberUpdatedState(currentScreen)

    val homeTabNestedScrollConnection = remember(fadeDistancePx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (currentScreenState != "home" || currentIsAnySheetVisible) return Offset.Zero

                val delta = available.y
                val currentScroll = getScrollOffset()
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

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // Perform snap ONLY at rest if user stopped mid-header without high residual velocity
                val totalVelocity = abs(consumed.y) + abs(available.y)
                if (totalVelocity < 500f && lazyListState.firstVisibleItemIndex == 0) {
                    val currentScroll = getScrollOffset()
                    val threshold = fadeDistancePx * 0.45f

                    if (currentScroll > 1f && currentScroll < fadeDistancePx - 1f) {
                        if (currentScroll >= threshold) {
                            lazyListState.animateScrollToItem(1)
                        } else {
                            lazyListState.animateScrollToItem(0)
                        }
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    BackHandler(enabled = currentScreen != "home") {
        when (currentScreen) {
            "venue_detail" -> {
                selectedVenueForDetail = null
                currentScreen = "home"
            }
            "vendor_detail" -> {
                selectedVendorForDetail = null
                currentScreen = "home"
            }
            else -> {
                selectedCategory = null
                currentScreen = "home"
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

    val topBarAlphaState = remember {
        derivedStateOf {
            if (fadeDistancePx > 0f) {
                (getScrollOffset() / fadeDistancePx).coerceIn(0f, 1f)
            } else 0f
        }
    }

    val useDarkIcons = remember {
        derivedStateOf {
            if (currentScreen != "home") {
                true
            } else {
                val alpha = topBarAlphaState.value
                if (alpha > 0.5f) {
                    true
                } else {
                    targetHeaderColor.luminance() <= 0.5f
                }
            }
        }
    }

    SetStatusBarTheme(useDarkIcons = useDarkIcons.value)

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (initialState == "home") {
                ScreenTransitions.ZoomDepthForwardTransition
            } else {
                ScreenTransitions.ZoomDepthReturnTransition
            }
        },
        label = "screen_zoom_transition",
        modifier = Modifier.fillMaxSize()
    ) { screen ->

        if (screen == "home") {
            HomeMainView(
                eventName = eventName,
                eventDateString = eventDateString,
                remainingPercentage = remainingPercentage,
                amountText = amountText,
                backdropScale = backdropScale,
                backdropCornerRadius = backdropCornerRadius,
                isAnySheetVisible = isAnySheetVisible,
                scrollOffsetProvider = getScrollOffset,
                fadeDistancePx = fadeDistancePx,
                visibleBackgroundOffset = visibleBackgroundOffset,
                headerHeight = headerHeight,
                headerMediaItems = headerMediaItems,
                headerPagerState = headerPagerState,
                currentHeaderColor = currentHeaderColor,
                topBarAlphaProvider = { topBarAlphaState.value },
                lazyListState = lazyListState,
                nestedScrollConnection = homeTabNestedScrollConnection,
                toastData = toastData,
                isSavedListToast = isSavedListToast,
                isMultiDay = activeEvent?.multiDay ?: false,
                isVenuesLoading = isVenuesLoading,
                trendingVenues = trendingVenues,
                exploreVenues = exploreVenues,
                onMenuClick = onMenuClick,
                onNavigate = navigateTo,
                onCategoryClick = { category ->
                    selectedCategory = category
                    currentScreen = "vendors"
                },
                onVenueClick = { venue ->
                    selectedVenueForDetail = venue
                    currentScreen = "venue_detail"
                },
                onVenueFavoriteToggle = handleVenueFavoriteToggle,
                onVendorFavoriteToggle = handleVendorFavoriteToggle,
                onOfferClick = { venue ->
                    offersToShow = venue.offers
                    showOfferSheet = true
                },
                onToastChange = { toastData = it },
                onSaveListChange = { venue, vendor ->
                    if (venue != null) lastSavedVenue = venue
                    if (vendor != null) lastSavedVendor = vendor
                }
            )

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
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                when (screen) {
                    "budget" -> eventViewModel?.let { vm ->
                        BudgetScreen(
                            onBackClick = { currentScreen = "home" },
                            navController = mainNavController,
                            eventViewModel = vm
                        )
                    }
                    "venues" -> eventViewModel?.let { vm ->
                        VenueScreen(
                            selectedLocation = "City, State",
                            onVenueClick = { venue ->
                                selectedVenueForDetail = venue
                                currentScreen = "venue_detail"
                            },
                            onChatClick = { venue ->
                                val merchantId = venue.merchantId.ifBlank { "unknown_merchant" }
                                val itemId = venue.id.ifBlank { "unknown_venue" }
                                mainNavController.navigate("chat_screen/$merchantId/$itemId?itemType=Venue")
                            },
                            onBackClick = { currentScreen = "home" },
                            navController = mainNavController,
                            eventViewModel = vm
                        )
                    }
                    "catering" -> eventViewModel?.let { vm ->
                        CateringMenuScreen(
                            onBackClick = { currentScreen = "home" },
                            navController = mainNavController,
                            eventViewModel = vm
                        )
                    }
                    "cards" -> {
                        CardsScreen(
                            onBackClick = { currentScreen = "home" },
                            navController = mainNavController,
                            eventViewModel = eventViewModel ?: hiltViewModel(),
                            roomViewModel = roomViewModel ?: hiltViewModel(),
                            cardViewModel = cardViewModel ?: hiltViewModel()
                        )
                    }
                    "moments" -> {
                        MomentsScreen(
                            onBackClick = { currentScreen = "home" },
                            navController = mainNavController,
                            viewModel = momentsViewModel ?: hiltViewModel()
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
                                    val merchantId = vendorChat.merchantId.ifBlank { "unknown_merchant" }
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
