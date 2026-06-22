package com.harshdeep.jasnify.presentation.components.filter

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.CustomRadioButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.others.CustomCheckbox
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.others.SearchBarType
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SortFilterBottomSheet(
    sheetState: SheetState,
    sortOptions: List<String>,
    initialSortOption: String,
    filterByOptions: List<String>,
    initialFilterOptions: Set<String>,
    onDismiss: () -> Unit,
    onApply: (String, Set<String>) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfacePrimary,
        scrimColor = Color.Black.copy(alpha = 0.8f),
        dragHandle = null,
        shape = SquircleShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        SortFilterBottomSheetContent(
            sortOptions = sortOptions,
            initialSortOption = initialSortOption,
            filterByOptions = filterByOptions,
            initialFilterOptions = initialFilterOptions,
            onDismiss = onDismiss,
            onApply = onApply
        )
    }
}

/**
 * Separated content representation of the Filter sheet.
 * Promotes ease of previews and clean state isolation.
 */
@Composable
fun SortFilterBottomSheetContent(
    sortOptions: List<String>,
    initialSortOption: String,
    filterByOptions: List<String>,
    initialFilterOptions: Set<String>,
    onDismiss: () -> Unit,
    onApply: (String, Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) }
    var filterSearchText by remember { mutableStateOf("") }

    var tempSortOption by remember(initialSortOption) { mutableStateOf(initialSortOption) }
    var tempFilterOptions by remember(initialFilterOptions) { mutableStateOf(initialFilterOptions) }

    val hasSelectedFilters = tempFilterOptions.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Tab Header System with Adaptive Width & Smooth Slider Animation
            val horizontalPadding = 12.dp
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                // Dynamically calculate the track size and width of a single tab
                val totalWidth = maxWidth - (horizontalPadding * 2)
                val tabWidth = totalWidth / 2

                // Smooth offset physics using Spring spec
                val indicatorOffset by animateDpAsState(
                    targetValue = if (activeTab == 0) 0.dp else tabWidth,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "TabIndicatorOffset"
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Sort by", "Filter by").forEachIndexed { index, title ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { activeTab = index }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = JasnifyTheme.typography.headingMedium,
                                    color = if (activeTab == index) ContentPrimary else ContentTertiary
                                )
                            }
                        }
                    }

                    // Sliding Indicator Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding)
                            .height(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(tabWidth)
                                .fillMaxHeight()
                                .offset(x = indicatorOffset)
                                .padding(horizontal = 12.dp) // Keeps original margins within the tab column
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(ContentBrandDark)
                        )
                    }
                }
            }

            // Integrates CustomSearchBar seamlessly below tabs for Filtering
            if (activeTab == 1) {
                CustomSearchBar(
                    value = filterSearchText,
                    placeholder = "Search",
                    onValueChange = { filterSearchText = it },
                    type = SearchBarType.DEFAULT,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Selection Options Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 320.dp, max = 450.dp),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                if (activeTab == 0) {
                    items(sortOptions) { option ->
                        val isSelected = tempSortOption == option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .clip(SquircleShape(16.dp))
                                .background(if (isSelected) SurfaceBrandSecondary else Color.Transparent)
                                .clickable { tempSortOption = option }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomRadioButton(
                                selected = isSelected,
                                onClick = { tempSortOption = option }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = option,
                                style = JasnifyTheme.typography.headingLarge,
                                color = if (isSelected) ContentBrandDark else ContentSecondary,
                            )
                        }
                    }
                } else {
                    val dynamicFilterOptions = if (filterSearchText.isBlank()) {
                        filterByOptions
                    } else {
                        filterByOptions.filter { it.contains(filterSearchText, ignoreCase = true) }
                    }

                    items(dynamicFilterOptions) { option ->
                        val isSelected = tempFilterOptions.contains(option)
                        CustomCheckbox(
                            text = option,
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                tempFilterOptions = if (checked) {
                                    tempFilterOptions + option
                                } else {
                                    tempFilterOptions - option
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }

        // Pinned Action Buttons Footer
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(SurfacePrimary),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.background(SurfacePrimary)
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextButton(
                        onClick = {
                            if (hasSelectedFilters) {
                                tempFilterOptions = emptySet()
                            } else {
                                onDismiss()
                            }
                        },
                        text = if (hasSelectedFilters) "Clear" else "Cancel",
                        modifier = Modifier
                            .weight(1f),
                        type = ButtonType.Tertiary,
                        shapeStyle = ButtonShapeStyle.Square
                    )

                    CustomTextButton(
                        onClick = { onApply(tempSortOption, tempFilterOptions) },
                        text = "Apply",
                        modifier = Modifier
                            .weight(1.2f),
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Square
                    )
                }
            }
        }
    }
}

// ---------- Full-featured Design System Preview ----------------

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun FilterBottomSheetPreview() {
    JasnifyTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E1E1E))
                .padding(top = 80.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF7F8F7),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                SortFilterBottomSheetContent(
                    sortOptions = listOf(
                        "Newest First (Default)",
                        "Oldest First",
                        "Highest to Lowest Amount",
                        "Lowest to Highest Amount"
                    ),
                    initialSortOption = "Newest First (Default)",
                    filterByOptions = listOf(
                        "Venue",
                        "Catering",
                        "Gifts",
                        "Staff & Crew",
                        "Costumes",
                        "Vendors",
                        "Transportation",
                        "Entertainment"
                    ),
                    initialFilterOptions = setOf("Venue", "Gifts"),
                    onDismiss = {},
                    onApply = { _, _ -> }
                )
            }
        }
    }
}