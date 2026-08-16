package com.harshdeep.jasnify.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ExpenseItem
import com.harshdeep.jasnify.presentation.components.cards.BudgetSummaryCard
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

@Composable
fun BudgetTrackerContent(
    isBudgetNotSet: Boolean,
    formattedTotalBudget: String,
    formattedRemaining: String,
    remainingPercentage: Float,
    isOwner: Boolean,
    isViewer: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredExpenses: List<ExpenseItem>,
    expandedCardId: String?,
    onExpandedCardIdChange: (String?) -> Unit,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onEditBudgetClick: () -> Unit,
    onViewSummaryClick: () -> Unit,
    onFilterClick: () -> Unit,
    onDeleteExpenseClick: (ExpenseItem) -> Unit,
    onModifyExpenseClick: (ExpenseItem) -> Unit,
    listState: LazyListState,
    isSearchBarFocused: Boolean,
    onSearchBarFocusChange: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceSecondary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfacePrimary)
                .statusBarsPadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            CustomTopBar(
                title = "Budget Tracker",
                onBackClick = onBackClick,
                onMenuClick = onMenuClick,
                isLargeTitle = true
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                },
        ) {
            // Budget Summary Card Section
            item {
                val summaryGradient = remember {
                    Brush.verticalGradient(
                        listOf(SurfacePrimary, SurfaceSecondary)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = summaryGradient)
                        .padding(12.dp)
                ) {
                    BudgetSummaryCard(
                        isBudgetNotSet = isBudgetNotSet,
                        formattedTotalBudget = formattedTotalBudget,
                        formattedRemaining = formattedRemaining,
                        remainingPercentage = remainingPercentage,
                        onEditBudgetClick = onEditBudgetClick,
                        onViewSummaryClick = onViewSummaryClick,
                        showEditButton = isOwner
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "All Expenses",
                        style = JasnifyTheme.typography.headingXLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = ContentPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomSearchBar(
                            value = searchQuery,
                            placeholder = "Search with AI",
                            onValueChange = onSearchQueryChange,
                            isAiSearch = true,
                            modifier = Modifier
                                .weight(1.0f)
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused && !isSearchBarFocused) {
                                        coroutineScope.launch {
                                            delay(150)
                                            listState.animateScrollToItem(
                                                index = 1,
                                                scrollOffset = -8
                                            )
                                        }
                                    }
                                    onSearchBarFocusChange(focusState.isFocused)
                                },
                            backgroundColor = SurfacePrimary
                        )

                        FilterButton(
                            onClick = onFilterClick,
                            backgroundColor = SurfacePrimary
                        )
                    }
                }
            }

            if (filteredExpenses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
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
                            text = "No expense found",
                            style = JasnifyTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentTertiary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                items(
                    items = filteredExpenses,
                    key = { it.id },
                    contentType = { "expense" }
                ) { item ->
                    val isFirst = filteredExpenses.firstOrNull()?.id == item.id
                    val isLast = filteredExpenses.lastOrNull()?.id == item.id

                    val itemShape = remember(isFirst, isLast) {
                        when {
                            isFirst && isLast -> SquircleShape(
                                CornerLarge,
                                CornerSmoothingDefault
                            )

                            isFirst -> SquircleShape(
                                CornerLarge,
                                CornerLarge,
                                CornerExtraSmall,
                                CornerExtraSmall,
                                CornerSmoothingDefault
                            )

                            isLast -> SquircleShape(
                                CornerExtraSmall,
                                CornerExtraSmall,
                                CornerLarge,
                                CornerLarge,
                                CornerSmoothingDefault
                            )

                            else -> SquircleShape(
                                CornerExtraSmall,
                                CornerSmoothingDefault
                            )
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
                            .padding(horizontal = 12.dp, vertical = 1.dp)
                            .graphicsLayer {}
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
                Spacer(modifier = Modifier.height(124.dp))
            }
        }
    }
}
