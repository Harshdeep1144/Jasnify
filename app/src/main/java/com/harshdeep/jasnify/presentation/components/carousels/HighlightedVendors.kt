package com.harshdeep.jasnify.presentation.components.carousels

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.CompactCardSize
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.states.CompactCardLoading
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
fun HighlightedVendors(
    title: String,
    subtitle: String,
    vendors: List<Vendor>,
    modifier: Modifier = Modifier,
    isHeadingTop: Boolean = false,
    headerImage: Painter? = null,
    isLoading: Boolean = false,
    onVendorClick: (Vendor) -> Unit = {},
    onFavoriteToggle: (Vendor) -> Unit = {},
    onOfferClick: (Vendor) -> Unit = {},
    cardSize: CompactCardSize = CompactCardSize.SMALL,
    buttonText: String? = "View all",
    buttonTrailingIcon: Painter? = null,
    onButtonClick: (() -> Unit)? = null,
    backgroundColor: Color = Color(0xFFD9E9FF),
    titleColor: Color = Color(0xFF003680),
    subtitleColor: Color = Color(0xFF003680)
) {
    val shimmerBrush = if (isLoading) shimmerBrush() else null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(0.16f),
                SquircleShape(CornerExtraLarge, CornerSmoothingDefault)
            )
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (isLoading && shimmerBrush != null) {
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerBrush)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(160.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmerBrush)
                        )
                    } else {
                        val titleComposable = @Composable {
                            Text(
                                text = title.uppercase(),
                                style = JasnifyTheme.typography.displayLarge.copy(
                                    fontFamily = FontFamily(Font(R.font.facadflux_bold)),
                                    lineHeight = JasnifyTheme.typography.displayLarge.fontSize
                                ),
                                fontWeight = FontWeight.Bold,
                                color = titleColor,
                            )
                        }

                        val subtitleComposable = @Composable {
                            Text(
                                text = subtitle.uppercase(),
                                style = JasnifyTheme.typography.labelSmall.copy(
                                    lineHeight = JasnifyTheme.typography.labelSmall.fontSize
                                ),
                                color = subtitleColor,
                                letterSpacing = 2.sp
                            )
                        }

                        if (isHeadingTop) {
                            titleComposable()
                            subtitleComposable()
                        } else {
                            subtitleComposable()
                            titleComposable()
                        }
                    }
                }

                if (headerImage != null) {
                    Image(
                        painter = headerImage,
                        contentDescription = null,
                        modifier = Modifier.height(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Carousel List
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = !isLoading
            ) {
                if (isLoading && shimmerBrush != null) {
                    items(
                        count = 4,
                        contentType = { "loading_card" }
                    ) {
                        CompactCardLoading(
                            cardSize = cardSize,
                            shimmerBrush = shimmerBrush
                        )
                    }
                } else {
                    items(
                        items = vendors,
                        key = { it.id },
                        contentType = { "vendor_card" }
                    ) { vendor ->
                        VendorCardCompact(
                            vendor = vendor,
                            onCardClick = { onVendorClick(vendor) },
                            onFavoriteToggle = { onFavoriteToggle(vendor) },
                            onOfferClick = { onOfferClick(vendor) },
                            compactCardSize = cardSize,
                            vendorNameColor = titleColor,
                            isVendorNameBold = true,
                            locationColor = ContentPrimary,
                        )
                    }
                }
            }

            // Optional Bottom Action Button
            if (!isLoading && onButtonClick != null && !buttonText.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextButton(
                    onClick = onButtonClick,
                    text = buttonText,
                    trailingIcon = buttonTrailingIcon,
                    shapeStyle = ButtonShapeStyle.Round,
                    containerColor = SurfacePrimary,
                    contentColor = ContentPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Preview(name = "Highlighted Vendors - Heading Down (Default)", showBackground = true)
@Composable
private fun HighlightedVendorsHeadingDownPreview() {
    val sampleVendors = listOf(
        Vendor(id = "1", name = "Vivid Visions", city = "New Delhi"),
        Vendor(id = "2", name = "Pixela Photogra...", city = "Noida"),
        Vendor(id = "3", name = "Frames & Focus", city = "Gurgaon")
    )

    HighlightedVendors(
        title = "Photographers",
        subtitle = "Top-Rated",
        vendors = sampleVendors,
        isHeadingTop = false,
        isLoading = false,
        buttonText = null,
        onButtonClick = null,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Highlighted Vendors - Heading Top", showBackground = true)
@Composable
private fun HighlightedVendorsHeadingTopPreview() {
    val sampleVendors = listOf(
        Vendor(id = "1", name = "Unique Catering...", city = "Noida"),
        Vendor(id = "2", name = "Terminal Cateri...", city = "New Delhi"),
        Vendor(id = "3", name = "Royal Caterers", city = "Ghaziabad")
    )

    HighlightedVendors(
        title = "Top Vendors",
        subtitle = "Curated for you",
        vendors = sampleVendors,
        isHeadingTop = true,
        isLoading = false,
        buttonText = "View all",
        buttonTrailingIcon = painterResource(R.drawable.ic_arrow_right),
        onButtonClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "Highlighted Vendors - Loading State", showBackground = true)
@Composable
private fun HighlightedVendorsLoadingPreview() {
    HighlightedVendors(
        title = "Top Vendors",
        subtitle = "Curated for you",
        vendors = emptyList(),
        isLoading = true,
        buttonText = "View all",
        onButtonClick = {},
        modifier = Modifier.padding(16.dp)
    )
}