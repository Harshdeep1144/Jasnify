package com.harshdeep.jasnify.presentation.screens.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.data.mock.MockData
import com.harshdeep.jasnify.presentation.components.chip.VendorTypeChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.OrDivider
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.sections.VendorCarousel
import com.harshdeep.jasnify.presentation.navigation.Screen
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

data class VendorCategoryItem(val name: String, val icon: Int)

@Composable
fun VendorsTab(mainNavController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }

    // Observe selected location from NavBackStackEntry's savedStateHandle
    val selectedCity by mainNavController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("selected_location", "City State")
        ?.collectAsState() ?: remember { mutableStateOf("City State") }

    val categories = listOf(
        VendorCategoryItem("Grooming", R.drawable.ill_vendor_grooming),
        VendorCategoryItem("Makeup", R.drawable.ill_vendor_makeup),
        VendorCategoryItem("Photography", R.drawable.ill_vendor_photographers),
        VendorCategoryItem("Mehendi", R.drawable.ill_vendor_mehendi),
        VendorCategoryItem("Jewellery", R.drawable.ill_vendor_jewellery),
        VendorCategoryItem("Outfits", R.drawable.ill_bride_and_groom),
        VendorCategoryItem("Entertainment", R.drawable.ill_vendor_entertainment),
        VendorCategoryItem("Food", R.drawable.ill_vendor_food),
        VendorCategoryItem("Gifts", R.drawable.ill_vendor_gifts)
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "Vendors",
                    subtitle = selectedCity,
                    onBackClick = {},
                    onMenuClick = {},
                    onDropdownClick = {
                        mainNavController.navigate(Screen.LocationSelector.route)
                    }
                )
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            item {
                CustomSearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }

            item {
                VendorCategoryGrid(categories = categories)
            }

            item {
                OrDivider(text = "EXPLORE", dividerGap = 12.dp, modifier = Modifier.padding(horizontal = 24.dp))
            }

            item {
                VendorCarousel(
                    title = "Top Makeup Artists in $selectedCity",
                    vendors = MockData.sampleMakeupArtists,
                    onSeeAllClick = {}
                )
            }

            item {
                VendorCarousel(
                    title = "Best Photographers in $selectedCity",
                    vendors = MockData.samplePhotographers,
                    onSeeAllClick = {}
                )
            }

            item {
                VendorCarousel(
                    title = "Expert Mehendi Artists in $selectedCity",
                    vendors = MockData.sampleMehendiArtists,
                    onSeeAllClick = {}
                )
            }

            item {
                DashedDivider()
                ExploreCategoriesHorizontal(categories = categories)
            }

            item {
                FooterJansify()
            }
        }
    }
}

@Composable
fun VendorCategoryGrid(categories: List<VendorCategoryItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = categories.chunked(3)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    VendorTypeChip(
                        label = item.name,
                        icon = item.icon,
                        isLarge = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreCategoriesHorizontal(categories: List<VendorCategoryItem>) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Explore Categories",
            style = JasnifyTheme.typography.headingLarge,
            fontWeight = FontWeight.Medium,
            color = ContentPrimary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { item ->
                VendorTypeChip(
                    label = item.name,
                    icon = item.icon,
                    subLabel = "Explore Now",
                    isLarge = false
                )
            }
        }
    }
}