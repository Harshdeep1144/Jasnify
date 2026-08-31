package com.harshdeep.jasnify.presentation.screens.main.tabs.vendors

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Offer
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.VendorGalleryCategory
import com.harshdeep.jasnify.domain.model.VendorHighlightItem
import com.harshdeep.jasnify.domain.model.VendorMediaItem
import com.harshdeep.jasnify.domain.model.VendorPricingItem
import com.harshdeep.jasnify.domain.model.VendorReview
import com.harshdeep.jasnify.domain.model.VendorReviewsData
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.selection.OfferBottomSheet
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.ReviewBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.OfferCard
import com.harshdeep.jasnify.presentation.components.cards.OfferCardType
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.CustomToast
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.ToastData
import com.harshdeep.jasnify.presentation.components.others.ToastType
import com.harshdeep.jasnify.presentation.components.others.VideoPlayer
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.sections.AlbumDetailScreen
import com.harshdeep.jasnify.presentation.components.sections.AllReviewsScreen
import com.harshdeep.jasnify.presentation.components.sections.GalleryCategoryUiModel
import com.harshdeep.jasnify.presentation.components.sections.GalleryDetailScreen
import com.harshdeep.jasnify.presentation.components.sections.GallerySection
import com.harshdeep.jasnify.presentation.components.sections.MediaItemUiModel
import com.harshdeep.jasnify.presentation.components.sections.MediaViewerScreen
import com.harshdeep.jasnify.presentation.components.sections.MerchantReplyUiModel
import com.harshdeep.jasnify.presentation.components.sections.RatingBreakdownUiModel
import com.harshdeep.jasnify.presentation.components.sections.RatingSurface
import com.harshdeep.jasnify.presentation.components.sections.ReviewDetailPostScreen
import com.harshdeep.jasnify.presentation.components.sections.ReviewUiModel
import com.harshdeep.jasnify.presentation.components.sections.ReviewsDataUiModel
import com.harshdeep.jasnify.presentation.components.sections.ReviewsSection
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
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
    DETAIL, REVIEWS, GALLERY, POST, MEDIA_VIEWER, ALBUM_DETAIL
}

