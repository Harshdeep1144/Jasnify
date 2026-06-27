package com.harshdeep.jasnify.presentation.components.cards

import com.harshdeep.jasnify.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun ExpenseCard(
    title: String,
    category: String,
    amount: String,
    modifier: Modifier = Modifier,
    lastUpdatedBy: String? = null,
    lastUpdatedDate: String? = null,
    showActions: Boolean = false,
    onDeleteClick: () -> Unit = {},
    onModifyClick: () -> Unit = {},
    emoji: String = "💸",
    cardShape: SquircleShape = SquircleShape(CornerExtraSmall, CornerSmoothingDefault)
) {
    val leftButtonShape = RoundedCornerShape(CornerLarge, CornerExtraSmall, CornerExtraSmall, CornerLarge)
    val rightButtonShape = SquircleShape(CornerExtraSmall, CornerLarge, CornerExtraSmall, CornerLarge)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                )
            )
            .background(color = SurfacePrimary, shape = cardShape)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Elegant circle container hosting our direct emoji
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(SurfaceBrandSecondary, shape = CircleShape)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), shape = CircleShape)
            ) {
                Text(
                    text = emoji,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Title and Category Label Block
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary
                )
            }

            // Numeric Cost Label
            Text(
                text = amount,
                style = JasnifyTheme.typography.displaySmall,
                color = ContentPrimary,
                fontWeight = FontWeight.Medium
            )
        }

        AnimatedVisibility(
            visible = showActions,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                )
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                )
            ) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                )
            )
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTextButton(
                        onClick = onDeleteClick,
                        text = "Delete",
                        size = ButtonSize.Medium,
                        type = ButtonType.Tertiary,
                        shapeStyle = ButtonShapeStyle.Square,
                        leadingIcon = painterResource(R.drawable.ic_delete),
                        containerColor = Color.Transparent, // Transparent background so our outer modifier draws the custom corners
                        contentColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .weight(1f)
                            .clip(leftButtonShape)
                            .background(color = SurfaceSecondary)
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    CustomTextButton(
                        onClick = onModifyClick,
                        text = "Modify",
                        size = ButtonSize.Medium,
                        type = ButtonType.Tertiary,
                        shapeStyle = ButtonShapeStyle.Square,
                        leadingIcon = painterResource(R.drawable.ic_edit),
                        containerColor = Color.Transparent, // Transparent background so our outer modifier draws the custom corners
                        modifier = Modifier
                            .weight(1f)
                            .clip(rightButtonShape)
                            .background(color = SurfaceSecondary)
                    )
                }

                // --- Card Metadata Info Footer ---
                if (lastUpdatedBy != null || lastUpdatedDate != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        if (lastUpdatedBy != null) {
                            Row {
                                Text(
                                    text = "Last updated by ",
                                    style = JasnifyTheme.typography.bodyMedium,
                                    color = ContentSecondary,
                                    fontWeight = FontWeight.Light
                                )
                                Text(
                                    text = lastUpdatedBy,
                                    style = JasnifyTheme.typography.bodyMedium,
                                    color = ContentSecondary
                                )
                            }
                        }
                        if (lastUpdatedDate != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastUpdatedDate,
                                style = JasnifyTheme.typography.bodyMedium,
                                color = ContentSecondary,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Preview Layer matching Mockup Frame Design ---

@Preview(showBackground = true, name = "Expense Card Stack Visual Preview")
@Composable
private fun ExpenseCardPreview() {
    Column {
        ExpenseCard(
            title = "Expense",
            category = "Category",
            amount = "₹0",
            emoji = "💍",
            showActions = false
        )

        ExpenseCard(
            title = "Expense",
            category = "Category",
            amount = "₹0",
            emoji = "💸",
            showActions = true,
            lastUpdatedBy = "Anand K.",
            lastUpdatedDate = "Aug 24, 2025, 01:04pm"
        )
    }
}