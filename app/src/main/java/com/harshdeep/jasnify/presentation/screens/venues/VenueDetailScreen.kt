package com.harshdeep.jasnify.presentation.screens.venues

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, sheetOffsetPx.roundToInt()) }
                .shadow(24.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    color = SurfacePrimary,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
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
                item {
                    VenueInfoSection(vendor = vendor)
                }

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
                item{
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                item {
                    HighlightsSection()
                }
                item{
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                item {
                    AboutSection(vendor = vendor)
                }
                item{
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                item {
                    AskAISection()
                }
                item{
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                item {
                    ReviewsSection(vendor = vendor)
                }
                item{
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                item {
                    ExploreMoreSection()
                }
                item {
                    DashedDivider(color = MaterialTheme.colorScheme.outline.copy(0.16f), modifier = Modifier.padding(horizontal = 12.dp))
                }

                item {
                    SimilarVenuesSection()
                }

                item {
                    FooterJansify()
                    // Safe bottom offset spacer to make sure LazyColumn content isn't obscured by the persistent actions
                    Spacer(Modifier.height(112.dp))
                }
            }
        }

        val secondaryIcon = if(isFavoriteState) painterResource(R.drawable.ic_heart_filled) else painterResource(R.drawable.ic_heart)

        // Calculate scroll range and progress ratio dynamically to drive gradual opacity
        val scrollRange = maxOffsetPx - minOffsetPx
        val currentScrollOffset = maxOffsetPx - sheetOffsetPx
        val scrollFraction = if (scrollRange > 0f) {
            (currentScrollOffset / scrollRange).coerceIn(0f, 1f)
        } else {
            0f
        }
        // Slowly map progress (0.0 to 1.0) to alpha (0.5f to 1.0f)
        val topBarAlpha = 0.5f + (scrollFraction * 0.5f)

        // Seamlessly switch button style to OPAQUE only when scrolled near the top boundary
        val dynamicButtonStyle = if (topBarAlpha > 0.9f) {
            ButtonBackground.OPAQUE
        } else {
            ButtonBackground.TRANSLUCENT
        }

        Column(
            modifier = Modifier.statusBarsPadding()
        ) {
            CustomTopBar(
                onBackClick = onBackClick,
                secondaryIcon = TopIcon.CustomPainter(painter = secondaryIcon),
                menuIcon = TopIcon.CustomPainter(painter = painterResource(R.drawable.ic_share)),
                backIcon = TopIcon.Predefined.DOWN,
                onSecondaryClick = { isFavoriteState = !isFavoriteState },
                onMenuClick = { /* Handle share context */  },
                buttonStyle = dynamicButtonStyle,
                translucentAlpha = topBarAlpha,
                textColor = ContentPrimary,
            )
        }

        FloatingBottomActionBar(
            onMessageClick = { /* Handle opening messages/chat with venue */ },
            onBookCallClick = { /* Handle phone/video booking call request */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        )
    }
}


// =========================================== Helper Sections =================================================


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
    ){
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
                .padding(horizontal = 12.dp, vertical = 56.dp),
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
                onClick = { /* Gallery navigation */ },
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
        // Left Content: Vendor Name & Location
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

            // Clickable row that toggles expansion if the text overflows
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
                        // Only update overflow state when not expanded to avoid resetting it
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

        // Right Content: Rating Badge
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
    // List of AI suggestion prompts
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
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = ContentBrand,
                height = 4.dp
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
fun PricingsSection(vendor: VendorCardData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        PricingCard(
            title = "Non-Veg Plate",
            price = vendor.priceStartsFrom,
            unit = "starting price",
            iconRes = R.drawable.ic_non_veg,
            shape = SquircleShape(CornerLarge, CornerLarge, CornerExtraSmall,CornerExtraSmall)
        )
        PricingCard(
            title = "Veg Plate",
            price = "₹2,499",
            unit = "per plate",
            iconRes = R.drawable.ic_veg
        )
        PricingCard(
            title = "Rooms",
            price = "₹3,199",
            unit = "per plate",
            iconRes = R.drawable.ic_door,
            shape = SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLarge,CornerLarge)
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
                        text = "Price Point Offer",
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
fun HighlightsSection() {
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

        val highlights = listOf(
            HighlightData("CAPACITY", "200 Guests", painterResource(R.drawable.ic_user_default)),
            HighlightData("CATERING", "In-House Available", painterResource(R.drawable.ic_food)),
            HighlightData("PARKING", "Upto 25 Four-Wheelers", painterResource(R.drawable.ic_car)),
            HighlightData("DECORATION", "In-House Available", painterResource(R.drawable.ic_start_2)),
            HighlightData("SOUND & MUSIC", "In-House DJ Available", painterResource(R.drawable.ic_music))
        )

        highlights.forEach { highlight ->
            HighlightItemRow(highlight)
            Spacer(Modifier.height(12.dp))
        }
    }
}

data class HighlightData(val label: String, val value: String, val icon: Painter)

@Composable
fun HighlightItemRow(data: HighlightData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = SurfaceBrandSecondary,
            shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
            modifier = Modifier
                .size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = data.icon,
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
fun AboutSection(vendor: VendorCardData) {
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
            text = "Discover the charm of ${vendor.vendorName}, located in ${vendor.location}.\n\nThis inviting space blends comfort with high-end luxury, matching your vision perfectly for event styling...",
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
                    onClick = { /* Handle */ }
                )
            }
        }
    }
}

@Composable
fun ReviewsSection(vendor: VendorCardData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding( 12.dp),
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

            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = ContentTertiary)

            RatingBreakdownItem("4.8", "Hospitality")
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = ContentTertiary)
            RatingBreakdownItem("4.4", "Food")
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = ContentTertiary)
            RatingBreakdownItem("4.1", "Ambience")
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = ContentTertiary)
            RatingBreakdownItem("4.2", "Banquets")
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        Text(rating, style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), color = ContentSecondary)
        Text(label, style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
    }
}

@Composable
fun ReviewCard() {
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
                            model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100",
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Anand K.",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "1 week ago",
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
                            text = "3.4",
                            color = ContentInvPrimary,
                            style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Discover the charm of this venue. It has excellent hospitality and top-notch facilities",
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
            val suggestionChips = listOf(
                "How is the vibe here?",
                "What's good here?",
                "Do they serve alcohol?",
                "How many guests they can serve?"
            )

            items(suggestionChips) { text ->
                FilterChip(
                    label = text,
                    shapeStyle = ChipShapeStyle.Round,
                    onClick = { /* Handle */ },
                    hasStroke = true
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val suggestionChips = listOf(
                "How is the vibe here?",
                "What's good here?",
                "Do they serve alcohol?",
                "How many guests they can serve?"
            )

            items(suggestionChips) { text ->
                FilterChip(
                    label = text,
                    shapeStyle = ChipShapeStyle.Round,
                    onClick = { /* Handle */ },
                    hasStroke = true
                )
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
            text = "Showing similar venues",
            style = JasnifyTheme.typography.labelLarge,
            color = ContentSecondary,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleVenues) { vendor ->
                VendorCardCompact(
                    vendor = vendor,
                    compactCardSize = CompactCardSize.SMALL
                )
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