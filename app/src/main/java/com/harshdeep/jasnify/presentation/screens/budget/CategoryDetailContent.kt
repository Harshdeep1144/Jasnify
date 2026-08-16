package com.harshdeep.jasnify.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ExpenseItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.chip.ChipShapeStyle
import com.harshdeep.jasnify.presentation.components.chip.ChipSize
import com.harshdeep.jasnify.presentation.components.chip.FilterChip
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun CategoryDetailContent(
    selectedCategoryName: String,
    formattedCategoryTotal: String,
    categoryProgressRatio: Float,
    sortedCategoryExpenses: List<ExpenseItem>,
    selectedCategoryChips: Set<String>,
    onCategoryChipsChange: (Set<String>) -> Unit,
    expandedCardId: String?,
    onExpandedCardIdChange: (String?) -> Unit,
    isViewer: Boolean,
    onBackClick: () -> Unit,
    onRenameCategoryClick: () -> Unit,
    onDeleteExpenseClick: (ExpenseItem) -> Unit,
    onModifyExpenseClick: (ExpenseItem) -> Unit
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
        ) {
            CustomTopBar(
                onBackClick = onBackClick,
                buttonStyle = ButtonBackground.TRANSLUCENT,
                translucentAlpha = 0.5f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedCategoryName,
                            style = JasnifyTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentPrimary
                        )
                        if (!isViewer) {
                            Box(
                                modifier = Modifier.padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_edit),
                                    contentDescription = "Rename Category",
                                    tint = ContentPrimary,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onRenameCategoryClick() }
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Category Expenses",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentSecondary
                        )
                        Text(
                            text = formattedCategoryTotal,
                            style = JasnifyTheme.typography.headingLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentPrimary
                        )
                    }
                    Row {
                        LinearProgressIndicator(
                            progress = { categoryProgressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape),
                            color = ContentBrand,
                            trackColor = MaterialTheme.colorScheme.outline.copy(
                                alpha = 0.16f
                            ),
                        )
                    }
                }
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        val isRecentActive = selectedCategoryChips.contains("Recent First")
                        FilterChip(
                            label = "Recent First",
                            isSelected = isRecentActive,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            hasStroke = true,
                            leadingIcon = ImageVector.vectorResource(R.drawable.ic_clock_forward),
                            trailingIcon = if (isRecentActive) Icons.Default.Close else null,
                            onClick = {
                                onCategoryChipsChange(
                                    if (isRecentActive) selectedCategoryChips - "Recent First"
                                    else selectedCategoryChips + "Recent First"
                                )
                            },
                            onTrailingIconClick = {
                                onCategoryChipsChange(selectedCategoryChips - "Recent First")
                            }
                        )
                    }

                    item {
                        val isMostActive = selectedCategoryChips.contains("Most Expensive")
                        FilterChip(
                            label = "Most Expensive",
                            isSelected = isMostActive,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            hasStroke = true,
                            leadingIcon = ImageVector.vectorResource(R.drawable.ic_line_chart_up),
                            trailingIcon = if (isMostActive) Icons.Default.Close else null,
                            onClick = {
                                onCategoryChipsChange(
                                    if (isMostActive) selectedCategoryChips - "Most Expensive"
                                    else selectedCategoryChips + "Most Expensive"
                                )
                            },
                            onTrailingIconClick = {
                                onCategoryChipsChange(selectedCategoryChips - "Most Expensive")
                            }
                        )
                    }

                    item {
                        val isLeastActive = selectedCategoryChips.contains("Least Expensive")
                        FilterChip(
                            label = "Least Expensive",
                            isSelected = isLeastActive,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            hasStroke = true,
                            leadingIcon = ImageVector.vectorResource(R.drawable.ic_line_chart_down),
                            trailingIcon = if (isLeastActive) Icons.Default.Close else null,
                            onClick = {
                                onCategoryChipsChange(
                                    if (isLeastActive) selectedCategoryChips - "Least Expensive"
                                    else selectedCategoryChips + "Least Expensive"
                                )
                            },
                            onTrailingIconClick = {
                                onCategoryChipsChange(selectedCategoryChips - "Least Expensive")
                            }
                        )
                    }
                }
            }

            if (sortedCategoryExpenses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_receipt),
                            contentDescription = "No expenses",
                            tint = ContentTertiary,
                            modifier = Modifier.size(84.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No expenses",
                            style = JasnifyTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentTertiary
                        )
                    }
                }
            } else {
                items(
                    items = sortedCategoryExpenses,
                    key = { it.id },
                    contentType = { "expense" }
                ) { item ->
                    val isFirst = sortedCategoryExpenses.firstOrNull()?.id == item.id
                    val isLast = sortedCategoryExpenses.lastOrNull()?.id == item.id

                    val itemShape = remember(isFirst, isLast) {
                        when {
                            isFirst && isLast -> SquircleShape(CornerLarge, CornerSmoothingDefault)
                            isFirst -> SquircleShape(
                                CornerLarge, CornerLarge,
                                CornerExtraSmall, CornerExtraSmall, CornerSmoothingDefault
                            )
                            isLast -> SquircleShape(
                                CornerExtraSmall, CornerExtraSmall,
                                CornerLarge, CornerLarge, CornerSmoothingDefault
                            )
                            else -> SquircleShape(CornerExtraSmall, CornerSmoothingDefault)
                        }
                    }

                    ExpenseCard(
                        title = item.title,
                        category = item.category,
                        amount = item.amount,
                        emoji = item.emoji,
                        lastUpdatedBy = item.lastUpdatedBy,
                        lastUpdatedDate = item.lastUpdatedDate,
                        showActions = (expandedCardId == item.id),
                        cardShape = itemShape,
                        isEditable = !isViewer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp, 1.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                focusManager.clearFocus()
                                onExpandedCardIdChange(if (expandedCardId == item.id) null else item.id)
                            },
                        onDeleteClick = { onDeleteExpenseClick(item) },
                        onModifyClick = { onModifyExpenseClick(item) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
