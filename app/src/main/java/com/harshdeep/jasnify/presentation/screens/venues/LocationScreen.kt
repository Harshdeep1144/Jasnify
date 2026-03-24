package com.harshdeep.jasnify.presentation.screens.venues

import com.harshdeep.jasnify.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.FamousCityChip
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun LocationScreen(
    initialSearches: List<String>,
    onBackClick: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val recentSearches = remember { mutableStateListOf<String>().apply { addAll(initialSearches) } }
    var selectedCityId by remember { mutableStateOf("") }

    data class City<T>(
        val name: String,
        val imageRes: Int,
        val value: T
    )

    val cities = listOf(
        City("Delhi NCR", R.drawable.ic_city_del, "delhi"),
        City("Bengaluru", R.drawable.ic_city_blr, "bengaluru"),
        City("Mumbai", R.drawable.ic_city_mum, "mumbai"),
        City("Hyderabad", R.drawable.ic_city_hyd, "hyderabad"),
        City("Chennai", R.drawable.ic_city_chn, "chennai"),
        City("Jaipur", R.drawable.ic_city_jpr, "jaipur"),
        City("Agra", R.drawable.ic_city_agr, "agra"),
        City("Kolkata", R.drawable.ic_city_kol, "kolkata"),
        City("Patna", R.drawable.ic_city_ptn, "patna")
    )

    Scaffold(
        topBar = {
            // Added statusBarsPadding here so the TopBar respects the system status bar
            Column(modifier = Modifier.statusBarsPadding()) {
                if (!isSearchActive) {
                    CustomTopBar(
                        title = "Location",
                        onBackClick = onBackClick,
                        isLargeTitle = true
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                Column {
                    CustomSearchBar(
                        value = text,
                        onValueChange = { text = it },
                        onActiveChange = { isSearchActive = it }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                LocationPicker(
                    city = "Hajipur",
                    state = "Bihar",
                    onClick = {}
                )
            }

            if (recentSearches.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Recent Searches",
                            style = JasnifyTheme.typography.headingMedium,
                            fontWeight = FontWeight.Medium
                        )

                        TextButton(
                            onClick = { recentSearches.clear() },
                        ) {
                            Text(
                                text = "Clear all",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentBrandDark
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        items(recentSearches) { city ->
                            FilterChip(
                                label = city,
                                trailingIcon = Icons.Default.Close,
                                hasStroke = true,
                                shapeStyle = ChipShapeStyle.Round,
                                onTrailingIconClick = {
                                    recentSearches.remove(city)
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Popular Cities",
                style = JasnifyTheme.typography.headingMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cities.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { city ->
                            Box(modifier = Modifier.weight(1f)) {
                                FamousCityChip(
                                    cityName = city.name,
                                    cityImage = painterResource(id = city.imageRes),
                                    isSelected = selectedCityId == city.value,
                                    onClick = {
                                        selectedCityId = city.value
                                    }
                                )
                            }
                        }

                        // Fill empty slots if the last row has fewer than 3 items
                        if (rowItems.size < 3) {
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPicker(
    city: String,
    state: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = ContentBrandDark,
                shape = SquircleShape(20.dp, CornerSmoothingDefault)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceBrandSecondary)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.MyLocation,
            contentDescription = "Location Icon",
            tint = ContentBrandDark,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Use Current Location",
                color = ContentBrandDark,
                style = JasnifyTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$city, $state",
                color = ContentBrandDark,
                style = JasnifyTheme.typography.labelMedium,
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = ContentBrandDark,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    JasnifyTheme {
        LocationScreen(
            initialSearches = listOf("Patna", "New Delhi", "Mumbai", "Pune", "Goa"),
            onBackClick = {}
        )
    }
}