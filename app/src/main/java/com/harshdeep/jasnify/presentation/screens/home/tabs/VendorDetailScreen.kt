package com.harshdeep.jasnify.presentation.screens.home.tabs

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.VendorGalleryCategory
import com.harshdeep.jasnify.domain.model.VendorHighlightItem
import com.harshdeep.jasnify.domain.model.VendorMediaItem
import com.harshdeep.jasnify.domain.model.VendorPricingItem
import com.harshdeep.jasnify.domain.model.VendorReview
import com.harshdeep.jasnify.domain.model.VendorReviewsData
import com.harshdeep.jasnify.presentation.components.bottomdrawer.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.VideoPlayer
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.pill360Shadow
import com.harshdeep.jasnify.presentation.components.sections.AllReviewsScreen
import com.harshdeep.jasnify.presentation.components.sections.GalleryCategoryUiModel
import com.harshdeep.jasnify.presentation.components.sections.GalleryDetailScreen
import com.harshdeep.jasnify.presentation.components.sections.GallerySection
import com.harshdeep.jasnify.presentation.components.sections.MediaItemUiModel
import com.harshdeep.jasnify.presentation.components.sections.MerchantReplyUiModel
import com.harshdeep.jasnify.presentation.components.sections.RatingBreakdownUiModel
import com.harshdeep.jasnify.presentation.components.sections.RatingSurface
import com.harshdeep.jasnify.presentation.components.sections.ReviewDetailPostScreen
import com.harshdeep.jasnify.presentation.components.sections.ReviewUiModel
import com.harshdeep.jasnify.presentation.components.sections.ReviewsDataUiModel
import com.harshdeep.jasnify.presentation.components.sections.ReviewsSection
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
import kotlin.time.Duration.Companion.milliseconds

enum class VendorActiveScreen {
    DETAIL, REVIEWS, GALLERY, POST
}

fun VendorReview.toUiModel() = ReviewUiModel(
    id = id,
    userName = userName,
    userAvatarUrl = userAvatarUrl,
    rating = rating,
    relativeTime = relativeTime,
    reviewText = reviewText,
    isVerified = isVerified,
    attachedImages = attachedImages,
    merchantReply = merchantReply?.let {
        MerchantReplyUiModel(
            merchantName = it.merchantName,
            merchantAvatarUrl = it.merchantAvatarUrl,
            relativeTime = it.relativeTime,
            replyText = it.replyText,
            isVerified = it.isVerified
        )
    }
)

fun VendorReviewsData.toUiModel() = ReviewsDataUiModel(
    reviews = reviews.map { it.toUiModel() },
    ratingBreakdown = ratingBreakdown.map { RatingBreakdownUiModel(it.score, it.label) },
    totalRatingsCount = totalRatingsCount,
    distribution = distribution,
    subMetrics = subMetrics.map { RatingBreakdownUiModel(it.score, it.label) }
)

fun VendorMediaItem.toUiModel() = MediaItemUiModel(
    url = url,
    video = video,
    videoDuration = videoDuration
)

fun VendorGalleryCategory.toUiModel() = GalleryCategoryUiModel(
    categoryName = categoryName,
    mediaItems = mediaItems.map { it.toUiModel() }
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VendorDetailScreen(
    vendorDetail: Vendor,
    onBackClick: () -> Unit = {},
    onChatClick: (Vendor) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onFavoriteToggle: (Vendor) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var screenStack by remember { mutableStateOf(listOf(VendorActiveScreen.DETAIL)) }
    val currentScreen = screenStack.last()

    var selectedReviewForPost by remember { mutableStateOf<ReviewUiModel?>(null) }

    BackHandler(enabled = screenStack.size > 1) {
        screenStack = screenStack.dropLast(1)
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        },
        label = "VendorNavigationTransition"
    ) { screen ->
        when (screen) {
            VendorActiveScreen.DETAIL -> {
                VendorDetailContent(
                    vendorDetail = vendorDetail,
                    onBackClick = onBackClick,
                    onChatClick = onChatClick,
                    onMenuClick = onMenuClick,
                    onFavoriteToggle = onFavoriteToggle,
                    onSeeAllReviewsClick = { screenStack = screenStack + VendorActiveScreen.REVIEWS },
                    onSeeAllGalleryClick = { screenStack = screenStack + VendorActiveScreen.GALLERY },
                    onOpenReviewPost = { review ->
                        selectedReviewForPost = review
                        screenStack = screenStack + VendorActiveScreen.POST
                    },
                    modifier = modifier
                )
            }
            VendorActiveScreen.REVIEWS -> {
                AllReviewsScreen(
                    title = vendorDetail.name,
                    reviewsData = vendorDetail.reviewsData?.toUiModel() ?: ReviewsDataUiModel(),
                    ratingValue = vendorDetail.rating.toString(),
                    onBack = { screenStack = screenStack.dropLast(1) },
                    onOpenReviewPost = { review ->
                        selectedReviewForPost = review
                        screenStack = screenStack + VendorActiveScreen.POST
                    },
                    onLeaveReview = { }
                )
            }
            VendorActiveScreen.GALLERY -> {
                GalleryDetailScreen(
                    title = vendorDetail.name,
                    galleryCategories = vendorDetail.galleryCategories.map { it.toUiModel() },
                    onBack = { screenStack = screenStack.dropLast(1) },
                    onOpenAlbum = { }
                )
            }
            VendorActiveScreen.POST -> {
                ReviewDetailPostScreen(
                    review = selectedReviewForPost ?: vendorDetail.reviewsData?.reviews?.firstOrNull()?.toUiModel() ?: ReviewUiModel(
                        userName = "Anand K.",
                        rating = 4.4,
                        relativeTime = "1 week ago",
                        reviewText = "Amazing service!"
                    ),
                    onBack = {
                        screenStack = screenStack.dropLast(1)
                    }
                )
            }
        }
    }
}

