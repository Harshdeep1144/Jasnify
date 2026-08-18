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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.CustomPieChart
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.presentation.components.others.PieChartSlice
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

private val CardContainerShape = SquircleShape(CornerLargeIncrease)
private val IndicatorPillShape = SquircleShape(100)
private val RoundedShadowShape = RoundedCornerShape(20.dp)
private val RoundedCardShadowShape = RoundedCornerShape(24.dp)

private val SuccessGreenColor = Color(0xFF137935)
private val SpentRedColor = Color(0xFFBF3C34)

@Composable
fun ExpenseSummaryContent(
    pieSlices: List<PieChartSlice>,
    centerTextPrimaryValue: String,
    formattedRemaining: String,
    remainingPercentageText: String,
    formattedTotalSpent: String,
    spentPercentageText: String,
    processedCategories: List<Pair<String, Double>>,
    getCategoryColor: (String) -> Color,
    isViewer: Boolean,
    onBackClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onAiOverviewClick: () -> Unit,
    formatAmount: (Double) -> String
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var isCategoryListExpanded by remember { mutableStateOf(false) }

    val aiPainter = painterResource(R.drawable.ic_ai)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfacePrimary)
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
                title = "Expense Summary",
                onBackClick = onBackClick,
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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CustomPieChart(
                        slices = pieSlices,
                        centerTextPrimary = centerTextPrimaryValue,
                        centerTextSecondary = "TOTAL EXPENSES",
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedShadowShape,
                        )
                        .clip(CardContainerShape)
                        .background(SurfacePrimary)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Text(
                                text = "Remaining Funds",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "($remainingPercentageText%)",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary
                            )
                        }
                        Text(
                            text = formattedRemaining,
                            style = JasnifyTheme.typography.headingLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = SuccessGreenColor
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCardShadowShape,
                            spotColor = ContentPrimary.copy(alpha = 0.35f),
                            ambientColor = ContentPrimary.copy(alpha = 0.15f)
                        )
                        .clip(CardContainerShape)
                        .background(SurfacePrimary)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Text(
                                text = "Total Spent",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "($spentPercentageText%)",
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary
                            )
                        }
                        Text(
                            text = formattedTotalSpent,
                            style = JasnifyTheme.typography.headingLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = SpentRedColor
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    DashedDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        dashLength = 12f,
                        gapLength = 6f
                    )

                    val displayedCategories = if (isCategoryListExpanded) {
                        processedCategories
                    } else {
                        processedCategories.take(4)
                    }

                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        displayedCategories.forEach { (categoryName, totalCategorySpent) ->
                            val formattedCategorySpentText = "₹${formatAmount(totalCategorySpent)}"
                            val indicatorColor = getCategoryColor(categoryName)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .height(24.dp)
                                            .clip(IndicatorPillShape)
                                            .background(indicatorColor)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = categoryName,
                                        style = JasnifyTheme.typography.labelLarge,
                                        color = ContentPrimary
                                    )
                                }

                                Text(
                                    text = formattedCategorySpentText,
                                    style = JasnifyTheme.typography.labelLarge,
                                    color = ContentPrimary
                                )
                            }
                        }
                    }

                    if (processedCategories.size > 4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clickable { isCategoryListExpanded = !isCategoryListExpanded }
                                .padding(vertical = 11.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isCategoryListExpanded) "View less" else "View all",
                                style = JasnifyTheme.typography.bodyLarge,
                                color = ContentBrandDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isCategoryListExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = ContentBrandDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        spotColor = ContentPrimary.copy(alpha = 0.1f),
                        ambientColor = ContentPrimary.copy(alpha = 0.05f)
                    )
                    .background(SurfacePrimary)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextButton(
                    onClick = onAiOverviewClick,
                    text = "AI Overview",
                    shapeStyle = ButtonShapeStyle.Square,
                    type = ButtonType.Secondary,
                    modifier = Modifier.weight(1f),
                    leadingIcon = aiPainter
                )

                if (!isViewer) {
                    CustomTextButton(
                        onClick = onAddExpenseClick,
                        text = "Add Expense",
                        shapeStyle = ButtonShapeStyle.Square,
                        type = ButtonType.Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}