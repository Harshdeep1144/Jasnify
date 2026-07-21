package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun BudgetSummaryCard(
    isBudgetNotSet: Boolean,
    formattedTotalBudget: String,
    formattedRemaining: String,
    remainingPercentage: Float,
    onEditBudgetClick: () -> Unit,
    onViewSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    showEditButton: Boolean = true
) {
    if (isBudgetNotSet) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(SquircleShape(CornerLargeIncrease, CornerSmoothingDefault))
                .background(SurfaceSecondary)
                .drawBehind {
                    val dashLength = 6.dp.toPx()
                    val gapLength = 6.dp.toPx()

                    val stroke = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(dashLength, gapLength),
                            phase = 0f
                        )
                    )

                    drawRoundRect(
                        color = ContentSecondary,
                        style = stroke,
                        cornerRadius = CornerRadius(CornerLargeIncrease.toPx())
                    )
                }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_alert_triangle),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "You have not assigned\nyour budget yet.",
                        style = JasnifyTheme.typography.headingMedium,
                        textAlign = TextAlign.Center,
                        color = ContentPrimary
                    )
                }
                
                if (showEditButton) {
                    Spacer(Modifier.height(16.dp))

                    CustomTextButton(
                        onClick = onEditBudgetClick,
                        text = "Add a Budget",
                        containerColor = ContentPrimary,
                        shapeStyle = ButtonShapeStyle.Square,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(
                    SquircleShape(
                        CornerExtraLarge,
                        CornerSmoothingDefault
                    )
                )
                .background(SurfaceBrandSecondary)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    SquircleShape(
                        CornerExtraLarge,
                        CornerSmoothingDefault
                    )
                )
        ) {
            Image(
                painter = painterResource(R.drawable.bg_budget_pattern),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "TOTAL BUDGET",
                            style = JasnifyTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formattedTotalBudget,
                            style = JasnifyTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentPrimary
                        )
                    }

                    if (showEditButton) {
                        TopBarIconButton(
                            icon = TopIcon.CustomPainter(
                                painterResource(R.drawable.ic_edit)
                            ),
                            onClick = onEditBudgetClick,
                            backgroundStyle = ButtonBackground.TRANSPARENT,
                            iconSize = 20.dp,
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(
                        0.16f
                    )
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp
                    )
                ) {
                    Text(
                        text = "REMAINING FUNDS",
                        style = JasnifyTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = ContentSecondary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp
                        )
                    ) {
                        Text(
                            text = formattedRemaining,
                            style = JasnifyTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = ContentPrimary
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_info),
                            contentDescription = "Remaining Funds Info",
                            tint = ContentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { remainingPercentage },
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

                CustomTextButton(
                    onClick = onViewSummaryClick,
                    text = "View Summary",
                    size = ButtonSize.Medium,
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Square,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}




@Preview(showBackground = true, name = "Budget Set")
@Composable
fun BudgetSummaryCard_Set_Preview() {
    JasnifyTheme {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BudgetSummaryCard(
                isBudgetNotSet = true,
                formattedTotalBudget = "$0.00",
                formattedRemaining = "$0.00",
                remainingPercentage = 0f,
                onEditBudgetClick = {},
                onViewSummaryClick = {},
                modifier = Modifier.weight(1f),
                showEditButton = true
            )

            // State 2: Low Remaining Budget (e.g., 15% left)
            BudgetSummaryCard(
                isBudgetNotSet = false,
                formattedTotalBudget = "$2,500.00",
                formattedRemaining = "$375.00",
                remainingPercentage = 0.15f,
                onEditBudgetClick = {},
                onViewSummaryClick = {},
                modifier = Modifier.weight(1f),
                showEditButton = true
            )

        }
    }
}