package com.harshdeep.jasnify.presentation.components.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.screens.venues.MerchantReplyData
import com.harshdeep.jasnify.presentation.screens.venues.RatingBreakdownItemData
import com.harshdeep.jasnify.presentation.screens.venues.VenueReviewItem
import com.harshdeep.jasnify.presentation.screens.venues.VenueReviewsData
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun ReviewsSection(
    vendor: VendorCardData,
    reviewsData: VenueReviewsData,
    onSeeAllClick: () -> Unit,
    onReviewCardClick: (VenueReviewItem) -> Unit,
    onWriteReviewClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header Section
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
                modifier = Modifier.clickable { onSeeAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See more", // Updated to match image mockup
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

        // Rating Breakdown Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RatingSurface(rating = vendor.rating.toString())
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${vendor.totalReviews} ratings",
                    style = JasnifyTheme.typography.labelSmall,
                    color = ContentSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            reviewsData.ratingBreakdown.forEach { item ->
                VerticalDivider(modifier = Modifier.height(32.dp), thickness = 1.dp, color = ContentTertiary)
                RatingBreakdownItem(item.score, item.label)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Reviews List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviewsData.reviews) { review ->
                ReviewCard(
                    review = review,
                    onCardClick = { onReviewCardClick(review) } // Fixed parameter
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Been there? Tell us how it was!" Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ContentSecondary),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Been there? Tell us how it was!",
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) { index ->
                        val ratingValue = index + 1
                        Icon(
                            imageVector = Icons.Rounded.StarBorder,
                            contentDescription = "Rate $ratingValue stars",
                            tint = ContentSecondary,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onWriteReviewClick(ratingValue) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AllReviewsScreen(
    title: String,
    reviewsData: VenueReviewsData,
    ratingValue: String,
    onBack: () -> Unit,
    onOpenReviewPost: (VenueReviewItem) -> Unit,
    onLeaveReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableStateOf(0) }
    val filters = remember {
        listOf("Relevance", "Recent First", "Negative First", "With Photos", "Highest Rated", "Lowest Rated")
    }

    Surface(
        color = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                CustomTopBar(
                    title = title,
                    backIcon = TopIcon.Predefined.BACK_2,
                    onBackClick = onBack,
                    isLeftAligned = true,
                    buttonStyle = ButtonBackground.TRANSPARENT
                )
                Spacer(Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        RatingDistributionSummaryBlock(
                            ratingValue = ratingValue,
                            totalRatings = reviewsData.totalRatingsCount,
                            distribution = reviewsData.distribution
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            reviewsData.subMetrics.forEachIndexed { index, item ->
                                if (index > 0) {
                                    VerticalDivider(
                                        modifier = Modifier.height(32.dp),
                                        thickness = 1.dp,
                                        color = ContentTertiary
                                    )
                                }
                                RatingBreakdownItem(item.score, item.label)
                            }
                        }
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            itemsIndexed(filters) { index, filter ->
                                val isSelected = selectedFilterIndex == index
                                FilterChip(
                                    label = filter,
                                    isSelected = isSelected,
                                    hasStroke = true,
                                    onClick = { selectedFilterIndex = index }
                                )
                            }
                        }
                    }

                    items(reviewsData.reviews) { review ->
                        ReviewCard(
                            review = review,
                            onCardClick = { onOpenReviewPost(review) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            // Floating Bottom Action Button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextButton(
                        onClick = onLeaveReview,
                        text = "Leave a review",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
fun ReviewDetailPostScreen(
    review: VenueReviewItem,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            CustomTopBar(
                title = "Post",
                backIcon = TopIcon.Predefined.BACK_2,
                isLeftAligned = true,
                onBackClick = onBack,
                buttonStyle = ButtonBackground.TRANSPARENT
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceBrandSecondary)
                            ) {
                                AsyncImage(
                                    model = review.userAvatarUrl ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100",
                                    contentDescription = "User Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = review.userName,
                                        style = JasnifyTheme.typography.labelLarge,
                                        color = ContentPrimary
                                    )
                                    if (review.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            painter = painterResource(R.drawable.ic_tick),
                                            contentDescription = "Verified Profile",
                                            modifier = Modifier.size(14.dp),
                                            tint = ContentPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = review.relativeTime,
                                    style = JasnifyTheme.typography.labelMedium,
                                    color = ContentSecondary
                                )
                            }
                        }

                        RatingSurface(
                            rating = review.rating.toString(),
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Simulating the data coming from database
                        val formattedAboutText = review.reviewText.replace(". ", ".\n\n")

                        Text(
                            text = formattedAboutText,
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentSecondary
                        )
                    }
                }

                if (review.attachedImages.isNotEmpty()) {
                    item {
                        AttachedImagesPreviewRow(images = review.attachedImages,
                            modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }

                item {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.16f))
                }

                item {
                    val reply = review.merchantReply ?: MerchantReplyData(
                        merchantName = "Hotel Imperial Inn",
                        relativeTime = "1 week ago",
                        replyText = "Thank you for your valuable feedback!"
                    )

                    Surface(
                        color = SurfaceSecondary,
                        shape = RoundedCornerShape(CornerLargeIncrease),
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceBrandSecondary)
                                ) {
                                    AsyncImage(
                                        model = reply.merchantAvatarUrl ?: "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100",
                                        contentDescription = "Merchant Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = reply.merchantName,
                                            style = JasnifyTheme.typography.labelLarge,
                                            color = ContentPrimary
                                        )
                                        if (reply.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                painter = painterResource(R.drawable.ic_tick),
                                                contentDescription = "Verified Profile",
                                                modifier = Modifier.size(14.dp),
                                                tint = ContentPrimary
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = reply.relativeTime,
                                        style = JasnifyTheme.typography.labelMedium,
                                        color = ContentSecondary
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = reply.replyText,
                                style = JasnifyTheme.typography.labelLarge,
                                color = ContentSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun ReviewCard(
    review: VenueReviewItem,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceSecondary,
        shape = SquircleShape(CornerExtraLarge),
        modifier = modifier
            .width(280.dp)
            .clickable(onClick = onCardClick)
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = review.userName,
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.relativeTime,
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentSecondary
                        )
                    }
                }

                RatingSurface(rating = review.rating.toString())
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.reviewText,
                style = JasnifyTheme.typography.labelLarge,
                color = ContentSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.clickable { onCardClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "More",
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
            Spacer(modifier = Modifier.height(12.dp))

            if (review.attachedImages.isNotEmpty()) {
                AttachedImagesPreviewRow(images = review.attachedImages)
            }
        }
    }
}


// ================================================= Helpers =================================================


@Composable
fun RatingSurface(
    rating: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF009B0A),
    contentColor: Color = ContentInvPrimary,
    starIconSize: Dp = 12.dp,
    shape: Shape = RoundedCornerShape(100),
    paddingValues: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    textStyle: TextStyle = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
) {
    Surface(
        color = backgroundColor,
        shape = shape,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier.size(starIconSize),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = rating,
                color = contentColor,
                style = textStyle
            )
        }
    }
}


@Composable
fun RatingDistributionSummaryBlock(
    ratingValue: String,
    totalRatings: String,
    distribution: List<Float>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            RatingSurface(
                rating = ratingValue,
                textStyle = JasnifyTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = ContentInvPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Based on\n$totalRatings ratings",
                style = JasnifyTheme.typography.labelMedium,
                color = ContentSecondary,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .weight(1.3f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val starLevels = listOf("5", "4", "3", "2", "1")
            starLevels.forEachIndexed { idx, label ->
                val progress = distribution.getOrNull(idx) ?: 0.0f
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(32.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = label,
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentBrand
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = ContentBrand
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(100)),
                        color = ContentBrandDark,
                        trackColor = SurfaceBrandSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AttachedImagesPreviewRow(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val maxVisible = 3
        val displayImages = images.take(maxVisible)
        val overflowCount = images.size - maxVisible

        displayImages.forEachIndexed { index, url ->
            val isLastIndex = index == maxVisible - 1 && overflowCount > 0
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(CornerLarge))
                    .background(SurfaceBrandSecondary)
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "Review Attached Image $index",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (isLastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ContentPrimary.copy(alpha = 0.75f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$overflowCount",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentInvPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBreakdownItem(rating: String, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(rating, style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), color = ContentSecondary)
        Text(label, style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
    } 
}


// ==========================================
// MOCK DATA FOR PREVIEWS
// ==========================================

private val mockVendorData = VendorCardData(
    vendorName = "Emerald Garden",
    location = "Ashiana Nagar, Patna",
    vendorType = null,
    rating = 4.4,
    totalReviews = "760",
    services = listOf("Garden Venue", "Decor", "Parking"),
    priceStartsFrom = "₹3,499",
    images = listOf(
        "https://picsum.photos/800/400?random=43",
        "https://picsum.photos/800/400?random=44",
        "https://picsum.photos/800/400?random=45"
    )
)

private val mockMerchantReply = MerchantReplyData(
    merchantName = "Hotel Imperial Inn",
    relativeTime = "3 days ago",
    replyText = "Thank you Harsh Deep for your kind words! We hope to welcome you back soon.",
    merchantAvatarUrl = null,
    isVerified = true
)

private val mockReviewItems = listOf(
    VenueReviewItem(
        userName = "Harsh Deep",
        userAvatarUrl = null,
        isVerified = true,
        relativeTime = "1 week ago",
        rating = 5.0,
        reviewText = "Amazing stay! The rooms were exceptionally clean and the service was top notch. Highly recommended for business trips.",
        attachedImages = listOf("url1", "url2", "url3", "url4"),
        merchantReply = mockMerchantReply
    ),
    VenueReviewItem(
        userName = "Rahul Sharma",
        userAvatarUrl = null,
        isVerified = false,
        relativeTime = "2 weeks ago",
        rating = 4.0,
        reviewText = "Good location and cooperative staff. Food quality could be slightly improved, but overall a pleasant experience.",
        attachedImages = emptyList(),
        merchantReply = null
    ),
    VenueReviewItem(
        userName = "Rahul Sharma",
        userAvatarUrl = null,
        isVerified = false,
        relativeTime = "2 weeks ago",
        rating = 4.0,
        reviewText = "Good location and cooperative staff. Food quality could be slightly improved, but overall a pleasant experience.",
        attachedImages = emptyList(),
        merchantReply = null
    )
)

private val mockBreakdownMetrics = listOf(
    RatingBreakdownItemData(score = "4.6", label = "Rooms"),
    RatingBreakdownItemData(score = "4.3", label = "Service"),
    RatingBreakdownItemData(score = "4.8", label = "Location"),
    RatingBreakdownItemData(score = "4.1", label = "Food")
)

private val mockVenueReviewsData = VenueReviewsData(
    reviews = mockReviewItems,
    ratingBreakdown = mockBreakdownMetrics.take(3), // Main section layout usage
    totalRatingsCount = "1,240",
    distribution = listOf(0.7f, 0.15f, 0.08f, 0.05f, 0.02f), // 5, 4, 3, 2, 1 stars percentages
    subMetrics = mockBreakdownMetrics
)

// ==========================================
// COMPOSABLE PREVIEWS
// ==========================================

@Preview(showBackground = true, name = "Rating Chips")
@Composable
fun RatingSurfacePreview() {
    JasnifyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RatingSurface(rating = "4.5")
            RatingSurface(rating = "5.0", backgroundColor = Color(0xFFE65100))
        }
    }
}

@Preview(showBackground = true, name = "Individual Review Card (Horizontal)")
@Composable
fun ReviewCardPreview() {
    JasnifyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReviewCard(
                review = mockReviewItems.first(),
                onCardClick = {}
            )
        }
    }
}


@Preview(showBackground = true, name = "Main Reviews Section Widget")
@Composable
fun ReviewsSectionPreview() {
    JasnifyTheme {
        ReviewsSection(
            vendor = mockVendorData,
            reviewsData = mockVenueReviewsData,
            onSeeAllClick = {},
            onReviewCardClick = {},
            onWriteReviewClick = {}
        )
    }
}

@Preview(showBackground = true, name = "All Reviews Screen Panel")
@Composable
fun AllReviewsScreenPreview() {
    JasnifyTheme {
        AllReviewsScreen(
            title = "Hotel Imperial Inn",
            reviewsData = mockVenueReviewsData,
            ratingValue = "4.5",
            onBack = {},
            onOpenReviewPost = {},
            onLeaveReview = {}
        )
    }
}

@Preview(showBackground = true, name = "Detailed Review Thread Screen")
@Composable
fun ReviewDetailPostScreenPreview() {
    JasnifyTheme {
        ReviewDetailPostScreen(
            review = mockReviewItems.first(),
            onBack = {}
        )
    }
}