package com.harshdeep.jasnify.presentation.screens.venues

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.cards.VendorCardCompact
import com.harshdeep.jasnify.presentation.components.cards.VendorCardData
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun VenueDetailScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pricings", "Highlights", "About", "Ask AI")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SurfacePrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                VenueHeader(onBackClick = onBackClick)
            }

            item {
                VenueInfoSection()
            }

            item {
                SuggestionChipsSection()
            }

            item {
                VenueTabs(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            }

            item {
                when (selectedTabIndex) {
                    0 -> PricingsSection()
                    1 -> HighlightsSection()
                    2 -> AboutSection()
                    3 -> AskAISection()
                }
            }
            
            item {
                Spacer(Modifier.height(24.dp))
                DashedDivider(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(24.dp))
            }

            item {
                HighlightsSection()
            }

            item {
                Spacer(Modifier.height(24.dp))
                DashedDivider(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(24.dp))
            }

            item {
                AboutSection()
            }

            item {
                Spacer(Modifier.height(24.dp))
                DashedDivider(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(24.dp))
            }

            item {
                AskAISection()
            }

            item {
                Spacer(Modifier.height(24.dp))
                DashedDivider(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(24.dp))
            }

            item {
                ReviewsSection()
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
}

@Composable
fun VenueHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800",
            contentDescription = "Venue Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.img_onboarding_1)
        )

        // Overlay Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderIconButton(
                icon = Icons.Default.KeyboardArrowDown,
                onClick = onBackClick
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HeaderIconButton(icon = Icons.Default.FavoriteBorder)
                HeaderIconButton(icon = Icons.Default.Share)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            HeaderIconButton(icon = Icons.Default.VolumeOff)
            
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable { }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Image, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("Gallery", color = Color.White, style = JasnifyTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun HeaderIconButton(
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = Color.Black.copy(alpha = 0.4f),
        shape = CircleShape,
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun VenueInfoSection() {
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
                text = "Hotel Imperial Inn",
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
                            "4.4",
                            color = Color.White,
                            style = JasnifyTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Text(
                    "1.4k",
                    style = JasnifyTheme.typography.labelSmall,
                    color = ContentSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().clickable {  },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "2nd Floor, Style Baazar, Park Street Road, Sampatchak, Patna, Bihar - 800...",
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
                painter = painterResource(R.drawable.ic_ai),
                contentDescription = null,
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
fun PricingsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PricingCard(
            title = "Non-Veg Plate",
            price = "₹3,199",
            unit = "per plate",
            iconRes = R.drawable.ic_non_veg
        )
        PricingCard(
            title = "Veg Plate",
            price = "₹2,499",
            unit = "per plate",
            iconRes = R.drawable.ic_veg
        )
        PricingCard(
            title = "Rooms",
            price = "₹2,999",
            unit = "per room",
            iconRes = R.drawable.ic_building
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
                    Text("Starting Price", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
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
fun AboutSection() {
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
            "Discover the charm of Hotel Imperial Inn, located in Sampatchak, Patna.\n\nThis inviting hotel blends comfort with elegance, maki...",
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
                    painter = painterResource(R.drawable.ic_ai),
                    contentDescription = null,
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
fun ReviewsSection() {
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
                                "4.4",
                                color = Color.White,
                                style = JasnifyTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
                Text("1.4k+ ratings", style = JasnifyTheme.typography.labelSmall, color = ContentSecondary)
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
                "Discover the charm of Hotel Imperial Inn, located in Sampatchak, Patna. This inviting...",
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
    JasnifyTheme {
        VenueDetailScreen()
    }
}
