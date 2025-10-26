package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.FavoriteButton
import com.harshdeep.jasnify.theme.BackgroundSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import kotlinx.coroutines.delay
import sv.lib.squircleshape.SquircleShape


// ---- Vendor Data class (Kept as is) --------
data class VendorCardData(
    val vendorName: String,
    val location: String,
    val rating: Double,
    val totalReviews: Int,
    val services: List<String>,
    val priceStartsFrom: String,
    val images: List<String> = listOf("", "", "", ""),
    val imageUrl: String? = null
)


// -----------------------------------------------------------------------------
// 1. FULL Vendor Card Composable
// -----------------------------------------------------------------------------
@Composable
fun VendorCardFull(
    vendor: VendorCardData,
    modifier: Modifier = Modifier,
    onMoreDetailsClick: () -> Unit,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val imageCount = vendor.images.size
    val pagerState = rememberPagerState(pageCount = { imageCount }, initialPage = 0)
    val autoScrollDuration = 3000L

    // Automatic scrolling effect
    if (imageCount > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(autoScrollDuration)
                val nextPage = (pagerState.currentPage + 1) % imageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp), // FULL style height
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // 1. Image Carousel Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // FULL style image height
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    ImagePlaceholder(
                        modifier = Modifier
                            .background(BackgroundSecondary)
                            .fillMaxSize()
                    )
                }

                FavoriteButton(
                    onFavoriteToggle = onFavoriteToggle,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
                    isFavorite = false
                )

                // The Rating Chip is HIDDEN in FULL style (based on original logic swap)

                // Carousel Dots (Bottom Center)
                CarouselDots(
                    pageCount = imageCount,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // 2. Card Content Area
            Column(modifier = Modifier.padding(16.dp)) {

                // Vendor Name & Rating/Total Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = vendor.vendorName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Rating and Total Count Column (VISIBLE in FULL style)
                    RatingTotalCountColumn(vendor)
                }

                Spacer(Modifier.height(4.dp))

                // Location Row
                LocationRow(vendor.location)

                Spacer(Modifier.height(12.dp))

                // Service Chips (VISIBLE in FULL style)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    vendor.services.take(3).forEach { service ->
                        ServiceChip(service)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                // Price Row & More Details Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Package starts from",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        // Price text style (FULL now uses the smaller/compact price style)
                        Text(
                            text = vendor.priceStartsFrom,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // More Details Button (VISIBLE in FULL style)
                    OutlinedButton(
                        onClick = onMoreDetailsClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("More Details")
                    }
                }
            }
        }
    }
}


// -----------------------------------------------------------------------------
// 2. COMPACT Vendor Card Composable
// -----------------------------------------------------------------------------
@Composable
fun VendorCardCompact(
    vendor: VendorCardData,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val imageCount = vendor.images.size
    val pagerState = rememberPagerState(pageCount = { imageCount }, initialPage = 0)
    val autoScrollDuration = 3000L

    // Automatic scrolling effect
    if (imageCount > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(autoScrollDuration)
                val nextPage = (pagerState.currentPage + 1) % imageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Card(
        onClick = onCardClick,
        modifier = modifier
            .width(160.dp) // COMPACT style width
            .height(285.dp), // COMPACT style height
        shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // 1. Image Carousel Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // COMPACT style image height
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    ImagePlaceholder(
                        modifier = Modifier
                            .background(BackgroundSecondary)
                            .fillMaxSize()
                    )
                }

                FavoriteButton(
                    onFavoriteToggle = onFavoriteToggle,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    isFavorite = false,
                    size = 30.dp
                )

                // Rating Chip (VISIBLE in COMPACT style)
                RatingChip(
                    rating = vendor.rating.toString(),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )

                // Carousel Dots (Bottom Center)
                CarouselDots(
                    pageCount = imageCount,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // 2. Card Content Area
            Column(modifier = Modifier.padding(12.dp)) { // Slightly less padding for compact

                // Vendor Name (Compact doesn't show Rating/Total Count in this section)
                Text(
                    text = vendor.vendorName,
                    style = MaterialTheme.typography.titleSmall, // Smaller title
                    fontWeight = FontWeight.Bold,
                    maxLines = 1 // Ensure it fits
                )

                Spacer(Modifier.height(4.dp))

                // Location Row
                LocationRow(vendor.location)

                Spacer(Modifier.height(12.dp))

                // Service Chips (HIDDEN in COMPACT style)

                // Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Package starts from",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        // Price text style (COMPACT now uses the larger price style)
                        Text(
                            text = vendor.priceStartsFrom,
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp), // Slightly smaller than 32.sp for better fit in 160.dp card, but still large
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // More Details Button (HIDDEN in COMPACT style)
                }
            }
        }
    }
}


// --- Helper Composable Functions (No change) ---

@Composable
private fun ImagePlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Vendor Image Placeholder",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
    }
}


@Composable
private fun RatingChip(rating: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating Star",
            tint = Color(0xFFFFC107), // Yellow star
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = rating,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun CarouselDots(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 0 until pageCount) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (i == currentPage) Color.Black else Color.White.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun LocationRow(location: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = location,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            maxLines = 1 // Ensure it fits in compact
        )
    }
}

@Composable
private fun RatingTotalCountColumn(vendor: VendorCardData) {
    Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating Star",
                tint = Color(0xFFFFC107), // Yellow star
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${vendor.rating}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "(${vendor.totalReviews} Count)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun ServiceChip(service: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFFE8E8E8)
    ) {
        Text(
            text = service,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// --- Preview (Updated to use new composable) ---

@Preview(showBackground = true, name = "Vendor Card - Full Style")
@Composable
fun PreviewFullVendorCard() {
    val sampleVendor = VendorCardData(
        vendorName = "The Grand Venue Hall",
        location = "Greater Noida, UP",
        rating = 4.8,
        totalReviews = 256,
        services = listOf("Catering", "Decorations", "Photography"),
        priceStartsFrom = "₹50,000",
        images = listOf("1", "2", "3")
    )
    Column(modifier = Modifier.padding(16.dp)) {
        VendorCardFull(
            vendor = sampleVendor,
            onMoreDetailsClick = {},
            onCardClick = {},
            onFavoriteToggle = {}
        )
    }
}

@Preview(showBackground = true, name = "Vendor Card - Compact Style (160dp)")
@Composable
fun PreviewCompactVendorCard() {
    val sampleVendor = VendorCardData(
        vendorName = "Elegant Decorators Co.",
        location = "Sector 18, Noida",
        rating = 4.5,
        totalReviews = 112,
        services = listOf("Floral", "Lighting", "Drapery"),
        priceStartsFrom = "₹35,500",
        images = listOf("a", "b", "c", "d")
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        VendorCardCompact(
            vendor = sampleVendor,
            onCardClick = {},
            onFavoriteToggle = {}
        )
    }
}