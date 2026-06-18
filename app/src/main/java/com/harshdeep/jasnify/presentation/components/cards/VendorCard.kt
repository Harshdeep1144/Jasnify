package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.chip.DisabledChip
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape

// ---- Constants for Infinite Pager ----
private const val VIRTUAL_PAGE_COUNT = 10000

data class VendorCardData(
    val vendorName: String,
    val location: String,
    val vendorType: String? = null,
    val rating: Double,
    val totalReviews: String,
    val services: List<String>,
    val priceStartsFrom: String,
    val images: List<String> = listOf(),
    val enquiriesLastMonth: Int = 65,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis() // Added timestamp field with default value for ordering
)

enum class CompactCardSize {
    MEDIUM,
    SMALL
}

@Composable
fun VendorCardFull(
    vendor: VendorCardData,
    modifier: Modifier = Modifier,
    onBookCallClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onCardClick: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onOfferClick: () -> Unit = {}
) {
    val actualPageCount = vendor.images.size
    val virtualCount = if (actualPageCount > 1) VIRTUAL_PAGE_COUNT else actualPageCount
    val initialPage = if (actualPageCount > 1) (VIRTUAL_PAGE_COUNT / 2) - ((VIRTUAL_PAGE_COUNT / 2) % actualPageCount) else 0

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualCount }
    )

    if (actualPageCount > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
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
            .height(416.dp)
            .vendorShadow(borderRadius = 20.dp),
        shape = SquircleShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp)) {
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
                            .background(Color(0xFFF2F2F2))
                    )
                }

                OfferBadge(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    onClick = onOfferClick
                )

                Surface(
                    onClick = onFavoriteToggle,
                    shape = CircleShape,
                    color = if (vendor.isFavorite) Color.White else ContentPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (vendor.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Icon",
                            modifier = Modifier.size(20.dp),
                            tint = if (vendor.isFavorite) Color.Red else ContentInvPrimary
                        )
                    }
                }

                CarouselDots(
                    pageCount = actualPageCount,
                    currentPage = if (actualPageCount > 0) pagerState.currentPage % actualPageCount else 0,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                )
            }

            Column(
                modifier = Modifier.height(190.dp).padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(46.dp).padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = vendor.vendorName,
                            style = JasnifyTheme.typography.headingLarge,
                            color = ContentPrimary,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(Modifier.height(4.dp))
                        LocationAndTypeRow(vendor.location, vendor.vendorType)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(color = Color(0xFF009B0A), shape = SquircleShape(100, 0.1f)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, null, Modifier.size(14.dp), Color.White)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${vendor.rating}",
                                    color = ContentInvPrimary,
                                    style = JasnifyTheme.typography.labelMedium
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${vendor.totalReviews} reviews",
                            style = JasnifyTheme.typography.labelSmall,
                            color = ContentSecondary,
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(vendor.services) { service -> DisabledChip(label = service) }
                }

                DashedDivider(modifier = Modifier.padding(horizontal = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Starting at", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
                        Text(
                            text = vendor.priceStartsFrom,
                            style = JasnifyTheme.typography.displayMedium,
                            color = ContentPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CustomIconButton(
                            onClick = onChatClick,
                            modifier = Modifier.width(60.dp),
                            icon = painterResource(R.drawable.ic_message),
                            size = ButtonSize.Small,
                            type = ButtonType.Secondary,
                            shapeStyle = ButtonShapeStyle.Round,
                        )
                        CustomTextButton(
                            onClick = onBookCallClick,
                            text = "Book a Call",
                            size = ButtonSize.Small,
                            shapeStyle = ButtonShapeStyle.Round,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEAC768))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null, Modifier.size(18.dp),
                        tint = Color(0xFF6D5410)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${vendor.enquiriesLastMonth} Enquiries last month",
                        style = JasnifyTheme.typography.labelMedium,
                        color = Color(0xFF6D5410)
                    )
                }
            }
        }
    }
}

