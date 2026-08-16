package com.harshdeep.jasnify.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.domain.model.CategorySummaryData
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.cards.CategoryCard
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*

@Composable
fun ExpenseCategoryContent(
    categorySearchQuery: String,
    onCategorySearchQueryChange: (String) -> Unit,
    filteredCategorySummary: List<CategorySummaryData>,
    isViewer: Boolean,
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryMenuClick: (String) -> Unit,
    onViewSummaryClick: () -> Unit,
    onAddCategoryClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceSecondary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceSecondary)
                .statusBarsPadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            CustomTopBar(
                title = "Expense Category",
                onBackClick = onBackClick,
                buttonStyle = ButtonBackground.TRANSLUCENT,
                translucentAlpha = 0.5f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CustomSearchBar(
                    value = categorySearchQuery,
                    placeholder = "Search",
                    onValueChange = onCategorySearchQueryChange,
                    backgroundColor = SurfacePrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(
                        items = filteredCategorySummary,
                        key = { it.name },
                        contentType = { "category" }
                    ) { categoryItem ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategoryClick(categoryItem.name) }
                        ) {
                            CategoryCard(
                                title = categoryItem.name,
                                amount = categoryItem.amountFormatted,
                                emojis = categoryItem.emojis,
                                totalItemCount = categoryItem.totalCount,
                                onMenuClick = { onCategoryMenuClick(categoryItem.name) },
                                showMenu = !isViewer
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            spotColor = ContentPrimary,
                            ambientColor = ContentPrimary
                        )
                        .background(SurfacePrimary)
                        .padding(12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextButton(
                        onClick = onViewSummaryClick,
                        text = "View Summary",
                        type = ButtonType.Secondary,
                        shapeStyle = ButtonShapeStyle.Square,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isViewer) {
                        CustomTextButton(
                            onClick = onAddCategoryClick,
                            text = "Add Category",
                            type = ButtonType.Primary,
                            shapeStyle = ButtonShapeStyle.Square,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