fun VendorReview.toUiModel() = ReviewUiModel(
    id = id,
    userId = userId,
    userName = userName,
    userAvatarUrl = userAvatarUrl,
    rating = rating,
    relativeTime = relativeTime,
    reviewText = reviewText,
    isVerified = isVerified,
    attachedImages = attachedImages,
    likedOptions = likedOptions,
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
    lastUpdated = lastUpdated,
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
    onAiSearchClick: (String) -> Unit = {},
    vendorViewModel: VendorViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var screenStack by remember { mutableStateOf(listOf(VendorActiveScreen.DETAIL)) }
    val currentScreen = screenStack.last()

    var selectedReviewForPost by remember { mutableStateOf<ReviewUiModel?>(null) }
    var selectedAlbum by remember { mutableStateOf<GalleryCategoryUiModel?>(null) }
    var gallerySelectedTab by rememberSaveable { mutableStateOf("Images") }
    var mediaViewerList by remember { mutableStateOf<List<MediaItemUiModel>>(emptyList()) }
    var mediaViewerInitialIndex by remember { mutableIntStateOf(0) }

    val listState = rememberLazyListState()

    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()
    val topBarHeightPx = with(density) { 56.dp.toPx() }
    val stickyMarginPx = with(density) { 12.dp.toPx() }
    val minOffsetPx = statusBarHeightPx + topBarHeightPx + stickyMarginPx
    val maxOffsetPx = with(density) { 320.dp.toPx() }

    var sheetOffsetPx by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    val currentSheetOffsetPx = if (sheetOffsetPx.isNaN()) maxOffsetPx else sheetOffsetPx.coerceIn(minOffsetPx, maxOffsetPx)

    val pagerState = rememberPagerState(pageCount = { vendorDetail.mediaItems.size })
    var isMuted by rememberSaveable { mutableStateOf(true) }

    var toastData by remember { mutableStateOf<ToastData?>(null) }
    val context = LocalContext.current

    LaunchedEffect(toastData?.message) {
        if (toastData?.message != null) {
            delay(3000.milliseconds)
            toastData = null
        }
    }

    var showReviewSheet by remember { mutableStateOf(false) }
    var initialRatingForSheet by remember { mutableIntStateOf(0) }
    var showAddressSheet by remember { mutableStateOf(false) }
    var showAboutSheet by remember { mutableStateOf(false) }
    var showOfferSheet by remember { mutableStateOf(false) }
    var selectedOfferForSheet by remember { mutableStateOf<Offer?>(null) }
    var sheetMotionProgress by remember { mutableFloatStateOf(0f) }

    val isSubmitting by vendorViewModel.isReviewSubmitting.collectAsStateWithLifecycle()
    val vendorReviews by vendorViewModel.vendorReviews.collectAsStateWithLifecycle()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userExistingReview = remember(vendorReviews) {
        vendorReviews.find { it.userId == currentUserId }
    }

    val dynamicReviewsData = remember(vendorDetail.reviewsData, vendorReviews) {
        val base = vendorDetail.reviewsData?.toUiModel() ?: ReviewsDataUiModel()
        val combinedReviews = (vendorReviews.map { it.toUiModel() } + base.reviews)
            .distinctBy { it.id.ifBlank { it.userName } }

        base.copy(reviews = combinedReviews)
    }

    LaunchedEffect(vendorDetail.id) {
        vendorViewModel.setSelectedVendorId(vendorDetail.id)
    }

    val anySheetVisible = showReviewSheet || showAddressSheet || showAboutSheet || showOfferSheet
    val targetScale = if (anySheetVisible) 0.92f + (0.08f * sheetMotionProgress) else 1.0f

    val backdropScaleState = animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.82f),
        label = "backdropScale"
    )

    val backdropCornerRadiusState = animateDpAsState(
        targetValue = if (anySheetVisible) 28.dp else 0.dp,
        animationSpec = spring(stiffness = 380f, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "backdropCornerRadius"
    )

    BackHandler(enabled = anySheetVisible || screenStack.size > 1) {
        if (showReviewSheet) { showReviewSheet = false; return@BackHandler }
        if (showAddressSheet) { showAddressSheet = false; return@BackHandler }
        if (showAboutSheet) { showAboutSheet = false; return@BackHandler }
        if (showOfferSheet) { showOfferSheet = false; return@BackHandler }

        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = backdropScaleState.value
                    scaleY = backdropScaleState.value
                    val radius = backdropCornerRadiusState.value
                    clip = anySheetVisible || radius > 0.dp
                    shape = RoundedCornerShape(radius.coerceAtLeast(0.dp))
                }
                .background(BackgroundPrimary)
        ) {
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
                            reviewsData = dynamicReviewsData,
                            listState = listState,
                            pagerState = pagerState,
                            sheetOffsetPx = currentSheetOffsetPx,
                            minOffsetPx = minOffsetPx,
                            maxOffsetPx = maxOffsetPx,
                            onSheetOffsetChange = { sheetOffsetPx = it },
                            isMuted = isMuted,
                            onMuteToggle = { isMuted = !isMuted },
                            onBackClick = onBackClick,
                            onChatClick = onChatClick,
                            onMenuClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    val shareMessage = "Check out ${vendorDetail.name} (${vendorDetail.category}) in ${vendorDetail.location} on Jasnify!\n\nhttps://jasnify.com"
                                    putExtra(Intent.EXTRA_SUBJECT, "Vendor Share")
                                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Vendor"))
                            },
                            onFavoriteToggle = onFavoriteToggle,
                            onSeeAllReviewsClick = { screenStack = screenStack + VendorActiveScreen.REVIEWS },
                            onSeeAllGalleryClick = { screenStack = screenStack + VendorActiveScreen.GALLERY },
                            onMediaClick = { list, index ->
                                mediaViewerList = list
                                mediaViewerInitialIndex = index
                                screenStack = screenStack + VendorActiveScreen.MEDIA_VIEWER
                            },
                            onOpenReviewPost = { review ->
                                selectedReviewForPost = review
                                screenStack = screenStack + VendorActiveScreen.POST
                            },
                            onWriteReviewClick = { rating ->
                                initialRatingForSheet = rating
                                showReviewSheet = true
                            },
                            onOfferClick = { offer ->
                                selectedOfferForSheet = offer
                                showOfferSheet = true
                            },
                            onAddressClick = { showAddressSheet = true },
                            onAboutClick = { showAboutSheet = true },
                            onAiSearchClick = onAiSearchClick,
                            onShowToast = { toastData = it },
                            anySheetVisible = anySheetVisible,
                            hasUserReviewed = userExistingReview != null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    VendorActiveScreen.REVIEWS -> {
                        AllReviewsScreen(
                            title = vendorDetail.name,
                            reviewsData = dynamicReviewsData,
                            ratingValue = vendorDetail.rating.toString(),
                            onBack = { screenStack = screenStack.dropLast(1) },
                            onOpenReviewPost = { review ->
                                selectedReviewForPost = review
                                screenStack = screenStack + VendorActiveScreen.POST
                            },
                            onLeaveReview = {
                                initialRatingForSheet = userExistingReview?.rating?.toInt() ?: 0
                                showReviewSheet = true
                            },
                            leaveReviewButtonText = if (userExistingReview != null) "Edit review" else "Leave a review"
                        )
                    }

                    VendorActiveScreen.GALLERY -> {
                        GalleryDetailScreen(
                            title = vendorDetail.name,
                            galleryCategories = vendorDetail.galleryCategories.map { it.toUiModel() },
                            onBack = { screenStack = screenStack.dropLast(1) },
                            onOpenAlbum = { category ->
                                selectedAlbum = category
                                screenStack = screenStack + VendorActiveScreen.ALBUM_DETAIL
                            },
                            onMediaClick = { list, index ->
                                mediaViewerList = list
                                mediaViewerInitialIndex = index
                                screenStack = screenStack + VendorActiveScreen.MEDIA_VIEWER
                            },
                            selectedTab = gallerySelectedTab,
                            onTabSelected = { gallerySelectedTab = it }
                        )
                    }

                    VendorActiveScreen.ALBUM_DETAIL -> {
                        selectedAlbum?.let { album ->
                            AlbumDetailScreen(
                                category = album,
                                onBack = { screenStack = screenStack.dropLast(1) },
                                onMediaClick = { list, index ->
                                    mediaViewerList = list
                                    mediaViewerInitialIndex = index
                                    screenStack = screenStack + VendorActiveScreen.MEDIA_VIEWER
                                }
                            )
                        }
                    }

                    VendorActiveScreen.POST -> {
                        ReviewDetailPostScreen(
                            review = selectedReviewForPost ?: vendorDetail.reviewsData?.reviews?.firstOrNull()
                                ?.toUiModel() ?: ReviewUiModel(
                                userName = "Username",
                                rating = 4.4,
                                relativeTime = "Just Now",
                                reviewText = "Amazing service!"
                            ),
                            onBack = {
                                screenStack = screenStack.dropLast(1)
                            }
                        )
                    }

                    VendorActiveScreen.MEDIA_VIEWER -> {
                        MediaViewerScreen(
                            mediaItems = mediaViewerList,
                            initialIndex = mediaViewerInitialIndex,
                            onBack = { screenStack = screenStack.dropLast(1) }
                        )
                    }
                }
            }
        }

        if (showReviewSheet) {
            ReviewBottomSheet(
                targetId = vendorDetail.id,
                targetName = vendorDetail.name,
                targetImageUrl = vendorDetail.images.firstOrNull(),
                targetCategory = vendorDetail.category,
                initialRating = initialRatingForSheet,
                initialReviewText = userExistingReview?.reviewText ?: "",
                initialLikedOptions = userExistingReview?.likedOptions?.toSet() ?: emptySet(),
                initialImages = userExistingReview?.attachedImages?.map { Uri.parse(it) } ?: emptyList(),
                isEdit = userExistingReview != null,
                onDismiss = { showReviewSheet = false },
                onSubmit = { rating, text, images, removedImages, likedOptions ->
                    vendorViewModel.submitReview(
                        vendorId = vendorDetail.id,
                        rating = rating.toDouble(),
                        text = text,
                        imageUris = images.map { Uri.parse(it) },
                        removedImageUrls = removedImages,
                        likedOptions = likedOptions
                    )
                },
                onDeleteReview = {
                    vendorViewModel.deleteReview(vendorDetail.id, userExistingReview?.attachedImages ?: emptyList())
                },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showAddressSheet) {
            AddressSheet(
                vendor = vendorDetail,
                onDismiss = { showAddressSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showAboutSheet) {
            AboutSheet(
                vendor = vendorDetail,
                onDismiss = { showAboutSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        if (showOfferSheet) {
            OfferBottomSheet(
                offers = vendorDetail.offers,
                initialOffer = selectedOfferForSheet,
                onDismiss = { showOfferSheet = false },
                onProgress = { sheetMotionProgress = it }
            )
        }

        AnimatedVisibility(
            visible = toastData?.message != null && !anySheetVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .zIndex(100f)
                .padding(horizontal = 12.dp, vertical = 16.dp)
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

@SuppressLint("UseKt")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun VendorDetailContent(
    vendorDetail: Vendor,
    reviewsData: ReviewsDataUiModel,
    listState: LazyListState,
    pagerState: PagerState,
    sheetOffsetPx: Float,
    minOffsetPx: Float,
    maxOffsetPx: Float,
    onSheetOffsetChange: (Float) -> Unit,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onBackClick: () -> Unit,
    onChatClick: (Vendor) -> Unit,
    onMenuClick: () -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    onSeeAllGalleryClick: () -> Unit,
    onMediaClick: (List<MediaItemUiModel>, Int) -> Unit,
    onOpenReviewPost: (ReviewUiModel) -> Unit,
    onWriteReviewClick: (Int) -> Unit,
    onOfferClick: (Offer) -> Unit,
    onAddressClick: () -> Unit,
    onAboutClick: () -> Unit,
    onAiSearchClick: (String) -> Unit,
    onShowToast: (ToastData) -> Unit,
    anySheetVisible: Boolean,
    hasUserReviewed: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val density = LocalDensity.current
    val stickyHeaderHeightPx = with(density) { 56.dp.roundToPx() }

    val activeTabs = remember(vendorDetail, reviewsData, hasUserReviewed) {
        buildList {
            if (vendorDetail.pricingItems.isNotEmpty()) add("Pricings")
            if (vendorDetail.highlightItems.isNotEmpty()) add("Highlights")
            if (vendorDetail.offers.isNotEmpty()) add("Offers")
            if (vendorDetail.aboutText != null) add("About")
            add("Ask AI")
            if (vendorDetail.galleryCategories.isNotEmpty()) add("Gallery")
            if (reviewsData.reviews.isNotEmpty() || !hasUserReviewed) add("Reviews")
        }
    }

    val listKeys = remember(vendorDetail, hasUserReviewed, reviewsData) {
        buildList {
            add("info")
            add("tabs")
            if (vendorDetail.pricingItems.isNotEmpty()) {
                add("pricings")
                add("div_pricings")
            }
            if (vendorDetail.highlightItems.isNotEmpty()) {
                add("highlights")
                add("div_highlights")
            }
            if (vendorDetail.offers.isNotEmpty()) {
                add("offers")
                add("div_offers")
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
            if (reviewsData.reviews.isNotEmpty() || !hasUserReviewed) {
                add("reviews")
                add("div_reviews")
            }
            add("explore_more")
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
                    "offers", "div_offers" -> activeTabs.indexOf("Offers").coerceAtLeast(0)
                    "about", "div_about" -> activeTabs.indexOf("About").coerceAtLeast(0)
                    "ask_ai", "div_ask_ai" -> activeTabs.indexOf("Ask AI").coerceAtLeast(0)
                    "gallery", "div_gallery" -> activeTabs.indexOf("Gallery").coerceAtLeast(0)
                    "reviews", "div_reviews" -> activeTabs.indexOf("Reviews").coerceAtLeast(0)
                    else -> {
                        val pricingsIdx = listKeys.indexOf("pricings").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val highlightsIdx = listKeys.indexOf("highlights").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val offersIdx = listKeys.indexOf("offers").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val aboutIdx = listKeys.indexOf("about").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val askAiIdx = listKeys.indexOf("ask_ai").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val galleryIdx = listKeys.indexOf("gallery").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val reviewsIdx = listKeys.indexOf("reviews").takeIf { it != -1 } ?: Int.MAX_VALUE
                        val firstContentIdx = minOf(pricingsIdx, highlightsIdx, offersIdx, aboutIdx, askAiIdx, galleryIdx, reviewsIdx)
                        if (itemIndex < firstContentIdx) 0 else {
                            activeTabs.size - 1
                        }
                    }
                }
            }
        }
    }

    val nestedScrollConnection = remember(minOffsetPx, maxOffsetPx, sheetOffsetPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                return if (delta < 0 && sheetOffsetPx > minOffsetPx) {
                    val newOffset = (sheetOffsetPx + delta).coerceAtLeast(minOffsetPx)
                    val consumed = newOffset - sheetOffsetPx
                    onSheetOffsetChange(newOffset)
                    Offset(0f, consumed)
                } else Offset.Zero
            }
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                return if (delta > 0 && !listState.canScrollBackward) {
                    val newOffset = (sheetOffsetPx + delta).coerceIn(minOffsetPx, maxOffsetPx)
                    val consumedOffset = newOffset - sheetOffsetPx
                    onSheetOffsetChange(newOffset)
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
        val scrollRange = maxOffsetPx - minOffsetPx
        val scrollFraction = if (scrollRange > 0f) {
            ((maxOffsetPx - sheetOffsetPx) / scrollRange).coerceIn(0f, 1f)
        } else 0f

        val parallaxTranslationY = remember(sheetOffsetPx) {
            val displacement = maxOffsetPx - sheetOffsetPx
            -displacement * 0.45f
        }

        VendorMediaSlider(
            mediaItems = vendorDetail.mediaItems,
            pagerState = pagerState,
            isMuted = isMuted,
            onMuteToggle = onMuteToggle,
            vendor = vendorDetail,
            onSeeAllGalleryClick = onSeeAllGalleryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .graphicsLayer {
                    translationY = parallaxTranslationY
                    alpha = 1f - (scrollFraction * 0.75f)
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, sheetOffsetPx.roundToInt()) }
                .clip(SquircleShape(CornerExtraLarge, CornerExtraLarge))
                .shadow(24.dp, SquircleShape(CornerExtraLarge, CornerExtraLarge))
                .background(BackgroundPrimary, SquircleShape(CornerExtraLarge, CornerExtraLarge))
        ) {
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "info", contentType = "info_section") {
                    VendorInfoSection(
                        vendor = vendorDetail,
                        onAddressClick = onAddressClick
                    )
                }

                stickyHeader(key = "tabs", contentType = "sticky_tabs") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfacePrimary,
                        shadowElevation = if (listState.firstVisibleItemIndex >= (if (hasUserReviewed) 1 else 2)) 4.dp else 0.dp
                    ) {
                        VendorTabs(
                            tabs = activeTabs,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                coroutineScope.launch {
                                    val targetKey = when (activeTabs[index]) {
                                        "Pricings" -> "pricings"
                                        "Highlights" -> "highlights"
                                        "Offers" -> "offers"
                                        "About" -> "about"
                                        "Ask AI" -> "ask_ai"
                                        "Gallery" -> "gallery"
                                        "Reviews" -> "reviews"
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
                    item(key = "pricings", contentType = "pricings_section") {
                        VendorPricingsSection(
                            vendor = vendorDetail,
                            pricingItems = vendorDetail.pricingItems
                        )
                    }
                    item(key = "div_pricings", contentType = "divider") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (vendorDetail.highlightItems.isNotEmpty()) {
                    item(key = "highlights", contentType = "highlights_section") {
                        VendorHighlightsSection(
                            highlightItems = vendorDetail.highlightItems
                        )
                    }
                    item(key = "div_highlights", contentType = "divider") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (vendorDetail.offers.isNotEmpty()) {
                    item(key = "offers", contentType = "offers_section") {
                        VendorOffersSection(
                            offers = vendorDetail.offers,
                            onOfferClick = onOfferClick
                        )
                    }
                    item(key = "div_offers", contentType = "divider") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (vendorDetail.aboutText != null) {
                    item(key = "about", contentType = "about_section") {
                        VendorAboutSection(
                            vendor = vendorDetail,
                            aboutText = vendorDetail.aboutText!!,
                            onReadMoreClick = onAboutClick
                        )
                    }
                    item(key = "div_about", contentType = "divider") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                item(key = "ask_ai", contentType = "ask_ai_section") {
                    VendorAskAISection(onAiSearchClick = onAiSearchClick)
                }

                item(key = "div_ask_ai", contentType = "divider") {
                    DashedDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                if (vendorDetail.galleryCategories.isNotEmpty()) {
                    item(key = "gallery", contentType = "gallery_section") {
                        GallerySection(
                            galleryCategories = vendorDetail.galleryCategories.map { it.toUiModel() },
                            onSeeAllClick = onSeeAllGalleryClick,
                            onMediaClick = onMediaClick
                        )
                    }
                    item(key = "div_gallery", contentType = "divider") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (reviewsData.reviews.isNotEmpty() || !hasUserReviewed) {
                    item(key = "reviews", contentType = "reviews_section") {
                        ReviewsSection(
                            rating = vendorDetail.rating,
                            totalReviews = vendorDetail.totalReviews,
                            reviewsData = reviewsData,
                            onSeeAllClick = onSeeAllReviewsClick,
                            onReviewCardClick = { onOpenReviewPost(it) },
                            onWriteReviewClick = { onWriteReviewClick(it) },
                            hasUserReviewed = hasUserReviewed
                        )
                    }
                    item(key = "div_reviews", contentType = "divider") {
                        DashedDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                item(key = "explore_more", contentType = "explore_more_section") {
                    VendorExploreMoreSection(
                        vendor = vendorDetail,
                        similarVendors = emptyList()
                    )
                }

                item(key = "footer", contentType = "footer") {
                    FooterJansify()
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        val heartFilledPainter = painterResource(R.drawable.ic_heart_filled)
        val heartOutlinePainter = painterResource(R.drawable.ic_top_bar_heart)
        val secondaryIcon = remember(vendorDetail.favorite) {
            if (vendorDetail.favorite) heartFilledPainter else heartOutlinePainter
        }
        val sharePainter = painterResource(R.drawable.ic_share)

        val dynamicButtonStyle = if (scrollFraction > 0.8f) {
            ButtonBackground.OPAQUE
        } else {
            ButtonBackground.TRANSLUCENT
        }

        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .statusBarsPadding()
        ) {
            CustomTopBar(
                title = if (scrollFraction > 0.7f) vendorDetail.name else null,
                isLeftAligned = true,
                onBackClick = onBackClick,
                secondaryIcon = TopIcon.CustomPainter(painter = secondaryIcon, isTinted = false),
                menuIcon = TopIcon.CustomPainter(painter = sharePainter),
                backIcon = TopIcon.Predefined.DOWN,
                onSecondaryClick = {
                    onFavoriteToggle(vendorDetail)
                },
                onMenuClick = onMenuClick,
                buttonStyle = dynamicButtonStyle,
                translucentAlpha = if (scrollFraction > 0.8f) 1f else 0.5f,
                textColor = ContentPrimary,
            )
        }

        AnimatedVisibility(
            visible = !anySheetVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .zIndex(10f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
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
            ) {
                FloatingBottomActionBar(
                    onMessageClick = { onChatClick(vendorDetail) },
                    onBookCallClick = {
                        val phone = vendorDetail.phoneNumber ?: ""
                        if (phone.isEmpty()) {
                            onShowToast(ToastData("No phone number available!", ToastType.DEFAULT))
                            return@FloatingBottomActionBar
                        }
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("VendorDetail", "Error opening dialer: ${e.message}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AddressSheet(
    vendor: Vendor,
    onDismiss: () -> Unit,
    onProgress: (Float) -> Unit
) {
    CustomBottomSheet(
        heading = "Vendor Address",
        onDismiss = onDismiss,
        onProgress = onProgress,
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
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
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
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
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

@Composable
private fun AboutSheet(
    vendor: Vendor,
    onDismiss: () -> Unit,
    onProgress: (Float) -> Unit
) {
    CustomBottomSheet(
        heading = "About ${vendor.name}",
        onDismiss = onDismiss,
        onProgress = onProgress,
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
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
        )

        Column {
            CustomTextButton(
                onClick = onDismiss,
                text = "Okay",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shapeStyle = ButtonShapeStyle.Square
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VendorMediaSlider(
    mediaItems: List<VendorMediaItem>,
    pagerState: PagerState,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    vendor: Vendor,
    onSeeAllGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (mediaItems.size > 1) {
        LaunchedEffect(pagerState) {
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

        val muteIconPainter = painterResource(R.drawable.ic_mute)
        val volumeIconPainter = painterResource(R.drawable.ic_volume)
        val volumeIcon = remember(isMuted) {
            if (isMuted) muteIconPainter else volumeIconPainter
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
                    icon = TopIcon.CustomPainter(painter = volumeIcon),
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
fun VendorInfoSection(
    vendor: Vendor,
    onAddressClick: () -> Unit
) {
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
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = vendor.location,
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = ContentSecondary,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }
        }
        Spacer(Modifier.width(48.dp))

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerMedium))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), RoundedCornerShape(CornerMedium)),
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
            val shape = when {
                pricingItems.size == 1 -> SquircleShape(CornerLarge)
                index == 0 -> SquircleShape(CornerLarge, CornerLarge, CornerExtraSmall, CornerExtraSmall)
                index == pricingItems.lastIndex -> SquircleShape(CornerExtraSmall, CornerExtraSmall, CornerLarge, CornerLarge)
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

        if (pricingItems.size > 3) {
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
}

@Composable
fun VendorOffersSection(
    offers: List<Offer>,
    onOfferClick: (Offer) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Available Offers",
            style = JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Medium),
            color = ContentPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(
                items = offers,
                key = { index, offer -> offer.title + index },
                contentType = { _, _ -> "offer_card" }
            ) { index, offer ->
                OfferCard(
                    title = offer.title,
                    description = offer.description,
                    type = OfferCardType.COMPACT,
                    onViewDetailsClick = { onOfferClick(offer) },
                    modifier = Modifier.width(300.dp),
                    progress = "${index + 1}/${offers.size}"
                )
            }
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
    var isOverflowed by remember { mutableStateOf(false) }

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
            color = ContentSecondary,
            onTextLayout = { textLayoutResult ->
                isOverflowed = textLayoutResult.hasVisualOverflow
            }
        )
        if (isOverflowed) {
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
}

@Composable
fun VendorAskAISection(onAiSearchClick: (String) -> Unit) {
    var aiQuery by remember { mutableStateOf("") }
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
            value = aiQuery,
            onValueChange = { aiQuery = it },
            placeholder = "What would you like to know?",
            isAiSearch = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = androidx.compose.ui.text.input.ImeAction.Search
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onSearch = {
                    if (aiQuery.isNotBlank()) {
                        onAiSearchClick(aiQuery)
                        aiQuery = ""
                    }
                }
            ),
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
            items(
                items = suggestions,
                key = { "suggestion_$it" },
                contentType = { "suggestion_chip" }
            ) { suggestion ->
                FilterChip(
                    label = suggestion,
                    trailingIcon = Icons.Rounded.ArrowOutward,
                    hasStroke = true,
                    onClick = { onAiSearchClick(suggestion) }
                )
            }
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
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .clip(shape = RoundedCornerShape(100, 100, 0, 0)),
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
fun VendorExploreMoreSection(
    vendor: Vendor,
    similarVendors: List<Vendor>,
    modifier: Modifier = Modifier
) {
    val filters = remember(vendor) {
        listOf(
            "Similar to ${vendor.name}",
            "In ${vendor.city}",
            "Top Rated ${vendor.category}",
            "Available now"
        )
    }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Explore more vendors",
            style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
            color = ContentPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = filters,
                key = { _, filterText -> filterText },
                contentType = { _, _ -> "filter_chip" }
            ) { index, filterText ->
                FilterChip(
                    label = filterText,
                    isSelected = selectedFilterIndex == index,
                    onClick = { selectedFilterIndex = index },
                    hasStroke = true
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing similar vendors",
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary,
            )
            Spacer(Modifier.width(12.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = similarVendors,
                key = { it.id },
                contentType = { "similar_vendor_card" }
            ) { vendorItem ->
                VendorCardCompact(
                    vendor = vendorItem,
                    compactCardSize = CompactCardSize.MEDIUM
                )
            }
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