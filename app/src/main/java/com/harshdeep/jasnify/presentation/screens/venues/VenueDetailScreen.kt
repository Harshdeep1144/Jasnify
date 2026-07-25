package com.harshdeep.jasnify.presentation.screens.venues

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.VenueHighlightItem
import com.harshdeep.jasnify.domain.model.VenueMediaItem
import com.harshdeep.jasnify.domain.model.VenuePricingItem
import com.harshdeep.jasnify.domain.model.VenueReview
import com.harshdeep.jasnify.domain.model.VenueReviewsData
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VenueCardCompact
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.VideoPlayer
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.sections.RatingSurface
import com.harshdeep.jasnify.presentation.components.sections.VenueAllReviewsScreen
import com.harshdeep.jasnify.presentation.components.sections.VenueGalleryDetailScreen
import com.harshdeep.jasnify.presentation.components.sections.VenueGallerySection
import com.harshdeep.jasnify.presentation.components.sections.VenueReviewDetailPostScreen
import com.harshdeep.jasnify.presentation.components.sections.VenueReviewsSection
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.math.roundToInt

// ============================================================================================================================================
// ENUMS
// ============================================================================================================================================

enum class VenueActiveScreen {
    DETAIL, REVIEWS, GALLERY, POST
}

// ============================================================================================================================================
// MAIN SCREEN CONTAINERS
// ============================================================================================================================================

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun VenueDetailScreen(
    venueDetail: Venue,
    onBackClick: () -> Unit = {},
    onChatClick: (Venue) -> Unit = {},
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    // Dynamic stack to keep track of screens locally
    var screenStack by remember { mutableStateOf(listOf(VenueActiveScreen.DETAIL)) }
    val currentScreen = screenStack.last()

    var selectedReviewForPost by remember { mutableStateOf<VenueReview?>(null) }

    // Intercepts the back gesture ONLY when there is a screen to pop locally
    BackHandler(enabled = screenStack.size > 1) {
        screenStack = screenStack.dropLast(1)
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "VenueNavigationTransition"
    ) { screen ->
        when (screen) {
            VenueActiveScreen.DETAIL -> {
                VenueDetailContent(
                    venueDetail = venueDetail,
                    onBackClick = onBackClick,
                    onChatClick = onChatClick,
                    onSeeAllReviewsClick = { screenStack = screenStack + VenueActiveScreen.REVIEWS },
                    onSeeAllGalleryClick = { screenStack = screenStack + VenueActiveScreen.GALLERY },
                    onOpenReviewPost = { review ->
                        selectedReviewForPost = review
                        screenStack = screenStack + VenueActiveScreen.POST
                    },
                    modifier = modifier,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            VenueActiveScreen.REVIEWS -> {
                VenueAllReviewsScreen(
                    title = venueDetail.name,
                    reviewsData = venueDetail.reviewsData ?: VenueReviewsData(),
                    ratingValue = venueDetail.rating.toString(),
                    onBack = { screenStack = screenStack.dropLast(1) },
                    onOpenReviewPost = { review ->
                        selectedReviewForPost = review
                        screenStack = screenStack + VenueActiveScreen.POST
                    },
                    onLeaveReview = {
                        // Navigation to Write Review Screen
                    }
                )
            }
            VenueActiveScreen.GALLERY -> {
                VenueGalleryDetailScreen(
                    title = venueDetail.name,
                    galleryCategories = venueDetail.galleryCategories,
                    onBack = { screenStack = screenStack.dropLast(1) },
                    onOpenAlbum = { }
                )
            }
            VenueActiveScreen.POST -> {
                VenueReviewDetailPostScreen(
                    review = selectedReviewForPost ?: venueDetail.reviewsData?.reviews?.firstOrNull() ?: VenueReview(
                        userName = "Anand K.",
                        rating = 4.4,
                        relativeTime = "1 week ago",
                        reviewText = "Discover the charm of Hotel Imperial Inn, located in Sampatchak, Patna."
                    ),
                    onBack = {
                        screenStack = screenStack.dropLast(1)
                    }
                )
            }
        }
    }
}


@SuppressLint("UseKtx")
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun VenueDetailContent(
    venueDetail: Venue,
    onBackClick: () -> Unit,
    onChatClick: (Venue) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    onSeeAllGalleryClick: () -> Unit,
    onOpenReviewPost: (VenueReview) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val venue = venueDetail

    val addressSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddressSheet by remember { mutableStateOf(false) }

    val aboutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAboutSheet by remember { mutableStateOf(false) }

    // Intercept back button if any custom bottom sheet is open inside this content block
    BackHandler(enabled = showAddressSheet || showAboutSheet) {
        if (showAddressSheet) showAddressSheet = false
        if (showAboutSheet) showAboutSheet = false
    }

    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()
    val topBarHeightPx = with(density) { 56.dp.toPx() }
    val stickyMarginPx = with(density) { 12.dp.toPx() }
    val minOffsetPx = statusBarHeightPx + topBarHeightPx + stickyMarginPx
    val maxOffsetPx = with(density) { 320.dp.toPx() }
    val stickyHeaderHeightPx = with(density) { 56.dp.roundToPx() }

    var sheetOffsetPx by remember { mutableStateOf(maxOffsetPx) }
    var isFavoriteState by remember { mutableStateOf(venue.favorite) }
    var isMuted by remember { mutableStateOf(true) }

    val activeTabs = remember(venueDetail) {
        buildList {
            if (venueDetail.pricingItems.isNotEmpty()) add("Pricings")
            if (venueDetail.highlightItems.isNotEmpty()) add("Highlights")
            if (venueDetail.aboutText != null) add("About")
            add("Ask AI")
        }
    }

    val listKeys = remember(venueDetail) {
        buildList {
            add("info")
            add("suggestions")
            add("tabs")
            if (venueDetail.pricingItems.isNotEmpty()) {
                add("pricings")
                add("div_pricings")
            }
            if (venueDetail.highlightItems.isNotEmpty()) {
                add("highlights")
                add("div_highlights")
            }
            if (venueDetail.aboutText != null) {
                add("about")
                add("div_about")
            }
            add("ask_ai")
            add("div_ask_ai")
            if (venueDetail.galleryCategories.isNotEmpty()) {
                add("gallery")
                add("div_gallery")
            }
            if (venueDetail.reviewsData != null && venueDetail.reviewsData.reviews.isNotEmpty()) {
                add("reviews")
                add("div_reviews")
            }
            add("footer")
        }
    }

    val selectedTabIndex by remember(activeTabs, listKeys) {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) {
                0
            } else {
                val thresholdPx = stickyHeaderHeightPx.toFloat()
                val candidateItem = visibleItems.firstOrNull {
                    it.offset + it.size > thresholdPx + 20f
                } ?: visibleItems.first()

                val itemIndex = candidateItem.index
                val itemKey = listKeys.getOrNull(itemIndex) ?: ""

                when (itemKey) {
                    "pricings", "div_pricings" -> activeTabs.indexOf("Pricings").coerceAtLeast(0)
                    "highlights", "div_highlights" -> activeTabs.indexOf("Highlights").coerceAtLeast(0)
                    "about", "div_about" -> activeTabs.indexOf("About").coerceAtLeast(0)
                    "ask_ai", "div_ask_ai" -> activeTabs.indexOf("Ask AI").coerceAtLeast(0)
                    else -> {
                        val pricingsIdx = listKeys.indexOf("pricings").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val highlightsIdx = listKeys.indexOf("highlights").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val aboutIdx = listKeys.indexOf("about").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val askAiIdx = listKeys.indexOf("ask_ai").takeIf { it != -1 } ?: Int.MAX_VALUE

                        val firstContentIdx = minOf(pricingsIdx, highlightsIdx, aboutIdx, askAiIdx)

                        if (itemIndex < firstContentIdx) {
                            0
                        } else {
                            val aiIndex = activeTabs.indexOf("Ask AI")
                            if (aiIndex != -1) aiIndex else 0
                        }
                    }
                }
            }
        }
    }

    val nestedScrollConnection = remember(minOffsetPx, maxOffsetPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                return if (delta < 0 && sheetOffsetPx > minOffsetPx) {
                    val newOffset = (sheetOffsetPx + delta).coerceAtLeast(minOffsetPx)
                    val consumed = newOffset - sheetOffsetPx
                    sheetOffsetPx = newOffset
                    Offset(0f, consumed)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return if (delta > 0 && !listState.canScrollBackward) {
                    val newOffset = (sheetOffsetPx + delta).coerceIn(minOffsetPx, maxOffsetPx)
                    val consumedOffset = newOffset - sheetOffsetPx
                    sheetOffsetPx = newOffset
                    Offset(0f, consumedOffset)
                } else {
                    Offset.Zero
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        val parallaxTranslationY = remember(sheetOffsetPx) {
            val displacement = maxOffsetPx - sheetOffsetPx
            -displacement * 0.45f
        }

        VenueMediaSlider(
            mediaItems = venueDetail.mediaItems,
            isMuted = isMuted,
            onMuteToggle = { isMuted = !isMuted },
            venue = venue,
            onSeeAllGalleryClick = onSeeAllGalleryClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .graphicsLayer {
                    translationY = parallaxTranslationY
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, sheetOffsetPx.roundToInt()) }
                .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge))
                .shadow(24.dp, SquircleShape(CornerExtraLarge, CornerExtraLarge))
                .background(
                    color = BackgroundPrimary,
                    shape = SquircleShape(CornerExtraLarge, CornerExtraLarge)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(56.dp)
                        .height(4.dp)
                        .background(ContentTertiary, shape = RoundedCornerShape(100))
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "info") {
                    VenueInfoSection(venue = venue, onAddressClick = { showAddressSheet = true })
                }

                item(key = "suggestions") {
                    SuggestionChipsSection()
                }

                stickyHeader(key = "tabs") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfacePrimary,
                        shadowElevation = if (listState.firstVisibleItemIndex >= 2) 4.dp else 0.dp
                    ) {
                        VenueTabs(
                            tabs = activeTabs,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                coroutineScope.launch {
                                    val tabName = activeTabs[index]
                                    val targetKey = when (tabName) {
                                        "Pricings" -> "pricings"
                                        "Highlights" -> "highlights"
                                        "About" -> "about"
                                        "Ask AI" -> "ask_ai"
                                        else -> "pricings"
                                    }
                                    val itemIndex = listKeys.indexOf(targetKey)
                                    if (itemIndex != -1) {
                                        listState.animateScrollToItem(
                                            index = itemIndex,
                                            scrollOffset = -stickyHeaderHeightPx
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                if (!venueDetail.pricingItems.isNullOrEmpty()) {
                    item(key = "pricings") {
                        VenuePricingsSection(venue = venue, pricingItems = venueDetail.pricingItems)
                    }
                    item(key = "div_pricings") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                if (!venueDetail.highlightItems.isNullOrEmpty()) {
                    item(key = "highlights") {
                        VenueHighlightsSection(highlightItems = venueDetail.highlightItems)
                    }
                    item(key = "div_highlights") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                if (venueDetail.aboutText != null) {
                    item(key = "about") {
                        VenueAboutSection(
                            venue = venue,
                            aboutText = venueDetail.aboutText!!,
                            onReadMoreClick = { showAboutSheet = true }
                        )
                    }
                    item(key = "div_about") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                item(key = "ask_ai") {
                    VenueAskAISection()
                }
                item(key = "div_ask_ai") {
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                if (!venueDetail.galleryCategories.isNullOrEmpty()) {
                    item(key = "gallery") {
                        VenueGallerySection(
                            galleryCategories = venueDetail.galleryCategories,
                            onSeeAllClick = onSeeAllGalleryClick
                        )
                    }
                    item(key = "div_gallery") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                if (venueDetail.reviewsData != null && venueDetail.reviewsData!!.reviews.isNotEmpty()) {
                    item(key = "reviews") {
                        VenueReviewsSection(
                            venue = venue,
                            reviewsData = venueDetail.reviewsData!!,
                            onSeeAllClick = onSeeAllReviewsClick,
                            onReviewCardClick = onOpenReviewPost,
                            onWriteReviewClick = { }
                        )
                    }
                    item(key = "div_reviews") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                item(key = "footer") {
                    FooterJansify()
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        val secondaryIcon = if(isFavoriteState) painterResource(R.drawable.ic_heart_filled) else painterResource(R.drawable.ic_heart)

        val scrollRange = maxOffsetPx - minOffsetPx
        val currentScrollOffset = maxOffsetPx - sheetOffsetPx
        val scrollFraction = if (scrollRange > 0f) {
            (currentScrollOffset / scrollRange).coerceIn(0f, 1f)
        } else {
            0f
        }
        val topBarAlpha = 0.5f + (scrollFraction * 0.5f)

        val dynamicButtonStyle = if (topBarAlpha > 0.9f) {
            ButtonBackground.OPAQUE
        } else {
            ButtonBackground.TRANSLUCENT
        }

        Column(
            modifier = Modifier
                .statusBarsPadding()
        ) {
            CustomTopBar(
                onBackClick = onBackClick,
                secondaryIcon = TopIcon.CustomPainter(painter = secondaryIcon),
                menuIcon = TopIcon.CustomPainter(painter = painterResource(R.drawable.ic_share)),
                backIcon = TopIcon.Predefined.DOWN,
                onSecondaryClick = { isFavoriteState = !isFavoriteState },
                onMenuClick = { },
                buttonStyle = dynamicButtonStyle,
                translucentAlpha = topBarAlpha,
                textColor = ContentPrimary,
            )
        }

        FloatingBottomActionBar(
            onMessageClick = { onChatClick(venue) },
            onBookCallClick = { },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .graphicsLayer { translationY = 0f } // Ensure it's not shifted
                .zIndex(10f) // Keep it on top
        )

// ============================================================= Bottom Sheets ===============================================

        if (showAddressSheet) {
            CustomBottomSheet(
                heading = "Venue Address",
                sheetState = addressSheetState,
                onDismiss = { showAddressSheet = false },
                sheetHeight = 360.dp
            ) {
                val context = LocalContext.current

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Surface(
                            color = SurfaceSecondary,
                            shape = SquircleShape(CornerLarge),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.img_hero_venueaddress),
                                        contentDescription = "Map Pin Logo",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(80.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = venue.name,
                                    style = JasnifyTheme.typography.displayMedium,
                                    color = ContentPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = venue.location,
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentSecondary
                                )
                            }
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

                    CustomTextButton(
                        onClick = {
                            val mapQuery = "${venue.name}, ${venue.location}"
                            val encodedQuery = Uri.encode(mapQuery)
                            val mapUri = "geo:0,0?q=$encodedQuery".toUri()
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                val webUri = "https://www.google.com/maps/search/?api=1&query=$encodedQuery".toUri()
                                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                                try {
                                    context.startActivity(webIntent)
                                } catch (ignored: Exception) {}
                            }
                        },
                        text = "Get Directions",
                        trailingIcon = rememberVectorPainter(Icons.Rounded.ArrowOutward),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shapeStyle = ButtonShapeStyle.Square
                    )
                }
            }
        }

        if (showAboutSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAboutSheet = false },
                sheetState = aboutSheetState,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(56.dp)
                            .height(4.dp)
                            .background(ContentTertiary, shape = SquircleShape(100))
                    )
                },
                scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f),
                containerColor = SurfacePrimary,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(12.dp),
                ) {
                    Text(
                        text = "About ${venue.name}",
                        style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                        color = ContentPrimary
                    )
                    Spacer(Modifier.height(16.dp))

                    // Simulating the data coming from database
                    val aboutVenueFromDb = venue.aboutText ?: "No information available for this venue."
                    val formattedAboutText = aboutVenueFromDb.replace(". ", ".\n\n")

                    Text(
                        text = formattedAboutText,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentSecondary
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))

                Column{
                    CustomTextButton(
                        onClick = { showAboutSheet = false },
                        text = "Okay",
                        modifier = Modifier.fillMaxWidth()
                            .padding(12.dp),
                        shapeStyle = ButtonShapeStyle.Square
                    )
                }
            }
        }
    }
}




// ============================================================================================================================================
// STRUCTURAL CONTENT SECTIONS
// ============================================================================================================================================



@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VenueMediaSlider(
    mediaItems: List<VenueMediaItem>,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    venue: Venue,
    onSeeAllGalleryClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { mediaItems.size })

    if (mediaItems.size > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % mediaItems.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val mediaItem = mediaItems[page]

            Box(modifier = Modifier.fillMaxSize()) {
                val sharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null && page == 0) {
                    with(sharedTransitionScope) {
                        Modifier
                            .fillMaxSize()
                            .sharedElement(
                                rememberSharedContentState(key = "image_${venue.name}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                renderInOverlayDuringTransition = false
                            )
                    }
                } else {
                    Modifier.fillMaxSize()
                }

                Box(modifier = sharedBoundsModifier) {
                    if (mediaItem.video) {
                        VideoPlayer(
                            videoUrl = mediaItem.url,
                            isMuted = isMuted,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = mediaItem.url,
                            contentDescription = "Venue Media Slide ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ic_gallery)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val activeItem = mediaItems.getOrNull(pagerState.currentPage)
            if (activeItem?.video == true) {
                val audioIcon = if(isMuted) painterResource(R.drawable.ic_mute) else painterResource(R.drawable.ic_volume)

                TopBarIconButton(
                    icon = TopIcon.CustomPainter(painter = audioIcon),
                    onClick = onMuteToggle,
                    backgroundStyle = ButtonBackground.TRANSLUCENT,
                    translucentAlpha = 0.5f
                )
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }

            CustomTextButton(
                text = "Gallery (${mediaItems.size})",
                onClick = onSeeAllGalleryClick,
                containerColor = ContentInvPrimary.copy(alpha = 0.5f),
                contentColor = ContentPrimary,
                size = ButtonSize.Small,
                leadingIcon = painterResource(R.drawable.ic_gallery)
            )
        }
    }
}





@Composable
fun VenueInfoSection(
    venue: Venue,
    onAddressClick: () -> Unit
) {
    var hasOverflow by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = venue.name,
                style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                color = ContentPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddressClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = venue.location,
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        hasOverflow = textLayoutResult.hasVisualOverflow
                    },
                    modifier = Modifier.weight(1f, fill = false)
                )

                Icon(
                    imageVector =  Icons.Default.KeyboardArrowDown,
                    contentDescription =  "Expand",
                    tint = ContentSecondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(CornerMedium))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(0.16f),
                    shape = RoundedCornerShape(CornerMedium)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RatingSurface(
                rating = venue.rating.toString(),
                shape = RoundedCornerShape(CornerMedium, CornerMedium, 0.dp, 0.dp)
            )
            Text(
                text = venue.totalReviews,
                style = JasnifyTheme.typography.labelSmall,
                color = ContentSecondary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}


@Composable
fun VenuePricingsSection(venue: Venue, pricingItems: List<VenuePricingItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        pricingItems.forEachIndexed { index, item ->
            val shape = when (index) {
                0 -> SquircleShape(CornerLarge, CornerLarge, CornerExtraSmall, CornerExtraSmall)
                pricingItems.lastIndex -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLarge, CornerLarge)
                else -> SquircleShape(CornerExtraSmall)
            }
            PricingCard(
                title = item.title,
                price = item.price,
                unit = item.unit,
                iconRes = item.iconRes,
                labelText = item.labelText,
                shape = shape
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "See full pricings",
                color = ContentBrandDark,
                style = JasnifyTheme.typography.labelLarge,
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = ContentBrandDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun VenueHighlightsSection(highlightItems: List<VenueHighlightItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Text(
            text = "Highlights",
            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
            color = ContentPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        highlightItems.forEach { highlight ->
            HighlightItemRow(data = highlight)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun VenueAboutSection(
    venue: Venue,
    aboutText: String,
    onReadMoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = "About this venue",
            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
            color = ContentPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = aboutText,
            style = JasnifyTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 4,
            color = ContentSecondary,
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.clickable { onReadMoreClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Read more",
                color = ContentBrandDark,
                style = JasnifyTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = ContentBrandDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun VenueAskAISection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Ask anything about this venue",
                style = JasnifyTheme.typography.displaySmall,
                color = ContentPrimary
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                color = Color(0xFFB66FC4),
                shape = RoundedCornerShape(100)
            ) {
                Text(
                    "NEW",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentInvPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        CustomSearchBar(
            value = "",
            onValueChange = {},
            placeholder = "What would you like to know?",
            isAiSearch = true,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        )

        val suggestionChips = listOf(
            "How is the vibe here?",
            "What's good here?",
            "Do they serve alcohol?",
            "How many guests they can serve?"
        )

        if (suggestionChips.size > 6) {
            val midIndex = (suggestionChips.size + 1) / 2
            val firstRowChips = suggestionChips.take(midIndex)
            val secondRowChips = suggestionChips.drop(midIndex)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // First Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    items(firstRowChips) { chip ->
                        FilterChip(
                            label = chip,
                            trailingIcon = Icons.Rounded.ArrowOutward,
                            hasStroke = true,
                            onClick = { }
                        )
                    }
                }

                // Second Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    items(secondRowChips) { chip ->
                        FilterChip(
                            label = chip,
                            trailingIcon = Icons.Rounded.ArrowOutward,
                            hasStroke = true,
                            onClick = { }
                        )
                    }
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(suggestionChips) { chip ->
                    FilterChip(
                        label = chip,
                        trailingIcon = Icons.Rounded.ArrowOutward,
                        hasStroke = true,
                        onClick = { }
                    )
                }
            }
        }
    }
}


@Composable
fun VenueExploreMoreSection(
    similarVenues: List<Venue>,
    modifier: Modifier = Modifier
) {
    val filters = remember {
        listOf("All", "Top Rated", "Great Ambiance", "Budget-Friendly")
    }
    var selectedFilterIndex by remember { mutableStateOf(0) }

    val filteredVenues = remember(selectedFilterIndex, similarVenues) {
        when (selectedFilterIndex) {
            1 -> similarVenues.filter { it.rating >= 4.5 }
            2 -> similarVenues.filter { it.rating >= 4.2 }
            else -> similarVenues
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore more venues",
                style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                color = ContentPrimary
            )
            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "See all",
                    color = ContentBrandDark,
                    style = JasnifyTheme.typography.labelLarge,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ContentBrandDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (filters.size > 6) {
            val midIndex = (filters.size + 1) / 2
            val firstRowFilters = filters.take(midIndex)
            val secondRowFilters = filters.drop(midIndex)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // First Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(firstRowFilters) { index, filterText ->
                        FilterChip(
                            label = filterText,
                            isSelected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index },
                            hasStroke = true
                        )
                    }
                }
                // Second Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(secondRowFilters) { index, filterText ->
                        val originalIndex = index + midIndex
                        FilterChip(
                            label = filterText,
                            isSelected = selectedFilterIndex == originalIndex,
                            onClick = { selectedFilterIndex = originalIndex },
                            hasStroke = true
                        )
                    }
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filters) { index, filterText ->
                    FilterChip(
                        label = filterText,
                        isSelected = selectedFilterIndex == index,
                        onClick = { selectedFilterIndex = index },
                        hasStroke = true
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Showing similar venues",
            style = JasnifyTheme.typography.labelLarge,
            color = ContentSecondary,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredVenues) { venueItem ->
                VenueCardCompact(
                    venue = venueItem,
                    compactCardSize = CompactCardSize.SMALL
                )
            }
        }
    }
}

// ============================================================================================================================================
// HELPER COMPONENTS
// ============================================================================================================================================


@Composable
fun SuggestionChipsSection(
    onClickSuggestion: (String) -> Unit = {}
) {
    val suggestions = remember {
        listOf(
            "What's good here?",
            "View popular dishes",
            "Any ongoing offers?",
            "Check real-time crowd status"
        )
    }
    val aiIcon = ImageVector.vectorResource(id = R.drawable.ic_ai)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(suggestions) { text ->
            FilterChip(
                label = text,
                isSelected = false,
                shapeStyle = ChipShapeStyle.Round,
                size = ChipSize.Small,
                leadingIcon = aiIcon,
                onClick = { onClickSuggestion(text) },
                hasStroke = true,
                isAiMode = true
            )
        }
    }
}