@SuppressLint("UseKt")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun VendorDetailContent(
    vendorDetail: Vendor,
    onBackClick: () -> Unit,
    onChatClick: (Vendor) -> Unit,
    onMenuClick: () -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    onSeeAllGalleryClick: () -> Unit,
    onOpenReviewPost: (ReviewUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val vendor = vendorDetail

    var showAddressSheet by remember { mutableStateOf(false) }
    var showAboutSheet by remember { mutableStateOf(false) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0f) }

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

    var sheetOffsetPx by remember { mutableFloatStateOf(maxOffsetPx) }
    var isMuted by remember { mutableStateOf(true) }

    val activeTabs = remember(vendorDetail) {
        buildList {
            if (vendorDetail.pricingItems.isNotEmpty()) add("Pricings")
            if (vendorDetail.highlightItems.isNotEmpty()) add("Highlights")
            if (vendorDetail.aboutText != null) add("About")
            add("Ask AI")
        }
    }

    val listKeys = remember(vendorDetail) {
        buildList {
            add("info")
            add("suggestions")
            add("tabs")
            if (vendorDetail.pricingItems.isNotEmpty()) {
                add("pricings")
                add("div_pricings")
            }
            if (vendorDetail.highlightItems.isNotEmpty()) {
                add("highlights")
                add("div_highlights")
            }
            if (vendorDetail.aboutText != null) {
                add("about")
                add("div_about")
            }
            add("ask_ai")
            add("div_ask_ai")
            if (vendorDetail.galleryCategories.isNotEmpty()) {
                add("gallery")
                add("div_gallery")
            }
            if (vendorDetail.reviewsData != null && vendorDetail.reviewsData.reviews.isNotEmpty()) {
                add("reviews")
                add("div_reviews")
            }
            add("footer")
        }
    }

    val selectedTabIndex by remember(activeTabs, listKeys) {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0 else {
                val thresholdPx = stickyHeaderHeightPx.toFloat()
                val candidateItem = visibleItems.firstOrNull { it.offset + it.size > thresholdPx + 20f } ?: visibleItems.first()
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
                        if (itemIndex < firstContentIdx) 0 else {
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
                } else Offset.Zero
            }
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                return if (delta > 0 && !listState.canScrollBackward) {
                    val newOffset = (sheetOffsetPx + delta).coerceIn(minOffsetPx, maxOffsetPx)
                    val consumedOffset = newOffset - sheetOffsetPx
                    sheetOffsetPx = newOffset
                    Offset(0f, consumedOffset)
                } else Offset.Zero
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

        VendorMediaSlider(
            mediaItems = vendorDetail.mediaItems,
            isMuted = isMuted,
            onMuteToggle = { isMuted = !isMuted },
            vendor = vendor,
            onSeeAllGalleryClick = onSeeAllGalleryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .graphicsLayer { translationY = parallaxTranslationY }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, sheetOffsetPx.roundToInt()) }
                .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge))
                .shadow(24.dp, SquircleShape(CornerExtraLarge, CornerExtraLarge))
                .background(BackgroundPrimary, SquircleShape(CornerExtraLarge, CornerExtraLarge))
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
                        .background(ContentTertiary, RoundedCornerShape(100))
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "info") {
                    VendorInfoSection(
                        vendor = vendor,
                        onAddressClick = { showAddressSheet = true }
                    )
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
                        VendorTabs(
                            tabs = activeTabs,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                coroutineScope.launch {
                                    val targetKey = when (activeTabs[index]) {
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

                if (vendorDetail.pricingItems.isNotEmpty()) {
                    item(key = "pricings") {
                        VendorPricingsSection(
                            vendor = vendor,
                            pricingItems = vendorDetail.pricingItems
                        )
                    }
                    item(key = "div_pricings") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (vendorDetail.highlightItems.isNotEmpty()) {
                    item(key = "highlights") {
                        VendorHighlightsSection(
                            highlightItems = vendorDetail.highlightItems
                        )
                    }
                    item(key = "div_highlights") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (vendorDetail.aboutText != null) {
                    item(key = "about") {
                        VendorAboutSection(
                            vendor = vendor,
                            aboutText = vendorDetail.aboutText!!,
                            onReadMoreClick = { showAboutSheet = true }
                        )
                    }
                    item(key = "div_about") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                item(key = "ask_ai") {
                    VendorAskAISection()
                }

                item(key = "div_ask_ai") {
                    DashedDivider(
                        color = MaterialTheme.colorScheme.outline.copy(0.16f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                if (vendorDetail.galleryCategories.isNotEmpty()) {
                    item(key = "gallery") {
                        GallerySection(
                            galleryCategories = vendorDetail.galleryCategories.map { it.toUiModel() },
                            onSeeAllClick = onSeeAllGalleryClick
                        )
                    }
                    item(key = "div_gallery") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (vendorDetail.reviewsData != null && vendorDetail.reviewsData!!.reviews.isNotEmpty()) {
                    item(key = "reviews") {
                        ReviewsSection(
                            rating = vendor.rating,
                            totalReviews = vendor.totalReviews,
                            reviewsData = vendorDetail.reviewsData!!.toUiModel(),
                            onSeeAllClick = onSeeAllReviewsClick,
                            onReviewCardClick = { onOpenReviewPost(it) },
                            onWriteReviewClick = { }
                        )
                    }
                    item(key = "div_reviews") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                item(key = "footer") {
                    FooterJansify()
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        val secondaryIcon = if (vendorDetail.favorite) {
            painterResource(R.drawable.ic_heart_filled)
        } else {
            painterResource(R.drawable.ic_top_bar_heart)
        }

        val scrollRange = maxOffsetPx - minOffsetPx
        val scrollFraction = if (scrollRange > 0f) {
            ((maxOffsetPx - sheetOffsetPx) / scrollRange).coerceIn(0f, 1f)
        } else 0f
        val topBarAlpha = 0.5f + (scrollFraction * 0.5f)
        val dynamicButtonStyle = if (topBarAlpha > 0.9f) ButtonBackground.OPAQUE else ButtonBackground.TRANSLUCENT

        Column(modifier = Modifier.statusBarsPadding()) {
            CustomTopBar(
                onBackClick = onBackClick,
                secondaryIcon = TopIcon.CustomPainter(painter = secondaryIcon),
                menuIcon = TopIcon.CustomPainter(painter = painterResource(R.drawable.ic_share)),
                backIcon = TopIcon.Predefined.DOWN,
                onSecondaryClick = {
                    onFavoriteToggle(vendorDetail)
                },
                onMenuClick = onMenuClick,
                buttonStyle = dynamicButtonStyle,
                translucentAlpha = topBarAlpha,
                textColor = ContentPrimary,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.25f to BackgroundPrimary.copy(alpha = 0.15f),
                            0.55f to BackgroundPrimary.copy(alpha = 0.65f),
                            0.80f to BackgroundPrimary.copy(alpha = 0.92f),
                            1.00f to BackgroundPrimary
                        )
                    )
                )
                .navigationBarsPadding()
                .zIndex(10f)
        ) {
            FloatingBottomActionBar(
                onMessageClick = { onChatClick(vendor) },
                onBookCallClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        if (showAddressSheet) {
            CustomBottomSheet(
                heading = "Vendor Address",
                onDismiss = { showAddressSheet = false },
                onProgress = { sheetMotionProgress = it },
                sheetHeight = 360.dp
            ) {
                val context = LocalContext.current
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(0.16f)
                    )

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
                                Icon(
                                    painter = painterResource(id = R.drawable.img_hero_venueaddress),
                                    contentDescription = "Map Pin Logo",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = vendor.name,
                                    style = JasnifyTheme.typography.displayMedium,
                                    color = ContentPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = vendor.location,
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentSecondary
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(0.16f)
                    )

                    CustomTextButton(
                        onClick = {
                            val mapQuery = "${vendor.name}, ${vendor.location}"
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
                                } catch (ignored: Exception) {
                                }
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
            CustomBottomSheet(
                heading = "About ${vendor.name}",
                onDismiss = { showAboutSheet = false },
                onProgress = { sheetMotionProgress = it },
                sheetHeight = null,
                showDragHandle = true,
                showCloseButton = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    val aboutFromDb = vendor.aboutText ?: "No information available."
                    Text(
                        text = aboutFromDb.replace(". ", ".\n\n"),
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentSecondary
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(0.16f)
                )

                Column {
                    CustomTextButton(
                        onClick = { showAboutSheet = false },
                        text = "Okay",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shapeStyle = ButtonShapeStyle.Square
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VendorMediaSlider(
    mediaItems: List<VendorMediaItem>,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    vendor: Vendor,
    onSeeAllGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { mediaItems.size })

    if (mediaItems.size > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000.milliseconds)
                if (!pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage((pagerState.currentPage + 1) % mediaItems.size)
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
                if (mediaItem.video) {
                    VideoPlayer(
                        videoUrl = mediaItem.url,
                        isMuted = isMuted,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = mediaItem.url,
                        contentDescription = "Vendor Media Slide ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.img_placeholder_venue_vendor)
                    )
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
            if (mediaItems.getOrNull(pagerState.currentPage)?.video == true) {
                TopBarIconButton(
                    icon = TopIcon.CustomPainter(
                        painter = if (isMuted) painterResource(R.drawable.ic_mute) else painterResource(R.drawable.ic_volume)
                    ),
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
fun VendorInfoSection(vendor: Vendor, onAddressClick: () -> Unit) {
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
                text = vendor.name,
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
                    text = vendor.location,
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = ContentSecondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerMedium))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.16f), RoundedCornerShape(CornerMedium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RatingSurface(
                rating = vendor.rating.toString(),
                shape = RoundedCornerShape(CornerMedium, CornerMedium, 0.dp, 0.dp)
            )
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
fun VendorPricingsSection(vendor: Vendor, pricingItems: List<VendorPricingItem>) {
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
                style = JasnifyTheme.typography.labelLarge
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = ContentBrandDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun VendorHighlightsSection(highlightItems: List<VendorHighlightItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
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
fun VendorAboutSection(vendor: Vendor, aboutText: String, onReadMoreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = "About this vendor",
            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
            color = ContentPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = aboutText,
            style = JasnifyTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 4,
            color = ContentSecondary
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.clickable { onReadMoreClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Read more",
                color = ContentBrandDark,
                style = JasnifyTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = ContentBrandDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun VendorAskAISection() {
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
                text = "Ask anything about this vendor",
                style = JasnifyTheme.typography.displaySmall,
                color = ContentPrimary
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                color = Color(0xFFB66FC4),
                shape = RoundedCornerShape(100)
            ) {
                Text(
                    text = "NEW",
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
        val suggestions = listOf(
            "How is the vibe here?",
            "What's good here?",
            "Do they serve alcohol?",
            "How many guests they can serve?"
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(suggestions) { suggestion ->
                FilterChip(
                    label = suggestion,
                    trailingIcon = Icons.Rounded.ArrowOutward,
                    hasStroke = true,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun SuggestionChipsSection(onClickSuggestion: (String) -> Unit = {}) {
    val suggestions = listOf(
        "What's good here?",
        "View popular dishes",
        "Any ongoing offers?",
        "Check real-time crowd status"
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(suggestions) { suggestion ->
            FilterChip(
                label = suggestion,
                isSelected = false,
                shapeStyle = ChipShapeStyle.Round,
                size = ChipSize.Small,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.ic_ai),
                onClick = { onClickSuggestion(suggestion) },
                hasStroke = true,
                isAiMode = true
            )
        }
    }
}

@Composable
fun VendorTabs(
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
                            color = if (isSelected) ContentPrimary else ContentSecondary
                        )
                        if (title == "Ask AI") {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFB66FC4),
                                shape = RoundedCornerShape(100)
                            ) {
                                Text(
                                    text = "NEW",
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
        if (iconName.isNullOrBlank()) {
            R.drawable.ic_gallery
        } else {
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
                Icon(
                    painter = painterResource(resId),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentPrimary
                    )
                    Text(
                        text = labelText,
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
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
fun HighlightItemRow(data: VendorHighlightItem) {
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
            .fillMaxWidth()
            .height(62.dp)
            .pill360Shadow(
                ambientColor = Color.Black.copy(alpha = 0.10f),
                ambientBlur = 12.dp,
                ambientSpread = 2.dp,
                spotColor = Color.Black.copy(alpha = 0.15f),
                spotBlur = 18.dp,
                spotOffsetY = 4.dp
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
                modifier = Modifier.weight(0.4f),
                shapeStyle = ButtonShapeStyle.Round
            )
            Spacer(modifier = Modifier.width(4.dp))
            CustomTextButton(
                onClick = onBookCallClick,
                text = "Book a Call",
                type = ButtonType.Primary,
                modifier = Modifier.weight(1.6f),
                shapeStyle = ButtonShapeStyle.Round
            )
        }
    }
}