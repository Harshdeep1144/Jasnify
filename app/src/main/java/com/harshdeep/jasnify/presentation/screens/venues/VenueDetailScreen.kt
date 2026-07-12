package com.harshdeep.jasnify.presentation.screens.venues

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
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
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.math.roundToInt


data class VenueMediaItem(
    val url: String,
    val isVideo: Boolean = false,
    val videoDuration: String? = null
)

data class VenuePricingItem(
    val title: String,
    val price: String,
    val unit: String,
    val iconRes: Int,
    val labelText: String = "Price Point Offer"
)

data class VenueHighlightItem(
    val label: String,
    val value: String,
    val iconRes: Int
)

data class GalleryCategoryData(
    val categoryName: String,
    val imageUrls: List<String>
)

data class RatingBreakdownItemData(
    val score: String,
    val label: String
)

data class VenueReviewItem(
    val userName: String,
    val userAvatarUrl: String? = null,
    val rating: Double,
    val relativeTime: String,
    val reviewText: String
)

data class VenueReviewsData(
    val ratingBreakdown: List<RatingBreakdownItemData>,
    val reviews: List<VenueReviewItem>
)

data class VenueDetailData(
    val vendorCard: VendorCardData,
    val mediaItems: List<VenueMediaItem> = emptyList(),
    val pricingItems: List<VenuePricingItem>? = null,
    val highlightItems: List<VenueHighlightItem>? = null,
    val aboutText: String? = null,
    val galleryCategories: List<GalleryCategoryData>? = null,
    val reviewsData: VenueReviewsData? = null,
    val similarVenues: List<VendorCardData>? = null
)


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun VenueDetailScreen(
    venueDetail: VenueDetailData,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val vendor = venueDetail.vendorCard

    // Dynamic density-derived values
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()
    val topBarHeightPx = with(density) { 56.dp.toPx() }
    val stickyMarginPx = with(density) { 12.dp.toPx() }
    val minOffsetPx = statusBarHeightPx + topBarHeightPx + stickyMarginPx
    val maxOffsetPx = with(density) { 320.dp.toPx() }
    val stickyHeaderHeightPx = with(density) { 56.dp.roundToPx() }

    // Floating Sheet Swipe limits
    var sheetOffsetPx by remember { mutableStateOf(maxOffsetPx) }
    var isFavoriteState by remember { mutableStateOf(vendor.isFavorite) }
    var isMuted by remember { mutableStateOf(true) }

    // Compute dynamic tabs based on properties present inside venueDetail
    val activeTabs = remember(venueDetail) {
        buildList {
            if (!venueDetail.pricingItems.isNullOrEmpty()) add("Pricings")
            if (!venueDetail.highlightItems.isNullOrEmpty()) add("Highlights")
            if (venueDetail.aboutText != null) add("About")
            add("Ask AI")
        }
    }

    // Capture precise keys assigned to items inside the LazyColumn list
    val listKeys = remember(venueDetail) {
        buildList {
            add("info")
            add("suggestions")
            add("tabs")
            if (!venueDetail.pricingItems.isNullOrEmpty()) {
                add("pricings")
                add("div_pricings")
            }
            if (!venueDetail.highlightItems.isNullOrEmpty()) {
                add("highlights")
                add("div_highlights")
            }
            if (venueDetail.aboutText != null) {
                add("about")
                add("div_about")
            }
            add("ask_ai")
            add("div_ask_ai")
            if (!venueDetail.galleryCategories.isNullOrEmpty()) {
                add("gallery")
                add("div_gallery")
            }
            if (venueDetail.reviewsData != null && venueDetail.reviewsData.reviews.isNotEmpty()) {
                add("reviews")
                add("div_reviews")
            }
            add("explore_more")
            add("div_explore_more")
            if (!venueDetail.similarVenues.isNullOrEmpty()) {
                add("similar_venues")
            }
            add("footer")
        }
    }

    // Scroll progress-derived index synchronization
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
                        // Dynamically evaluate positions relative to tab content anchors
                        val pricingsIdx = listKeys.indexOf("pricings").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val highlightsIdx = listKeys.indexOf("highlights").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val aboutIdx = listKeys.indexOf("about").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val askAiIdx = listKeys.indexOf("ask_ai").takeIf { it != -1 } ?: Int.MAX_VALUE

                        val firstContentIdx = minOf(pricingsIdx, highlightsIdx, aboutIdx, askAiIdx)

                        if (itemIndex < firstContentIdx) {
                            // If user is at "info", "suggestions", or "tabs", highlight the first available section tab
                            0
                        } else {
                            // If user is below all defined tab contents (e.g. gallery/reviews), map to "Ask AI"
                            val aiIndex = activeTabs.indexOf("Ask AI")
                            if (aiIndex != -1) aiIndex else 0
                        }
                    }
                }
            }
        }
    }

    // NestedScroll handling for scrolling vs panel-drags
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
            vendor = vendor,
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
                .then(
                    if (animatedVisibilityScope != null) {
                        with(animatedVisibilityScope) {
                            Modifier.animateEnterExit(
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(500)
                                ) + fadeIn(animationSpec = tween(500)),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(500)
                                ) + fadeOut(animationSpec = tween(500))
                            )
                        }
                    } else Modifier
                )
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
                // Info Section
                item(key = "info") {
                    VenueInfoSection(vendor = vendor)
                }

                // AI Suggestions
                item(key = "suggestions") {
                    SuggestionChipsSection()
                }

                // Sticky Tab Bar Component
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

                // Pricings Layout
                if (!venueDetail.pricingItems.isNullOrEmpty()) {
                    item(key = "pricings") {
                        PricingsSection(vendor = vendor, pricingItems = venueDetail.pricingItems)
                    }
                    item(key = "div_pricings") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                // Highlights Layout
                if (!venueDetail.highlightItems.isNullOrEmpty()) {
                    item(key = "highlights") {
                        HighlightsSection(highlightItems = venueDetail.highlightItems)
                    }
                    item(key = "div_highlights") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                // About layout
                if (venueDetail.aboutText != null) {
                    item(key = "about") {
                        AboutSection(vendor = vendor, aboutText = venueDetail.aboutText)
                    }
                    item(key = "div_about") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                // Ask AI Area
                item(key = "ask_ai") {
                    AskAISection()
                }
                item(key = "div_ask_ai") {
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                // Gallery Grid
                if (!venueDetail.galleryCategories.isNullOrEmpty()) {
                    item(key = "gallery") {
                        GallerySection(galleryCategories = venueDetail.galleryCategories)
                    }
                    item(key = "div_gallery") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                // Reviews Section
                if (venueDetail.reviewsData != null && venueDetail.reviewsData.reviews.isNotEmpty()) {
                    item(key = "reviews") {
                        ReviewsSection(vendor = vendor, reviewsData = venueDetail.reviewsData)
                    }
                    item(key = "div_reviews") {
                        DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                // Explore Section
                item(key = "explore_more") {
                    ExploreMoreSection()
                }
                item(key = "div_explore_more") {
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                // Similar Venues Section
                if (!venueDetail.similarVenues.isNullOrEmpty()) {
                    item(key = "similar_venues") {
                        SimilarVenuesSection(similarVenues = venueDetail.similarVenues)
                    }
                }

                // Footer and spacing anchor
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
                .then(
                    if (animatedVisibilityScope != null) {
                        with(animatedVisibilityScope) {
                            Modifier.animateEnterExit(
                                enter = slideInVertically(
                                    initialOffsetY = { -it },
                                    animationSpec = tween(500)
                                ) + fadeIn(animationSpec = tween(500)),
                                exit = slideOutVertically(
                                    targetOffsetY = { -it },
                                    animationSpec = tween(500)
                                ) + fadeOut(animationSpec = tween(500))
                            )
                        }
                    } else Modifier
                )
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
            onMessageClick = { },
            onBookCallClick = { },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .then(
                    if (animatedVisibilityScope != null) {
                        with(animatedVisibilityScope) {
                            Modifier.animateEnterExit(
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(600)
                                ) + fadeIn(animationSpec = tween(600)),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(600)
                                ) + fadeOut(animationSpec = tween(600))
                            )
                        }
                    } else Modifier
                )
        )
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VenueMediaSlider(
    mediaItems: List<VenueMediaItem>,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    vendor: VendorCardData,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { mediaItems.size })

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
                                rememberSharedContentState(key = "image_${vendor.vendorName}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    }
                } else {
                    Modifier.fillMaxSize()
                }

                Box(modifier = sharedBoundsModifier) {
                    AsyncImage(
                        model = mediaItem.url,
                        contentDescription = "Venue Media Slide ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_gallery)
                    )
                }

                if (mediaItem.isVideo) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ContentPrimary.copy(alpha = 0.15f))
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ContentPrimary.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.Center)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Video Preview",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 34.dp)
                .then(
                    if (animatedVisibilityScope != null) {
                        with(animatedVisibilityScope) {
                            Modifier.animateEnterExit(
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(500)
                                ) + fadeIn(animationSpec = tween(500)),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(500)
                                ) + fadeOut(animationSpec = tween(500))
                            )
                        }
                    } else Modifier
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val activeItem = mediaItems.getOrNull(pagerState.currentPage)
            if (activeItem?.isVideo == true) {
                val audioIcon = if(isMuted) painterResource(R.drawable.ic_mute) else painterResource(R.drawable.ic_music)

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
                onClick = { },
                containerColor = ContentInvPrimary.copy(alpha = 0.5f),
                contentColor = ContentPrimary,
                size = ButtonSize.Small,
                leadingIcon = painterResource(R.drawable.ic_gallery)
            )
        }
    }
}

