package com.harshdeep.jasnify.presentation.components.cards


import com.harshdeep.jasnify.R
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.Outfit
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
    iconPainter: Painter? = null,
) {
    // Standardized safe squircle container shape
    val cardShape = SquircleShape(CornerLarge, CornerSmoothingDefault)

    // Left button has rounded corners only on the left side
    val leftButtonShape = RoundedCornerShape(
        topStart = CornerLarge,
        bottomStart = CornerLarge,
        topEnd = 0.dp,
        bottomEnd = 0.dp
    )

    // Right button has rounded corners only on the right side
    val rightButtonShape = RoundedCornerShape(
        topStart = 0.dp,
        bottomStart = 0.dp,
        topEnd = CornerLarge,
        bottomEnd = CornerLarge
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SurfacePrimary, shape = cardShape)
            .padding(16.dp)
    ) {
        // --- Header Section (Icon, Title, Category, Amount) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Circular Thumbnail/Icon Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(SurfaceBrandSecondary, shape = CircleShape)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), shape = CircleShape)
            ) {
                if (iconPainter != null) {
                    Icon(
                        painter = iconPainter,
                        contentDescription = "Category Icon",
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        painter = rememberVectorPainter(Icons.Rounded.CreditCard),
                        contentDescription = "Card Icon",
                        tint = Color(0xFF2E4D48),
                        modifier = Modifier.size(28.dp)
                    )
                }
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

        // --- Interactive Action Layout Section ---
        if (showActions) {
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

// --- Preview Layer matching Mockup Frame Design ---

@Preview(showBackground = true, name = "Expense Card Stack Visual Preview")
@Composable
private fun ExpenseCardPreview() {
    Column{
        ExpenseCard(
            title = "Expense",
            category = "Category",
            amount = "₹0",
            showActions = false
        )

        ExpenseCard(
            title = "Expense",
            category = "Category",
            amount = "₹0",
            showActions = true,
            lastUpdatedBy = "Anand K.",
            lastUpdatedDate = "Aug 24, 2025, 01:04pm"
        )
    }
}