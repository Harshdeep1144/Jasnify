package com.harshdeep.jasnify.presentation.screens.venues

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Data holder representing each item in the header media slider
data class VenueMediaItem(
    val url: String,
    val isVideo: Boolean = false,
    val videoDuration: String? = null
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun VenueDetailScreen(
    vendor: VendorCardData,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pricings", "Highlights", "About", "Ask AI")

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Dynamic offsets matching top bar height + status bar bounds
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()

    // Top bar is roughly 56.dp + we want it to sit 12.dp below the top bar
    val topBarHeightPx = with(density) { 56.dp.toPx() }
    val stickyMarginPx = with(density) { 12.dp.toPx() }

    // Limits of the sliding sheet
    val minOffsetPx = statusBarHeightPx + topBarHeightPx + stickyMarginPx
    val maxOffsetPx = with(density) { 320.dp.toPx() }

    // Animated layout state handling the sheet's slide-up
    var sheetOffsetPx by remember { mutableStateOf(maxOffsetPx) }

    // Automatically match the visible items to correct tab indexing (adjusted for the sheet content)
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val index = listState.firstVisibleItemIndex
        selectedTabIndex = when {
            index >= 6 -> 3 // Ask AI section or below (Index 6)
            index == 5 -> 2 // About section (Index 5)
            index == 4 -> 1 // Highlights section (Index 4)
            else -> 0       // Pricings section or above (Index 3)
        }
    }

    val mediaItems = remember(vendor) {
        listOf(
            VenueMediaItem(
                url = vendor.images.firstOrNull() ?: "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
                isVideo = false
            ),
            VenueMediaItem(
                url = "https://images.unsplash.com/photo-1469371670807-013ccf25f16a?auto=format&fit=crop&w=800",
                isVideo = true,
                videoDuration = "0:15"
            ),
            VenueMediaItem(
                url = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800",
                isVideo = false
            )
        )
    }

    var isFavoriteState by remember { mutableStateOf(vendor.isFavorite) }
    var isMuted by remember { mutableStateOf(true) }

    // Setup nested scroll system to handle sheet dragging vs internal scrolling elegantly
    val nestedScrollConnection = remember(minOffsetPx, maxOffsetPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // When dragging up: slide the sheet up first before allowing lists to scroll
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
                // When dragging down: pull the sheet down if the internal list has reached the top
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
        // Subtle Parallax calculation matching the background translation to the sheet swipe
        val parallaxTranslationY = remember(sheetOffsetPx) {
            val displacement = maxOffsetPx - sheetOffsetPx
            -displacement * 0.45f
        }

        // Media Slider Background Anchor
        VenueMediaSlider(
            mediaItems = mediaItems,
            isMuted = isMuted,
            onMuteToggle = { isMuted = !isMuted },
            vendor = vendor,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .graphicsLayer {
                    translationY = parallaxTranslationY
                }
        )

        // Scrolling sheet overlap docking container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, sheetOffsetPx.roundToInt()) }
                .shadow(24.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    color = SurfacePrimary,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Drag handle container and brand header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(40.dp)
                                .height(5.dp)
                                .background(
                                    color = ContentSecondary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(100)
                                )
                        )

                        VenueInfoSection(vendor = vendor)
                    }
                }

                // AI Suggestion Chips section
                item {
                    SuggestionChipsSection()
                }

                // The Tabs sticky header container ensures beautiful persistence
                stickyHeader {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfacePrimary,
                        shadowElevation = if (listState.firstVisibleItemIndex >= 2) 4.dp else 0.dp
                    ) {
                        VenueTabs(
                            tabs = tabs,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                selectedTabIndex = index
                                coroutineScope.launch {
                                    // Target indices are updated:
                                    // Header (0), Chips (1), Tabs (2), Pricings (3), Highlights (4)...
                                    listState.animateScrollToItem(index + 3)
                                }
                            }
                        )
                    }
                }

                item {
                    PricingsSection(vendor = vendor)
                }

                item {
                    HighlightsSection()
                }

                item {
                    AboutSection(vendor = vendor)
                }

                item {
                    AskAISection()
                }

                item {
                    ReviewsSection(vendor = vendor)
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    DashedDivider(Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    ExploreMoreSection()
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    DashedDivider(Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    SimilarVenuesSection()
                }

                item {
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        // Overlaid Top Bar (Static, stays perfectly positioned at the top boundary)
        VenueCustomTopBar(
            isFavorite = isFavoriteState,
            onBackClick = onBackClick,
            onFavoriteClick = { isFavoriteState = !isFavoriteState },
            onShareClick = { /* Handle share context */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueCustomTopBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(
                icon = Icons.Default.KeyboardArrowLeft,
                onClick = onBackClick
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HeaderIconButton(
                    icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    iconColor = if (isFavorite) Color.Red else Color.White,
                    onClick = onFavoriteClick
                )
                HeaderIconButton(
                    icon = Icons.Default.Share,
                    onClick = onShareClick
                )
            }
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
                        placeholder = painterResource(R.drawable.img_onboarding_1)
                    )
                }

                if (mediaItem.isVideo) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.15f))
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
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

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 90.dp, end = 16.dp)
                        ) {
                            Text(
                                text = mediaItem.videoDuration ?: "0:00",
                                color = Color.White,
                                style = JasnifyTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val activeItem = mediaItems.getOrNull(pagerState.currentPage)
            if (activeItem?.isVideo == true) {
                HeaderIconButton(
                    icon = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    onClick = onMuteToggle
                )
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(mediaItems.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable { /* Gallery navigation */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Open Gallery",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Gallery (${mediaItems.size})",
                        color = Color.White,
                        style = JasnifyTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderIconButton(
    icon: ImageVector,
    iconColor: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = Color.Black.copy(alpha = 0.4f),
        shape = CircleShape,
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun VenueInfoSection(vendor: VendorCardData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = vendor.vendorName,
                style = JasnifyTheme.typography.displaySmall,
                color = ContentPrimary,
                modifier = Modifier.weight(1f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = Color(0xFF009B0A),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, Modifier.size(14.dp), Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${vendor.rating}",
                            color = Color.White,
                            style = JasnifyTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Text(
                    vendor.totalReviews,
                    style = JasnifyTheme.typography.labelSmall,
                    color = ContentSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${vendor.location}, India",
                style = JasnifyTheme.typography.bodyMedium,
                color = ContentSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.KeyboardArrowDown, null, tint = ContentSecondary)
        }
    }
}

@Composable
fun SuggestionChipsSection() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        item {
            SuggestionChipWithIcon(text = "What's good here?")
        }
        item {
            SuggestionChipWithIcon(text = "How many guests they can serve?")
        }
    }
}

@Composable
fun SuggestionChipWithIcon(text: String) {
    Surface(
        onClick = { },
        color = SurfaceSecondary.copy(alpha = 0.5f),
        shape = SquircleShape(12.dp),
        border = BorderStroke(1.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ai),
                contentDescription = "AI Suggestion",
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
            Text(text, style = JasnifyTheme.typography.labelMedium, color = ContentSecondary)
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
        edgePadding = 16.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = ContentBrand,
                height = 3.dp
            )
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
                            style = JasnifyTheme.typography.labelLarge,
                            color = if (isSelected) ContentPrimary else ContentSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (title == "Ask AI") {
                            Spacer(Modifier.width(4.dp))
                            Surface(
                                color = Color(0xFFC7A8F7),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "NEW",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
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
fun PricingsSection(vendor: VendorCardData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PricingCard(
            title = "Whole Venue Package",
            price = vendor.priceStartsFrom,
            unit = "starting price",
            iconRes = R.drawable.ic_ai
        )
        PricingCard(
            title = "Veg Plate",
            price = "₹2,499",
            unit = "per plate",
            iconRes = R.drawable.ic_veg
        )
        PricingCard(
            title = "Non-Veg Plate",
            price = "₹3,199",
            unit = "per plate",
            iconRes = R.drawable.ic_non_veg
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "See full pricings",
                color = Color(0xFF397F7F),
                style = JasnifyTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = Color(0xFF397F7F),
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
    iconRes: Int
) {
    Surface(
        color = SurfaceSecondary.copy(alpha = 0.3f),
        shape = SquircleShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color.White,
                    shape = SquircleShape(8.dp),
                    modifier = Modifier.size(40.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, style = JasnifyTheme.typography.headingMedium, color = ContentPrimary)
                    Text("Price Point Offer", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(price, style = JasnifyTheme.typography.headingLarge, color = ContentPrimary)
                Text(unit, style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
            }
        }
    }
}

@Composable
fun HighlightsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Highlights",
            style = JasnifyTheme.typography.headingLarge,
            color = ContentPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val highlights = listOf(
            HighlightData("CAPACITY", "200 Guests", Icons.Outlined.People),
            HighlightData("CATERING", "In-House Available", Icons.Outlined.Restaurant),
            HighlightData("PARKING", "Upto 25 Four-Wheelers", Icons.Outlined.DirectionsCar),
            HighlightData("DECORATION", "In-House Available", Icons.Outlined.AutoAwesome),
            HighlightData("SOUND & MUSIC", "In-House DJ Available", Icons.Outlined.MusicNote)
        )

        highlights.forEach { highlight ->
            HighlightItemRow(highlight)
            Spacer(Modifier.height(16.dp))
        }
    }
}

data class HighlightData(val label: String, val value: String, val icon: ImageVector)

@Composable
fun HighlightItemRow(data: HighlightData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFFD7E3E3),
            shape = SquircleShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(data.icon, null, tint = Color(0xFF395555), modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(data.label, style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
            Text(data.value, style = JasnifyTheme.typography.bodyLarge, color = ContentPrimary)
        }
    }
}

@Composable
fun AboutSection(vendor: VendorCardData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "About this venue",
            style = JasnifyTheme.typography.headingLarge,
            color = ContentPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            "Discover the charm of ${vendor.vendorName}, located in ${vendor.location}.\n\nThis inviting space blends comfort with high-end luxury, matching your vision perfectly for event styling...",
            style = JasnifyTheme.typography.bodyLarge,
            color = ContentSecondary,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.clickable { },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Read more",
                color = Color(0xFF397F7F),
                style = JasnifyTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = Color(0xFF397F7F),
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
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Ask anything about this venue",
                style = JasnifyTheme.typography.headingLarge,
                color = ContentPrimary
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                color = Color(0xFFC7A8F7),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "NEW",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("What would you like to know?", color = ContentSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(100)),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai),
                    contentDescription = "Ask AI",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceSecondary.copy(alpha = 0.2f),
                unfocusedContainerColor = SurfaceSecondary.copy(alpha = 0.2f),
                focusedIndicatorColor = Color(0xFFC7A8F7),
                unfocusedIndicatorColor = Color(0xFFC7A8F7).copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(100)
        )

        Spacer(Modifier.height(16.dp))

        val aiChips = listOf(
            "How is the vibe here?",
            "What's good here?",
            "Do they serve alcohol?",
            "How many guests they can serve?"
        )

        aiChips.forEach { chip ->
            Surface(
                onClick = {},
                color = SurfaceSecondary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(100),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(chip, style = JasnifyTheme.typography.bodyLarge, color = ContentSecondary)
                    Icon(Icons.Default.NorthEast, null, tint = ContentSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun ReviewsSection(vendor: VendorCardData) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reviews", style = JasnifyTheme.typography.headingLarge, color = ContentPrimary)
            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "See all",
                    color = Color(0xFF397F7F),
                    style = JasnifyTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    null,
                    tint = Color(0xFF397F7F),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF009B0A),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, null, Modifier.size(14.dp), Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${vendor.rating}",
                                color = Color.White,
                                style = JasnifyTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
                Text("${vendor.totalReviews} ratings", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
            }

            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = Color.LightGray)

            RatingBreakdownItem("4.8", "Hospitality")
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = Color.LightGray)
            RatingBreakdownItem("4.4", "Food")
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = Color.LightGray)
            RatingBreakdownItem("4.1", "Ambience")
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = Color.LightGray)
            RatingBreakdownItem("4.2", "Banquets")
        }

        Spacer(Modifier.height(24.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) {
                ReviewCard()
            }
        }
    }
}

