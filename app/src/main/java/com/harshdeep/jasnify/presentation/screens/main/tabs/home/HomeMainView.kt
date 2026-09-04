package com.harshdeep.jasnify.presentation.screens.main.tabs.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.local.prefs.PreferenceManager
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.presentation.components.cards.BudgetTrackerCard
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.HomeCard
import com.harshdeep.jasnify.presentation.components.carousels.VendorCarousel
import com.harshdeep.jasnify.presentation.components.carousels.VenueCarousel
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.FeatureOnboardingOverlay
import com.harshdeep.jasnify.presentation.components.others.OnboardingStep
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.onboardingTarget
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.HomeTopBar
import com.harshdeep.jasnify.presentation.components.sections.ExploreCategoriesHorizontal
import com.harshdeep.jasnify.presentation.components.sections.VendorCategoryItem
import com.harshdeep.jasnify.presentation.components.sections.vendorCategories
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

private const val PARALLAX_RATE = 0.5f

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun HomeMainView(
    eventName: String,
    eventDateString: String,
    remainingPercentage: Float,
    amountText: String,
    backdropScale: Float,
    backdropCornerRadius: Dp,
    isAnySheetVisible: Boolean,
    scrollOffsetProvider: () -> Float,
    fadeDistancePx: Float,
    visibleBackgroundOffset: Dp,
    headerHeight: Dp,
    headerMediaItems: List<HeaderMedia>,
    headerPagerState: PagerState,
    currentHeaderColor: Color,
    topBarAlphaProvider: () -> Float,
    lazyListState: LazyListState,
    nestedScrollConnection: androidx.compose.ui.input.nestedscroll.NestedScrollConnection,
    toastData: ToastData?,
    isSavedListToast: Boolean,
    isMultiDay: Boolean,
    selectedLocation: String = "City, State",
    isVenuesLoading: Boolean = false,
    trendingVenues: List<Venue> = emptyList(),
    isVendorsLoading: Boolean = false,
    trendingVendors: List<Vendor> = emptyList(),
    onMenuClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onCategoryClick: (VendorCategoryItem) -> Unit,
    onVenueClick: (Venue) -> Unit,
    onVendorClick: (Vendor) -> Unit = {},
    onVenueFavoriteToggle: (Venue) -> Unit,
    onVendorFavoriteToggle: (Vendor) -> Unit,
    onOfferClick: (Venue) -> Unit,
    onVendorOfferClick: (Vendor) -> Unit = {},
    onToastChange: (ToastData?) -> Unit,
    onSaveListChange: (Venue?, Vendor?) -> Unit,
    onBottomBarVisibilityChange: ((Boolean) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    var isHomeOnboardingActive by remember { mutableStateOf(!prefManager.hasCompletedScreenOnboarding("home")) }
    var homeStepIndex by remember { mutableIntStateOf(0) }
    val homeTargetRects = remember { mutableStateMapOf<String, androidx.compose.ui.geometry.Rect>() }

    LaunchedEffect(isHomeOnboardingActive) {
        onBottomBarVisibilityChange?.invoke(!isHomeOnboardingActive)
    }

    val homeOnboardingSteps = remember {
        listOf(
            OnboardingStep(
                stepKey = "home_menu_button",
                title = "Event Details & Settings",
                description = "Tap the menu button to manage event details, invite members, or switch rooms!",
                iconRes = R.drawable.ill_vendor_grooming,
                isCircleHighlight = true,
                shape = CircleShape,
                arrowXShift = (-24).dp
            ),
            OnboardingStep(
                stepKey = "home_budget_card",
                title = "Track Your Budget",
                description = "Monitor your spending, manage funds, and keep track of remaining budget at a glance!",
                iconRes = R.drawable.ill_budget_tracker_card,
                highlightPadding = 6.dp,
                shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
                forceCardAbove = false
            ),
            OnboardingStep(
                stepKey = "home_catering_card",
                title = "Catering Menu",
                description = "Explore custom dishes, food choices, and drinks for your event!",
                iconRes = R.drawable.ill_catering_menu_card,
                highlightPadding = 6.dp,
                shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
                forceCardAbove = false
            ),
            OnboardingStep(
                stepKey = "home_venue_card",
                title = "Venue Selection",
                description = "Discover and manage perfect event spaces and locations!",
                iconRes = R.drawable.ill_venue_card,
                highlightPadding = 6.dp,
                shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
                forceCardAbove = false
            ),
            OnboardingStep(
                stepKey = "home_moments_card",
                title = "Capture Moments",
                description = "Upload photos, videos, and create event memory albums!",
                iconRes = R.drawable.ill_moments_card,
                highlightPadding = 6.dp,
                shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
                forceCardAbove = false
            ),
            OnboardingStep(
                stepKey = "home_cards_card",
                title = "Invitation Cards",
                description = "Design and send digital invitation cards to your guests!",
                iconRes = R.drawable.ill_cards_and_guests_card,
                highlightPadding = 6.dp,
                shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
                forceCardAbove = false
            )
        )
    }

    val currentHomeStepKey = if (isHomeOnboardingActive && homeStepIndex in homeOnboardingSteps.indices) {
        homeOnboardingSteps[homeStepIndex].stepKey
    } else null

    LaunchedEffect(currentHomeStepKey) {
        when (currentHomeStepKey) {
            "home_menu_button" -> lazyListState.animateScrollToItem(0)
            "home_budget_card" -> lazyListState.animateScrollToItem(1)
            "home_catering_card" -> lazyListState.animateScrollToItem(2, scrollOffset = -20)
            "home_venue_card" -> lazyListState.animateScrollToItem(2, scrollOffset = -20)
            "home_moments_card" -> lazyListState.animateScrollToItem(3, scrollOffset = -20)
            "home_cards_card" -> lazyListState.animateScrollToItem(3, scrollOffset = -20)
        }
    }

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
                .nestedScroll(nestedScrollConnection)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        val currentOffset = scrollOffsetProvider()
                        translationY = -currentOffset * PARALLAX_RATE
                        alpha = if (fadeDistancePx > 0f) {
                            (1f - (currentOffset / fadeDistancePx)).coerceIn(0f, 1f)
                        } else 1f
                    }
            ) {
                HeaderMediaSlider(
                    mediaList = headerMediaItems,
                    pagerState = headerPagerState,
                    onMediaClick = { },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Scaffold(
                topBar = {
                    HomeTopBar(
                        title = eventName,
                        dateString = eventDateString,
                        alphaProvider = topBarAlphaProvider,
                        contentColorOverride = currentHeaderColor,
                        onMenuClick = onMenuClick,
                        menuButtonModifier = Modifier.onboardingTarget("home_menu_button", currentHomeStepKey) {
                            homeTargetRects["home_menu_button"] = it
                        }
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
                        var totalHeaderDragX by remember { mutableFloatStateOf(0f) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(visibleBackgroundOffset)
                                .noRippleClickable {
                                    if (headerMediaItems.isNotEmpty()) {
                                        val actualIndex = headerPagerState.currentPage % headerMediaItems.size
                                        val media = headerMediaItems[actualIndex]
                                        if (media.actionType.isNotEmpty()) {
                                            onNavigate(media.targetRoute)
                                        }
                                    }
                                }
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
                        ) {
                            // Slider Dot Indicators
                            if (headerMediaItems.size > 1) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp), // Fixed distance above cards
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val currentActualIndex = headerPagerState.currentPage % headerMediaItems.size
                                    repeat(headerMediaItems.size) { index ->
                                        val isSelected = index == currentActualIndex
                                        Box(
                                            modifier = Modifier
                                                .size(if (isSelected) 6.dp else 4.dp)
                                                .background(
                                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(50)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item(key = "budget_card") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = BackgroundPrimary,
                                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                )
                                .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 4.dp)
                                .onboardingTarget("home_budget_card", currentHomeStepKey) {
                                    homeTargetRects["home_budget_card"] = it
                                }
                        ) {
                            BudgetTrackerCard(
                                insight = "See your budget",
                                heading = "Budget Tracker",
                                illustration = painterResource(R.drawable.ill_budget_tracker_card),
                                progress = remainingPercentage,
                                amountText = amountText,
                                labelText = "left",
                                onClick = { onNavigate("budget") }
                            )
                        }
                    }

                    item(key = "row_1_cards") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundPrimary)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            HomeCard(
                                insight = "Delicious and Elegant",
                                heading = "Catering Menu",
                                illustration = painterResource(R.drawable.ill_catering_menu_card),
                                modifier = Modifier
                                    .weight(1f)
                                    .onboardingTarget("home_catering_card", currentHomeStepKey) {
                                        homeTargetRects["home_catering_card"] = it
                                    },
                                cardBgColor = Color(0xFFC4D4C2),
                                waveColor = Color(0x1A14570C).copy(alpha = 0.9f),
                                insightColor = Color(0xFF47671A),
                                onClick = { onNavigate("catering") }
                            )
                            HomeCard(
                                insight = "Perfect Event Spaces",
                                heading = "Venue",
                                illustration = painterResource(R.drawable.ill_venue_card),
                                modifier = Modifier
                                    .weight(1f)
                                    .onboardingTarget("home_venue_card", currentHomeStepKey) {
                                        homeTargetRects["home_venue_card"] = it
                                    },
                                cardBgColor = Color(0xFFD3CDE8),
                                waveColor = Color(0x1A2C186C).copy(alpha = 0.9f),
                                insightColor = Color(0xFF6448D6),
                                onClick = { onNavigate("venues") }
                            )
                        }
                    }

                    item(key = "row_2_cards") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundPrimary)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            HomeCard(
                                insight = "Capture and Smile",
                                heading = "Moments",
                                illustration = painterResource(R.drawable.ill_moments_card),
                                modifier = Modifier
                                    .weight(1f)
                                    .onboardingTarget("home_moments_card", currentHomeStepKey) {
                                        homeTargetRects["home_moments_card"] = it
                                    },
                                cardBgColor = Color(0xFFC3D4E8),
                                waveColor = Color(0x1A014594).copy(alpha = 0.9f),
                                insightColor = Color(0xFF3D58B4),
                                onClick = { onNavigate("moments") }
                            )

                            HomeCard(
                                insight = "Invite and Celebrate",
                                heading = "Cards",
                                illustration = painterResource(R.drawable.ill_cards_and_guests_card),
                                modifier = Modifier
                                    .weight(1f)
                                    .onboardingTarget("home_cards_card", currentHomeStepKey) {
                                        homeTargetRects["home_cards_card"] = it
                                    },
                                cardBgColor = Color(0xFFE8D0CE),
                                waveColor = Color(0x1A5D0501).copy(alpha = 0.9f),
                                insightColor = Color(0xFF5D1D1B),
                                onClick = { onNavigate("cards") }
                            )
                        }
                    }

                    item(key = "explore_divider") {
                        OrDivider(
                            dividerGap = 12.dp,
                            text = "EXPLORE",
                            modifier = Modifier
                                .background(BackgroundPrimary)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    item(key = "trending_venues") {
                        val locationSubtitle = if (selectedLocation.isNotBlank() && selectedLocation != "City, State") "In $selectedLocation" else "Top Spaces"
                        VenueCarousel(
                            title = "Trending Venues",
                            subtitle = locationSubtitle,
                            venues = trendingVenues,
                            isLoading = isVenuesLoading,
                            onVenueClick = onVenueClick,
                            cardSize = CompactCardSize.MEDIUM,
                            onFavoriteToggle = onVenueFavoriteToggle,
                            onSeeAllClick = { onNavigate("venues") },
                            onOfferClick = { venue -> onOfferClick(venue) },
                            modifier = Modifier.background(BackgroundPrimary)
                        )
                    }

                    item(key = "trending_vendors") {
                        val locationSubtitle = if (selectedLocation.isNotBlank() && selectedLocation != "City, State") "In $selectedLocation" else "Top Services"
                        VendorCarousel(
                            title = "Trending Vendors",
                            subtitle = locationSubtitle,
                            vendors = trendingVendors,
                            isLoading = isVendorsLoading,
                            onVendorClick = onVendorClick,
                            cardSize = CompactCardSize.MEDIUM,
                            onFavoriteToggle = onVendorFavoriteToggle,
                            onSeeAllClick = { onNavigate("vendors") },
                            onOfferClick = { vendor -> onVendorOfferClick(vendor) },
                            modifier = Modifier.background(BackgroundPrimary)
                        )
                    }

                    item(key = "dashed_divider") {
                        DashedDivider(modifier = Modifier.background(BackgroundPrimary))
                    }

                    item(key = "vendor_categories") {
                        ExploreCategoriesHorizontal(
                            categories = vendorCategories,
                            onCategoryClick = onCategoryClick
                        )
                    }

                    item(key = "footer") {
                        FooterJansify(modifier = Modifier.background(BackgroundPrimary))
                    }
                }
            }

        }

        if (isHomeOnboardingActive && homeOnboardingSteps.isNotEmpty()) {
            FeatureOnboardingOverlay(
                steps = homeOnboardingSteps,
                currentStepIndex = homeStepIndex,
                targetRectMap = homeTargetRects,
                onNextStep = {
                    if (homeStepIndex < homeOnboardingSteps.lastIndex) {
                        homeStepIndex++
                    } else {
                        isHomeOnboardingActive = false
                        prefManager.setCompletedScreenOnboarding("home", true)
                    }
                },
                onPreviousStep = {
                    if (homeStepIndex > 0) homeStepIndex--
                }
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
                    iconColor = Color.Unspecified,
                    buttonText = if (isMultiDay) "Change" else null,
                    onButtonClick = if (isMultiDay) {
                        {
                            onToastChange(null)
                            onSaveListChange(null, null)
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
}