package com.harshdeep.jasnify.presentation.components.cards

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.states.CompactCardLoading
import com.harshdeep.jasnify.presentation.components.states.FullCardLoading
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceInvSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private const val VIRTUAL_PAGE_COUNT = 10000
private data class VendorShadowLayer(val offsetY: Dp, val blur: Dp, val alpha: Float)


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VendorCardFull(
    vendor: Vendor,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onCardClick: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onOfferClick: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    if (isLoading) {
        FullCardLoading(modifier = modifier)
        return
    }
    val actualPageCount = vendor.images.size
    val virtualCount = if (actualPageCount > 1) VIRTUAL_PAGE_COUNT else actualPageCount
    val initialPage =
        if (actualPageCount > 1) (VIRTUAL_PAGE_COUNT / 2) - ((VIRTUAL_PAGE_COUNT / 2) % actualPageCount) else 0

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualCount }
    )

    if (actualPageCount > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000.milliseconds)
                if (!pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }

    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .vendorShadow(borderRadius = CornerLargeIncrease),
        shape = SquircleShape(CornerLargeIncrease),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
    ) {
        Column {
            val sharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "vendor_image_${vendor.name}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                }
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            }

            Box(modifier = sharedBoundsModifier) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val actualIndex = if (actualPageCount > 0) page % actualPageCount else 0
                    val imageUrl = vendor.images.getOrNull(actualIndex) ?: ""
                    VendorImage(
                        url = imageUrl,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SurfaceSecondary)
                    )
                }

                OfferBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    onClick = onOfferClick
                )

                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(CircleShape)
                        .clickable { onFavoriteToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    val iconRes =
                        if (vendor.favorite) painterResource(R.drawable.ic_heart_filled) else painterResource(
                            R.drawable.ic_heart
                        )
                    Icon(
                        painter = iconRes,
                        contentDescription = "Favorite Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp),
                    )
                }

                val hasEnquiries = vendor.enquiriesLastMonth > 0
                val dotsBottomPadding = if (hasEnquiries) (26.dp + 12.dp) else 12.dp

                CarouselDots(
                    pageCount = actualPageCount,
                    currentPage = if (actualPageCount > 0) pagerState.currentPage % actualPageCount else 0,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                            top = 12.dp,
                            bottom = dotsBottomPadding
                        )
                )

                VendorBannerRow(
                    vendor = vendor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }

            Column(
                modifier = Modifier
                    .background(SurfacePrimary)
                    .padding(12.dp),
            ) {
                Text(
                    text = vendor.name,
                    style = JasnifyTheme.typography.headingLarge,
                    color = ContentPrimary
                )
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location_marker),
                            contentDescription = "Location Pin",
                            modifier = Modifier.size(16.dp),
                            tint = ContentSecondary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${vendor.locality}, ${vendor.city}",
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentSecondary
                        )
                    }

                    if (vendor.category.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val iconRes = getIconResId(vendor.categoryIconRes)
                            Icon(
                                painter = painterResource(iconRes),
                                contentDescription = "Vendor Category",
                                modifier = Modifier.size(16.dp),
                                tint = ContentSecondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = vendor.category,
                                style = JasnifyTheme.typography.labelMedium,
                                color = ContentSecondary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                DashedDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outline.copy(0.16f)
                )
                Spacer(Modifier.height(8.dp))

                Column {
                    Text(
                        text = "Starting at",
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = vendor.priceStartsFrom,
                            style = JasnifyTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                            color = ContentBrandDark
                        )
                        if (vendor.priceUnit.isNotBlank()) {
                            Text(
                                text = "/ ${vendor.priceUnit}",
                                style = JasnifyTheme.typography.labelMedium,
                                color = ContentSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VendorCardCompact(
    vendor: Vendor,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onCardClick: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onRemoveClick: (() -> Unit)? = null,
    showLikeButton: Boolean = true,
    onOfferClick: () -> Unit = {},
    compactCardSize: CompactCardSize = CompactCardSize.MEDIUM,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    if (isLoading) {
        CompactCardLoading(cardSize = compactCardSize)
        return
    }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isMedium = compactCardSize == CompactCardSize.MEDIUM

    val cardWidth = if (isMedium) screenWidth * 0.43f else screenWidth * 0.38f

    val actualPageCount = vendor.images.size
    val virtualCount = if (actualPageCount > 1) VIRTUAL_PAGE_COUNT else actualPageCount
    val initialPage =
        if (actualPageCount > 1) (VIRTUAL_PAGE_COUNT / 2) - ((VIRTUAL_PAGE_COUNT / 2) % actualPageCount) else 0

    val pagerState = if (isMedium) {
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { virtualCount }
        )
    } else null

    if (isMedium && actualPageCount > 1 && pagerState != null) {
        LaunchedEffect(pagerState) {
            while (true) {
                delay(3000.milliseconds)
                if (!pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }

    val containerColor = Color.Transparent

    Card(
        onClick = onCardClick,
        modifier = modifier
            .width(cardWidth)
            .wrapContentHeight()
            .clip(SquircleShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            val sharedBoundsModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    Modifier
                        .fillMaxWidth()
                        .height(cardWidth)
                        .clip(SquircleShape(20.dp))
                        .sharedElement(
                            rememberSharedContentState(key = "vendor_image_${vendor.name}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                }
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(cardWidth)
                    .clip(SquircleShape(20.dp))
            }

            Box(
                modifier = sharedBoundsModifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        shape = SquircleShape(20.dp)
                    ),
            ) {
                if (isMedium && pagerState != null) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = false
                    ) { page ->
                        val actualIndex = if (actualPageCount > 0) page % actualPageCount else 0
                        VendorImage(
                            url = vendor.images.getOrNull(actualIndex) ?: "",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    VendorImage(
                        url = vendor.images.firstOrNull() ?: "",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Surface(
                    color = SurfacePrimary.copy(alpha = 0.8f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.ic_star), null, Modifier.size(12.dp), ContentPrimary)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${vendor.rating}",
                            color = ContentPrimary,
                            style = JasnifyTheme.typography.labelSmall
                        )
                    }
                }

                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(CircleShape)
                        .clickable { if (onRemoveClick != null) onRemoveClick() else if (showLikeButton) onFavoriteToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    if (onRemoveClick != null) {
                        Surface(
                            color = Color.Black,
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_cross),
                                contentDescription = "Remove Icon",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else if (showLikeButton) {
                        val iconRes =
                            if (vendor.favorite) painterResource(R.drawable.ic_heart_filled) else painterResource(
                                R.drawable.ic_heart
                            )
                        Icon(
                            painter = iconRes,
                            contentDescription = "Favorite Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                if (isMedium && pagerState != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OfferBadge(onClick = onOfferClick)
                        CarouselDots(
                            pageCount = actualPageCount,
                            currentPage = if (actualPageCount > 0) pagerState.currentPage % actualPageCount else 0
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(
                    if (isMedium) PaddingValues(12.dp, 8.dp, 12.dp, 12.dp)
                    else PaddingValues(8.dp)
                )
            ) {
                Text(
                    text = vendor.name,
                    style = if (isMedium) JasnifyTheme.typography.headingMedium else JasnifyTheme.typography.bodyLarge,
                    color = ContentPrimary,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${vendor.locality}, ${vendor.city}",
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isMedium) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Starting at",
                        style = JasnifyTheme.typography.labelSmall,
                        color = ContentSecondary
                    )
                    Text(
                        text = vendor.priceStartsFrom,
                        style = JasnifyTheme.typography.displaySmall,
                        color = ContentPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun VendorCard(
    vendor: Vendor,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onOfferClick: () -> Unit = {}
) {
    VendorCardFull(
        vendor = vendor,
        modifier = modifier,
        onCardClick = onCardClick,
        onFavoriteToggle = onFavoriteToggle,
        onOfferClick = onOfferClick
    )
}

@Composable
private fun VendorImage(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Vendor Image",
        modifier = modifier,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.img_placeholder_venue_vendor),
        error = painterResource(id = R.drawable.img_placeholder_venue_vendor)
    )
}

@Composable
private fun OfferBadge(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = ContentPrimary.copy(alpha = 0.5f),
        shape = SquircleShape(100, 0f),
        modifier = modifier.height(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalOffer,
                contentDescription = "Offer Icon",
                Modifier
                    .size(18.dp)
                    .graphicsLayer { scaleX = -1f },
                tint = ContentInvPrimary
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Offers",
                color = ContentInvPrimary,
                style = JasnifyTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun VendorBannerRow(
    vendor: Vendor,
    modifier: Modifier = Modifier
) {
    val hasEnquiries = vendor.enquiriesLastMonth > 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.BottomStart
    ) {
        if (hasEnquiries) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFEAC768),
                                Color(0xFFCCB065),
                            )
                        )
                    )
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trend_up),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF6D5410),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${vendor.enquiriesLastMonth} Enquiries last month",
                    style = JasnifyTheme.typography.labelMedium,
                    color = Color(0xFF6D5410)
                )
            }
        }

        Box(
            modifier = Modifier
                .width(101.5.dp)
                .height(34.dp)
                .drawBehind {
                    val bleedY = size.height + 1.5f

                    val path = Path().apply {
                        moveTo(0f, bleedY)
                        lineTo(0f, 0f)

                        val startCurveX = size.width * 0.45f
                        lineTo(startCurveX, 0f)

                        cubicTo(
                            x1 = startCurveX + (size.width * 0.35f), y1 = 0f,
                            x2 = startCurveX + (size.width * 0.20f), y2 = size.height,
                            x3 = size.width, y3 = size.height
                        )

                        lineTo(0f, bleedY)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = SurfacePrimary
                    )
                }
                .padding(start = 12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Row(
                modifier = Modifier
                    .background(Color(0xFF009B0A), shape = RoundedCornerShape(100))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_star), null, Modifier.size(12.dp), Color.White)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${vendor.rating}",
                    color = ContentInvPrimary,
                    style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}


@Composable
private fun CarouselDots(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = if (index == currentPage) ContentBrandDark else SurfacePrimary,
                        shape = CircleShape
                    )
            )
        }
    }
}

fun Modifier.vendorShadow(
    borderRadius: Dp = 24.dp,
    color: Color = Color.Black
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint()

        val layers = listOf(
            VendorShadowLayer(offsetY = 8.dp, blur = 16.dp, alpha = 0.06f),
            VendorShadowLayer(offsetY = 24.dp, blur = 28.dp, alpha = 0.04f),
            VendorShadowLayer(offsetY = 48.dp, blur = 40.dp, alpha = 0.025f),
            VendorShadowLayer(offsetY = 80.dp, blur = 48.dp, alpha = 0.01f)
        )

        layers.forEach { layer ->
            paint.color = color.copy(alpha = layer.alpha).toArgb()
            paint.setShadowLayer(
                layer.blur.toPx(),
                0f,
                layer.offsetY.toPx(),
                color.copy(alpha = layer.alpha).toArgb()
            )

            canvas.nativeCanvas.drawRoundRect(
                0f,
                0f,
                size.width,
                size.height,
                borderRadius.toPx(),
                borderRadius.toPx(),
                paint
            )
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
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


@Preview(showBackground = true)
@Composable
fun PreviewVendorCards() {
    val sampleVendor = Vendor(
        name = "Royal Starlight Photography",
        category = "Photographer",
        city = "Noida",
        locality = "Sector 62",
        rating = 4.8,
        totalReviews = "1.8k",
        priceStartsFrom = "₹45,000",
        priceUnit = "day",
        images = listOf(
            "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
            "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800"
        ),
        favorite = true,
        enquiriesLastMonth = 18
    )

    JasnifyTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .background(Color(0xFFF9F9F9))
        ) {
            VendorCardFull(vendor = sampleVendor)
            Spacer(Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                VendorCardCompact(vendor = sampleVendor, compactCardSize = CompactCardSize.MEDIUM)
                Spacer(Modifier.width(12.dp))
                VendorCardCompact(vendor = sampleVendor, compactCardSize = CompactCardSize.SMALL)
            }
        }
    }
}