@Composable
fun RatingBreakdownItem(rating: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(rating, style = JasnifyTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = ContentPrimary)
        Text(label, style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
    }
}

@Composable
fun ReviewCard() {
    Surface(
        color = SurfaceSecondary.copy(alpha = 0.3f),
        shape = SquircleShape(20.dp),
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
                            .background(Color.LightGray)
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100",
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Anand K.", style = JasnifyTheme.typography.headingMedium, color = ContentPrimary)
                        Text("1 week ago", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
                    }
                }

                Surface(
                    color = Color(0xFF009B0A),
                    shape = RoundedCornerShape(100)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, Modifier.size(12.dp), Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "4.4",
                            color = Color.White,
                            style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Discover the charm of this venue. It has excellent hospitality and top-notch facilities...",
                style = JasnifyTheme.typography.bodyMedium,
                color = ContentSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "See all",
                    color = Color(0xFF397F7F),
                    style = JasnifyTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    null,
                    tint = Color(0xFF397F7F),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ExploreMoreSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Explore more venues", style = JasnifyTheme.typography.headingLarge, color = ContentPrimary)
            Row(
                modifier = Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "See all",
                    color = Color(0xFF397F7F),
                    style = JasnifyTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    null,
                    tint = Color(0xFF397F7F),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("How is the vibe here?", "What's good here?", "Do they serve alcohol?")) { text ->
                Surface(
                    onClick = {},
                    color = SurfaceSecondary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(100),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Text(
                        text,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = JasnifyTheme.typography.bodyLarge,
                        color = ContentSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("How is the vibe here?", "What's good here?", "Do they serve alcohol?")) { text ->
                Surface(
                    onClick = {},
                    color = SurfaceSecondary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(100),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Text(
                        text,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = JasnifyTheme.typography.bodyLarge,
                        color = ContentSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarVenuesSection() {
    val sampleVenues = List(3) {
        VendorCardData(
            vendorName = "Hotel Imperial Inn",
            location = "Sampatchak, Patna",
            rating = 4.4,
            totalReviews = "1.4k",
            services = emptyList(),
            priceStartsFrom = "₹2,999",
            images = listOf("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800")
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Showing similar venues",
            style = JasnifyTheme.typography.labelLarge,
            color = ContentSecondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleVenues) { vendor ->
                VendorCardCompact(vendor = vendor)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VenueDetailScreenPreview() {
    val mockVendor = VendorCardData(
        vendorName = "Hotel Imperial Inn",
        location = "Sampatchak, Patna",
        rating = 4.4,
        totalReviews = "1.4k",
        services = emptyList(),
        priceStartsFrom = "₹2,999",
        images = listOf("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800")
    )
    JasnifyTheme {
        VenueDetailScreen(vendor = mockVendor)
    }
}