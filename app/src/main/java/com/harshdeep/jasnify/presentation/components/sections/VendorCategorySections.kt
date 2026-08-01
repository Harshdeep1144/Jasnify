package com.harshdeep.jasnify.presentation.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.chip.VendorTypeChip
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme

data class VendorCategoryItem(val name: String, val icon: Int)

val vendorCategories = listOf(
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

@Composable
fun ExploreCategoriesHorizontal(categories: List<VendorCategoryItem>, onCategoryClick: (VendorCategoryItem) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp))
    {
        Text(text = "Explore Categories",
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
                    isLarge = false,
                    onClick = {
                        onCategoryClick(item)
                    }
                )
            }
        }
    }
}

@Composable
fun VendorCategoryGrid(categories: List<VendorCategoryItem>, onCategoryClick: (VendorCategoryItem) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val rows = categories.chunked(3)
        rows.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    VendorTypeChip(
                        label = item.name,
                        icon = item.icon,
                        isLarge = true,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onCategoryClick(item)
                        }
                    )
                }
                if (rowItems.size < 3) repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}