@Composable
fun VenueInfoSection(vendor: VendorCardData) {
    var isExpanded by remember { mutableStateOf(false) }
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
                text = vendor.vendorName,
                style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                color = ContentPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = hasOverflow) { isExpanded = !isExpanded },
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${vendor.location}, India",
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        if (!isExpanded) {
                            hasOverflow = textLayoutResult.hasVisualOverflow
                        }
                    },
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (hasOverflow || isExpanded) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = ContentSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
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
            Surface(
                color = Color(0xFF009B0A),
                shape = RoundedCornerShape(CornerMedium, CornerMedium, 0.dp, 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.ic_star), null, Modifier.size(12.dp), ContentInvPrimary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${vendor.rating}",
                        color = ContentInvPrimary,
                        style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
            Text(
                text = vendor.totalReviews,
                style = JasnifyTheme.typography.labelSmall,
                color = ContentSecondary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

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
fun PricingsSection(vendor: VendorCardData, pricingItems: List<VenuePricingItem>) {
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
fun PricingCard(
    title: String,
    price: String,
    unit: String,
    iconRes: Int,
    labelText: String,
    shape: SquircleShape = SquircleShape(CornerExtraSmall)
) {
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
                        painter = painterResource(iconRes),
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
fun HighlightsSection(highlightItems: List<VenueHighlightItem>) {
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
            HighlightItemRow(highlight)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun HighlightItemRow(data: VenueHighlightItem) {
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
                    painter = painterResource(data.iconRes),
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
fun AboutSection(vendor: VendorCardData, aboutText: String) {
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
            modifier = Modifier.clickable { },
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
fun AskAISection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            modifier = Modifier.padding(vertical = 12.dp)
        )

        val suggestionChips = listOf(
            "How is the vibe here?",
            "What's good here?",
            "Do they serve alcohol?",
            "How many guests they can serve?"
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestionChips.forEach { chip ->
                FilterChip(
                    label = chip,
                    trailingIcon = Icons.Rounded.ArrowOutward,
                    shapeStyle = ChipShapeStyle.Round,
                    hasStroke = true,
                    onClick = { }
                )
            }
        }
    }
}


@Composable
fun GallerySection(galleryCategories: List<GalleryCategoryData>) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val activeCategory = galleryCategories.getOrNull(selectedCategoryIndex)
    val imagesList = activeCategory?.imageUrls.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gallery",
                style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                color = ContentPrimary
            )
            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See all",
                    color = ContentBrandDark,
                    style = JasnifyTheme.typography.labelLarge,
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "See All Gallery",
                    tint = ContentBrandDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(galleryCategories.size) { index ->
                val category = galleryCategories[index]
                val isSelected = selectedCategoryIndex == index

                FilterChip(
                    label = category.categoryName,
                    isSelected = isSelected,
                    shapeStyle = ChipShapeStyle.Round,
                    hasStroke = true,
                    onClick = { selectedCategoryIndex = index }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .clip(SquircleShape(CornerExtraLarge))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        shape = SquircleShape(CornerExtraLarge)
                    )
                    .background(SurfaceSecondary)
            ) {
                AsyncImage(
                    model = imagesList.getOrNull(0) ?: "image_2e5379.jpg",
                    contentDescription = "Main Gallery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = ContentPrimary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "1/${imagesList.size}",
                        color = ContentInvPrimary,
                        style = JasnifyTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(SquircleShape(CornerLarge))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge)
                        )
                        .background(SurfaceSecondary)
                ) {
                    AsyncImage(
                        model = imagesList.getOrNull(1) ?: "image_2e5379.jpg",
                        contentDescription = "Gallery Row 2",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = ContentPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(100),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "2/${imagesList.size}",
                            color = ContentInvPrimary,
                            style = JasnifyTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(SquircleShape(CornerLarge))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge)
                        )
                        .background(SurfaceSecondary)
                ) {
                    AsyncImage(
                        model = imagesList.getOrNull(2) ?: "image_2e5379.jpg",
                        contentDescription = "Gallery Row 3",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = ContentPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(100),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "3/${imagesList.size}",
                            color = ContentInvPrimary,
                            style = JasnifyTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(SquircleShape(CornerLarge))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            shape = SquircleShape(CornerLarge)
                        )
                        .background(SurfaceSecondary)
                        .clickable { }
                ) {
                    AsyncImage(
                        model = imagesList.getOrNull(3) ?: "image_2e5379.jpg",
                        contentDescription = "Gallery Row OverView",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ContentPrimary.copy(alpha = 0.8f))
                    )

                    Surface(
                        color = ContentInvPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(100),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = "See all",
                            color = ContentInvPrimary,
                            style = JasnifyTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ReviewsSection(vendor: VendorCardData, reviewsData: VenueReviewsData) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews",
                style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
                color = ContentPrimary
            )
            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See all",
                    color = ContentBrandDark,
                    style = JasnifyTheme.typography.labelLarge,
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ContentBrandDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF009B0A),
                        shape = RoundedCornerShape(100)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = null,
                                Modifier.size(12.dp),
                                tint = ContentInvPrimary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${vendor.rating}",
                                color = ContentInvPrimary,
                                style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
                Text("${vendor.totalReviews} ratings", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
            }

            reviewsData.ratingBreakdown.forEach { item ->
                VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = ContentTertiary)
                RatingBreakdownItem(item.score, item.label)
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviewsData.reviews) { review ->
                ReviewCard(review)
            }
        }
    }
}

