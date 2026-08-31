package com.harshdeep.jasnify.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ExpenseItem
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.cards.BudgetSummaryCard
import com.harshdeep.jasnify.presentation.components.cards.ExpenseCard
import com.harshdeep.jasnify.presentation.components.filter.FilterButton
import com.harshdeep.jasnify.presentation.components.others.CustomSearchBar
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import com.harshdeep.jasnify.theme.TopBrandGradientBrush
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import kotlin.time.Duration.Companion.milliseconds

private val SingleItemShape = SquircleShape(CornerLarge, CornerSmoothingDefault)
private val FirstItemShape = SquircleShape(
    topStart = CornerLarge,
    topEnd = CornerLarge,
    bottomStart = CornerExtraSmall,
    bottomEnd = CornerExtraSmall,
    cornerSmoothing = CornerSmoothingDefault
)
private val LastItemShape = SquircleShape(
    topStart = CornerExtraSmall,
    topEnd = CornerExtraSmall,
    bottomStart = CornerLarge,
    bottomEnd = CornerLarge,
    cornerSmoothing = CornerSmoothingDefault
)
private val MiddleItemShape = SquircleShape(CornerExtraSmall, CornerSmoothingDefault)

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
    onChatClick: () -> Unit,
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
    val receiptPainter = painterResource(R.drawable.ic_receipt)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceSecondary)
    ) {
        // Gradient overlay spanning the top section behind the TopBar and Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(TopBrandGradientBrush)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Transparent top bar wrapper so the gradient shows through
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .noRippleClickable {
                        focusManager.clearFocus()
                    }
            ) {
                CustomTopBar(
                    title = "Budget Tracker",
                    onBackClick = onBackClick,
                    onMenuClick = onMenuClick,
                    secondaryIcon = TopIcon.Predefined.CHAT,
                    onSecondaryClick = onChatClick,
                    isLargeTitle = true,
                    buttonStyle = ButtonBackground.TRANSLUCENT
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .noRippleClickable {
                        focusManager.clearFocus()
                    },
            ) {
                // Budget Summary Card Section
                item(key = "budget_summary_header", contentType = "summary") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        BudgetSummaryCard(
                            isBudgetNotSet = isBudgetNotSet,
                            formattedTotalBudget = formattedTotalBudget,
                            formattedRemaining = formattedRemaining,
                            remainingPercentage = remainingPercentage,
                            onEditBudgetClick = onEditBudgetClick,
                            onViewSummaryClick = onViewSummaryClick,
                            showEditButton = isOwner,
                        )
                    }
                }

                item(key = "expenses_search_filter_header", contentType = "header") {
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
                                                delay(150.milliseconds)
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
                    item(key = "empty_expenses_state", contentType = "empty") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
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
                    itemsIndexed(
                        items = filteredExpenses,
                        key = { _, item -> item.id },
                        contentType = { _, _ -> "expense_card" }
                    ) { index, item ->
                        val isFirst = index == 0
                        val isLast = index == filteredExpenses.lastIndex

                        val itemShape = when {
                            isFirst && isLast -> SingleItemShape
                            isFirst -> FirstItemShape
                            isLast -> LastItemShape
                            else -> MiddleItemShape
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

                item(key = "bottom_spacer", contentType = "spacer") {
                    Spacer(modifier = Modifier.height(124.dp))
                }
            }
        }
    }
}