@Composable
fun VenueTabs(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = ContentBrand,
        edgePadding = 12.dp,
        divider = {},
        indicator = { tabPositions ->
            if (selectedTabIndex in tabPositions.indices) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = ContentBrand,
                    height = 4.dp
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = JasnifyTheme.typography.headingMedium,
                            color = if (isSelected) ContentPrimary else ContentSecondary,
                        )
                        if (title == "Ask AI") {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFB66FC4),
                                shape = RoundedCornerShape(100)
                            ) {
                                Text(
                                    "NEW",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = JasnifyTheme.typography.labelMedium,
                                    color = ContentInvPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun getIconResId(iconName: String?): Int {
    val context = LocalContext.current
    return remember(iconName) {
        if (iconName.isNullOrBlank()) R.drawable.ic_gallery
        else {
            val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
            if (resId != 0) resId else R.drawable.ic_gallery
        }
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,
    unit: String,
    iconRes: String?,
    labelText: String,
    shape: SquircleShape = SquircleShape(CornerExtraSmall)
) {
    val resId = getIconResId(iconRes)
    Surface(
        color = SurfaceSecondary,
        shape = shape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.TopCenter) {
                    Icon(
                        painter = painterResource(resId),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentPrimary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = labelText,
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = price,
                    style = JasnifyTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium),
                    color = ContentPrimary
                )
                Text(
                    text = unit,
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary
                )
            }
        }
    }
}

