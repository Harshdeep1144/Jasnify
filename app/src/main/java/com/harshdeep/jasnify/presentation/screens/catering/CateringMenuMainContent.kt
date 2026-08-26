package com.harshdeep.jasnify.presentation.screens.catering

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.presentation.components.buttons.AskAiButton
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.carousels.HighlightedVendors
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.chip.FoodChip
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.FooterJansify
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.presentation.components.states.SkeletonMenuCategoryCard
import com.harshdeep.jasnify.presentation.components.states.shimmerBrush
import com.harshdeep.jasnify.presentation.utils.pill360Shadow
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.BottomGradientBrush
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.SurfacePrimary

@Composable
fun CateringMenuMainContent(
    activeEvent: Event?,
    isCateringLoading: Boolean,
    allMenuItems: List<MenuItem>,
    categorizedItems: Map<String, List<MenuItem>>,
    foodCategoryVendors: List<Vendor>,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    selectedFilterTab: String,
    onFilterTabChange: (String) -> Unit,
    selectedCuisines: Set<String>,
    onCuisineChipClick: () -> Unit,
    selectedTypes: Set<String>,
    onTypeChipClick: () -> Unit,
    onResetFilters: () -> Unit,
    selectedItemIds: Set<String>,
    onItemClick: (MenuItem) -> Unit,
    onItemLongClick: (MenuItem) -> Unit,
    isMultiSelectActive: Boolean,
    onMultiSelectActiveChange: (Boolean) -> Unit,
    onDeleteSelectedClick: () -> Unit,
    mainListState: LazyListState,
    nestedScrollConnection: androidx.compose.ui.input.nestedscroll.NestedScrollConnection,
    isBottomBarVisible: Boolean,
    onAddAnItemClick: () -> Unit,
    onAiChatClick: () -> Unit,
    showAiChat: Boolean,
    isViewer: Boolean,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onVendorClick: (Vendor) -> Unit,
    onFavoriteToggle: (Vendor) -> Unit,
    onOfferClick: (Vendor) -> Unit,
    onViewAllVendorsClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val shimmer = shimmerBrush()
    val isSelectionMode = isMultiSelectActive || selectedItemIds.isNotEmpty()
    val topBarMaxScrollPx = 56.dp // Simplified, assumed constant for the content

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .nestedScroll(nestedScrollConnection),
            state = mainListState,
        ) {
            item(key = "top_bar", contentType = "top_bar") {
                AnimatedVisibility(
                    visible = !isSearchActive,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + slideInVertically(initialOffsetY = { -it / 2 }),
                    exit = fadeOut(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)) + slideOutVertically(targetOffsetY = { -it / 2 })
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .graphicsLayer {
                                val progress = if (isSearchActive) {
                                    0f
                                } else if (mainListState.firstVisibleItemIndex > 0) {
                                    1f
                                } else {
                                    (mainListState.firstVisibleItemScrollOffset / 150f).coerceIn(0f, 1f)
                                }
                                alpha = (1f - progress).coerceIn(0f, 1f)
                                translationY = -progress * 30f
                            }
                    ) {
                        CustomTopBar(
                            title = "Catering Menu",
                            onBackClick = onBackClick,
                            onMenuClick = onMenuClick,
                            menuIcon = if (isSelectionMode) TopIcon.Predefined.CLOSE else TopIcon.Predefined.MENU_VERTICAL,
                            isLargeTitle = true,
                            buttonStyle = ButtonBackground.OPAQUE
                        )
                    }
                }
            }

            stickyHeader(key = "search_and_filters_header", contentType = "header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfacePrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomSearchBar(
                            value = searchText,
                            onValueChange = onSearchTextChange,
                            onActiveChange = onSearchActiveChange,
                            placeholder = "Search Menu",
                            modifier = Modifier.weight(1f)
                        )

                        AnimatedVisibility(
                            visible = isSelectionMode && !isViewer,
                            enter = fadeIn(animationSpec = tween(200)) + expandHorizontally(expandFrom = Alignment.Start),
                            exit = fadeOut(animationSpec = tween(150)) + shrinkHorizontally(shrinkTowards = Alignment.Start)
                        ) {
                            CustomIconButton(
                                onClick = onDeleteSelectedClick,
                                icon = painterResource(R.drawable.ic_delete),
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = SurfacePrimary,
                                modifier = Modifier.width(84.dp)
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(start = 12.dp, top = 0.dp, end = 12.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item(key = "all_items_chip", contentType = "chip") {
                            FilterChip(
                                label = "All Items",
                                isSelected = selectedFilterTab == "All Items" && selectedCuisines.isEmpty() && selectedTypes.isEmpty(),
                                shapeStyle = ChipShapeStyle.Round,
                                hasStroke = true,
                                onClick = onResetFilters
                            )
                        }
                        item(key = "veg_chip", contentType = "chip") {
                            FoodChip(
                                foodType = Dietary.Veg,
                                isSelected = selectedFilterTab == "Veg",
                                shapeStyle = ChipShapeStyle.Round,
                                onClick = { onFilterTabChange("Veg") }
                            )
                        }
                        item(key = "non_veg_chip", contentType = "chip") {
                            FoodChip(
                                foodType = Dietary.NonVeg,
                                isSelected = selectedFilterTab == "Non-Veg",
                                shapeStyle = ChipShapeStyle.Round,
                                onClick = { onFilterTabChange("Non-Veg") }
                            )
                        }
                        item(key = "cuisine_chip", contentType = "chip") {
                            val cuisineLabel = if (selectedCuisines.isNotEmpty()) "Cuisine (${selectedCuisines.size})" else "Cuisine"
                            FilterChip(
                                label = cuisineLabel,
                                isSelected = selectedCuisines.isNotEmpty(),
                                shapeStyle = ChipShapeStyle.Round,
                                hasStroke = true,
                                hasDropdown = true,
                                onClick = onCuisineChipClick
                            )
                        }
                        item(key = "type_chip", contentType = "chip") {
                            val typeLabel = if (selectedTypes.isNotEmpty()) "Type (${selectedTypes.size})" else "Type"
                            FilterChip(
                                label = typeLabel,
                                isSelected = selectedTypes.isNotEmpty(),
                                shapeStyle = ChipShapeStyle.Round,
                                hasStroke = true,
                                hasDropdown = true,
                                onClick = onTypeChipClick
                            )
                        }
                    }
                }
            }

            if (isCateringLoading && categorizedItems.isEmpty()) {
                items(count = 3, contentType = { "skeleton" }) {
                    Spacer(Modifier.height(12.dp))
                    SkeletonMenuCategoryCard(brush = shimmer)
                }
            } else if (categorizedItems.isEmpty()) {
                item(key = "empty_state", contentType = "empty") {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_catering),
                                contentDescription = null,
                                tint = ContentTertiary,
                                modifier = Modifier.size(84.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (allMenuItems.isEmpty()) "Your menu will\nappear here" else "No items match\nyour search",
                                style = JasnifyTheme.typography.displayMedium.copy(
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = ContentTertiary
                            )
                        }
                    }
                }
            } else {
                item(key = "spacer_top", contentType = "spacer") {
                    Spacer(Modifier.height(12.dp))
                }

                val categoryList = categorizedItems.entries.toList()
                val highlightInsertIndex = if (categoryList.size <= 1) 0 else (categoryList.size - 1) / 2

                categoryList.forEachIndexed { index, (category, items) ->
                    item(key = "category_$category", contentType = "category_card") {
                        MenuCategoryCard(
                            categoryTitle = category,
                            items = items,
                            selectedItemIds = selectedItemIds,
                            isSelectionMode = isSelectionMode,
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    if (index == highlightInsertIndex && !isSelectionMode && !isSearchActive) {
                        item(key = "highlighted_top_vendors", contentType = "carousel") {
                            HighlightedVendors(
                                title = "TOP VENDORS",
                                subtitle = "CURATED FOR YOU",
                                vendors = foodCategoryVendors,
                                isHeadingTop = true,
                                headerImage = painterResource(id = R.drawable.ill_vendor_food_serve),
                                buttonText = "View all",
                                buttonTrailingIcon = painterResource(id = R.drawable.ic_arrow_right),
                                onButtonClick = onViewAllVendorsClick,
                                onVendorClick = onVendorClick,
                                onFavoriteToggle = onFavoriteToggle,
                                onOfferClick = onOfferClick,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }

                item(key = "footer", contentType = "footer") {
                    FooterJansify()
                }
            }
        }

        AnimatedVisibility(
            visible = !isSearchActive && !isSelectionMode && isBottomBarVisible,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 260)) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 260)) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(10f)
        ) {
            if(!isViewer){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = BottomGradientBrush)
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                            .pill360Shadow(
                                ambientColor = Color.Black.copy(alpha = 0.10f),
                                ambientBlur = 12.dp,
                                ambientSpread = 2.dp,
                                spotColor = Color.Black.copy(alpha = 0.15f),
                                spotBlur = 18.dp,
                                spotOffsetY = 4.dp
                            ),
                        color = SurfacePrimary,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomTextButton(
                                onClick = onAddAnItemClick,
                                text = "Add an Item",
                                type = ButtonType.Primary,
                                shapeStyle = ButtonShapeStyle.Round,
                                leadingIcon = painterResource(R.drawable.ic_plus),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        if (!isSelectionMode && !isSearchActive && isBottomBarVisible && !showAiChat) {
            AskAiButton(
                onClick = onAiChatClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp)
                    .zIndex(150f)
            )
        }
    }
}
