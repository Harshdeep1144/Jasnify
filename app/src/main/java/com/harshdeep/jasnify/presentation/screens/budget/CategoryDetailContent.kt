package com.harshdeep.jasnify.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val SingleCardShape = SquircleShape(CornerLarge, CornerSmoothingDefault)
private val TopCardShape = SquircleShape(
    topStart = CornerLarge,
    topEnd = CornerLarge,
    bottomStart = CornerExtraSmall,
    bottomEnd = CornerExtraSmall,
    cornerSmoothing = CornerSmoothingDefault
)
private val BottomCardShape = SquircleShape(
    topStart = CornerExtraSmall,
    topEnd = CornerExtraSmall,
    bottomStart = CornerLarge,
    bottomEnd = CornerLarge,
    cornerSmoothing = CornerSmoothingDefault
)
private val MiddleCardShape = SquircleShape(CornerExtraSmall, CornerSmoothingDefault)

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

    val editPainter = painterResource(R.drawable.ic_edit)
    val receiptPainter = painterResource(R.drawable.ic_receipt)
    val clockForwardVector = ImageVector.vectorResource(R.drawable.ic_clock_forward)
    val lineChartUpVector = ImageVector.vectorResource(R.drawable.ic_line_chart_up)
    val lineChartDownVector = ImageVector.vectorResource(R.drawable.ic_line_chart_down)

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
            item(key = "category_header_summary", contentType = "header") {
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
                                    painter = editPainter,
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

            item(key = "category_filter_chips", contentType = "filters") {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item(key = "chip_recent", contentType = "filter_chip") {
                        val isRecentActive = selectedCategoryChips.contains("Recent First")
                        FilterChip(
                            label = "Recent First",
                            isSelected = isRecentActive,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            hasStroke = true,
                            leadingIcon = clockForwardVector,
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

                    item(key = "chip_most_expensive", contentType = "filter_chip") {
                        val isMostActive = selectedCategoryChips.contains("Most Expensive")
                        FilterChip(
                            label = "Most Expensive",
                            isSelected = isMostActive,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            hasStroke = true,
                            leadingIcon = lineChartUpVector,
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

                    item(key = "chip_least_expensive", contentType = "filter_chip") {
                        val isLeastActive = selectedCategoryChips.contains("Least Expensive")
                        FilterChip(
                            label = "Least Expensive",
                            isSelected = isLeastActive,
                            shapeStyle = ChipShapeStyle.Round,
                            size = ChipSize.Small,
                            hasStroke = true,
                            leadingIcon = lineChartDownVector,
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
                item(key = "empty_category_expenses", contentType = "empty_state") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = receiptPainter,
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
                    contentType = { "category_expense_card" }
                ) { item ->
                    val isFirst = sortedCategoryExpenses.firstOrNull()?.id == item.id
                    val isLast = sortedCategoryExpenses.lastOrNull()?.id == item.id

                    val itemShape = when {
                        isFirst && isLast -> SingleCardShape
                        isFirst -> TopCardShape
                        isLast -> BottomCardShape
                        else -> MiddleCardShape
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

            item(key = "category_detail_bottom_spacer", contentType = "spacer") {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}