@Composable
fun RatingBreakdownItem(rating: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(rating, style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), color = ContentSecondary)
        Text(label, style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
    }
}

@Composable
fun ReviewCard(review: VenueReviewItem) {
    Surface(
        color = SurfaceSecondary,
        shape = SquircleShape(CornerExtraLarge),
        modifier = Modifier.width(280.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceSecondary)
                    ) {
                        AsyncImage(
                            model = review.userAvatarUrl ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100",
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = review.userName,
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = review.relativeTime,
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentSecondary
                        )
                    }
                }

                Surface(
                    color = Color(0xFF009B0A),
                    shape = RoundedCornerShape(100)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            Modifier.size(12.dp),
                            tint = ContentInvPrimary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${review.rating}",
                            color = ContentInvPrimary,
                            style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = review.reviewText,
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

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
    }
}


@Composable
fun ExploreMoreSection() {
    val suggestionChips = listOf(
        "How is the vibe here?",
        "What's good here?",
        "Do they serve alcohol?",
        "How many guests they can serve?"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
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

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(suggestionChips) { text ->
                FilterChip(
                    label = text,
                    shapeStyle = ChipShapeStyle.Round,
                    onClick = { },
                    hasStroke = true
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(suggestionChips.reversed()) { text ->
                FilterChip(
                    label = text,
                    shapeStyle = ChipShapeStyle.Round,
                    onClick = { },
                    hasStroke = true
                )
            }
        }
    }
}

@Composable
fun SimilarVenuesSection(similarVenues: List<VendorCardData>) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
            items(similarVenues) { vendor ->
                VendorCardCompact(
                    vendor = vendor,
                    compactCardSize = CompactCardSize.SMALL
                )
            }
        }
    }
}