@Composable
fun HighlightItemRow(data: VenueHighlightItem) {
    val resId = getIconResId(data.iconRes)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = SurfaceBrandSecondary,
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(resId),
                    contentDescription = null,
                    tint = ContentBrandDark,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = data.label,
                style = JasnifyTheme.typography.labelSmall,
                color = ContentSecondary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = data.value,
                style = JasnifyTheme.typography.labelXLarge,
                color = ContentPrimary
            )
        }
    }
}

@Composable
fun FloatingBottomActionBar(
    onMessageClick: () -> Unit,
    onBookCallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(64.dp)
            .dropShadow(
                shape = CircleShape,
                shadow = Shadow(
                    radius = 16.dp,
                    spread = 0.dp,
                    color = ContentPrimary.copy(alpha = 0.2f),
                    offset = DpOffset(0.dp, 6.dp)
                )
            ),
        color = SurfacePrimary,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomIconButton(
                onClick = onMessageClick,
                icon = painterResource(R.drawable.ic_message),
                type = ButtonType.Secondary,
                modifier = Modifier.weight(0.4f)
            )
            Spacer(modifier = Modifier.width(4.dp))

            CustomTextButton(
                onClick = onBookCallClick,
                text = "Book a Call",
                type = ButtonType.Primary,
                modifier = Modifier.weight(1.6f)
            )
        }
    }
}