@Composable
fun VendorCardCompact(
    vendor: VendorCardData,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onOfferClick: () -> Unit = {},
    compactCardSize: CompactCardSize = CompactCardSize.MEDIUM,
    removeBg: Boolean = false
) {
    val actualPageCount = vendor.images.size
    val virtualCount = if (actualPageCount > 1) VIRTUAL_PAGE_COUNT else actualPageCount
    val initialPage = if (actualPageCount > 1) (VIRTUAL_PAGE_COUNT / 2) - ((VIRTUAL_PAGE_COUNT / 2) % actualPageCount) else 0

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualCount }
    )

    if (actualPageCount > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                if (!pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }

    val accentColors = listOf(CloudWhisper, SoftMint, PaleLavender, LightSkyBlue, SoftPeach)
    val randomBackgroundColor = remember { accentColors.random() }
    val isMedium = compactCardSize == CompactCardSize.MEDIUM
    val containerColor = if (isMedium && !removeBg) randomBackgroundColor else Color.Transparent

    Card(
        onClick = onCardClick,
        modifier = modifier
            .width(if (isMedium) 200.dp else 160.dp)
            .then(if (isMedium) Modifier.height(316.dp) else Modifier.wrapContentHeight())
            .clip(SquircleShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isMedium) 200.dp else 160.dp)
                    .clip(SquircleShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        shape = SquircleShape(20.dp)
                    ),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) { page ->
                    val actualIndex = if (actualPageCount > 0) page % actualPageCount else 0
                    VendorImage(url = vendor.images.getOrNull(actualIndex) ?: "", modifier = Modifier.fillMaxSize())
                }

                Surface(
                    color = SurfacePrimary.copy(alpha = 0.8f),
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, Modifier.size(12.dp), ContentPrimary)
                        Spacer(Modifier.width(4.dp))
                        Text(text = "${vendor.rating}", color = ContentPrimary, style = JasnifyTheme.typography.labelSmall)
                    }
                }

                Surface(
                    onClick = onFavoriteToggle,
                    shape = CircleShape,
                    color = if (vendor.isFavorite) Color.White else ContentPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (vendor.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Icon",
                            modifier = Modifier.size(18.dp),
                            tint = if (vendor.isFavorite) Color.Red else ContentInvPrimary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (isMedium) {
                        OfferBadge(onClick = onOfferClick)
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    CarouselDots(
                        pageCount = actualPageCount,
                        currentPage = if (actualPageCount > 0) pagerState.currentPage % actualPageCount else 0
                    )
                }
            }

            Column(
                modifier = Modifier.padding(
                    if (isMedium) PaddingValues(12.dp, 8.dp, 12.dp, 12.dp)
                    else PaddingValues(8.dp)
                )
            ) {
                Text(
                    text = vendor.vendorName,
                    style = if (isMedium) JasnifyTheme.typography.headingMedium else JasnifyTheme.typography.bodyLarge,
                    color = ContentPrimary,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))

                LocationAndTypeRow(vendor.location, vendor.vendorType, compactCardSize = compactCardSize)

                if (isMedium) {
                    Spacer(Modifier.height(8.dp))
                    Text("Starting at", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
                    Text(text = vendor.priceStartsFrom, style = JasnifyTheme.typography.displaySmall, color = ContentPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
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
        placeholder = painterResource(id = R.drawable.carousel_img1),
        error = painterResource(id = R.drawable.carousel_img1)
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
        modifier = modifier.height(26.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Outlined.LocalOffer,
                contentDescription = "Offer Icon",
                Modifier.size(18.dp).graphicsLayer { scaleX = -1f },
                tint = ContentInvPrimary)
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
private fun LocationAndTypeRow(
    location: String,
    type: String?,
    compactCardSize: CompactCardSize = CompactCardSize.MEDIUM
) {
    val isMedium = compactCardSize == CompactCardSize.MEDIUM
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isMedium) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = ContentSecondary
            )
            Spacer(Modifier.width(2.dp))
        }

        Text(
            text = location,
            style = JasnifyTheme.typography.labelMedium,
            color = ContentSecondary,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
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
                        color = if (index == currentPage) ContentBrandDark else SurfaceInvSecondary,
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
            ShadowLayer(offsetY = 11.dp, blur = 24.dp, alpha = 0.10f),
            ShadowLayer(offsetY = 43.dp, blur = 43.dp, alpha = 0.09f),
            ShadowLayer(offsetY = 97.dp, blur = 58.dp, alpha = 0.05f),
            ShadowLayer(offsetY = 172.dp, blur = 69.dp, alpha = 0.01f)
        )
        layers.forEach { layer ->
            paint.color = color.copy(alpha = layer.alpha).toArgb()
            paint.setShadowLayer(layer.blur.toPx(), 0f, layer.offsetY.toPx(), color.copy(alpha = layer.alpha).toArgb())
            canvas.nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, borderRadius.toPx(), borderRadius.toPx(), paint)
        }
    }
}

private data class ShadowLayer(val offsetY: Dp, val blur: Dp, val alpha: Float)

@Preview(showBackground = true)
@Composable
fun PreviewVendorCards() {
    val sample = VendorCardData(
        vendorName = "The Grand Palace",
        location = "Greater Noida, UP",
        rating = 4.9,
        totalReviews = "2.4k",
        services = listOf("Catering", "Decor", "Photography", "Music"),
        priceStartsFrom = "₹75,000",
        images = listOf(
            "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
            "https://images.unsplash.com/photo-1469334031218-e382a71b716b?auto=format&fit=crop&w=800",
            "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=800"
        ),
        vendorType = "Photographer",
        isFavorite = true,
        timestamp = 1718000000000L // Updated mock timestamp
    )

    JasnifyTheme {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize().background(Color(0xFFF9F9F9))) {
            VendorCardFull(vendor = sample)
            Spacer(Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                VendorCardCompact(vendor = sample)
                Spacer(Modifier.width(12.dp))
                VendorCardCompact(vendor = sample, compactCardSize = CompactCardSize.SMALL)
            }
        }
    }
}