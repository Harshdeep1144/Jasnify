package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

/**
 * A customizable Plan Card component displaying plan details, active status badge,
 * optional upgrade action button, and a "View Benefits" trigger.
 */
@Composable
fun PlanCard(
    planName: String,
    price: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    isCurrentPlan: Boolean = false,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    onViewBenefitsClick: () -> Unit = {},
    buttonEnabled: Boolean = true,
    buttonLeadingIcon: Painter? = null
) {
    Surface(
        modifier = modifier.width(280.dp),
        color = backgroundColor,
        shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Plan Name and Current Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = planName,
                        style = JasnifyTheme.typography.headingXLarge,
                        color = ContentPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = price,
                        style = JasnifyTheme.typography.labelMedium,
                        color = ContentPrimary
                    )
                }

                if (isCurrentPlan) {
                    Surface(
                        color = ContentPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(100)
                    ) {
                        Text(
                            text = "Current",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = JasnifyTheme.typography.labelMedium,
                            color = ContentInvPrimary
                        )
                    }
                }
            }

            // Footer: Action Button and View Benefits
            // Removing unwanted vertical padding i.e. Wrapping inside CompositionLocalProvider resets the mandatory 48dp minimum
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (buttonText != null && onButtonClick != null) {
                        Surface(
                            onClick = { if (buttonEnabled) onButtonClick() },
                            color = if (buttonEnabled) ContentPrimary.copy(alpha = 0.5f) else ContentPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(100),
                            modifier = Modifier.padding(end = 10.dp),
                            enabled = buttonEnabled
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (buttonLeadingIcon != null) {
                                    Icon(
                                        painter = buttonLeadingIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = ContentInvPrimary.copy(alpha = if (buttonEnabled) 1f else 0.5f)
                                    )
                                }
                                Text(
                                    text = buttonText,
                                    style = JasnifyTheme.typography.labelMedium,
                                    color = ContentInvPrimary.copy(alpha = if (buttonEnabled) 1f else 0.5f)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onViewBenefitsClick() }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View Benefits",
                            style = JasnifyTheme.typography.labelLarge,
                            color = ContentBrandDark
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = ContentBrandDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun PreviewPlanCards() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Basic Plan
        PlanCard(
            planName = "Basic Plan",
            price = "FREE",
            backgroundColor = Color(0xFFF4E3E2), // Soft Peach
            isCurrentPlan = true
        )

        // 2. Pro Plan
        PlanCard(
            planName = "Pro",
            price = "$5/month",
            backgroundColor = Color(0xFFFBD6B9), // Light Apricot
            buttonText = "Upgrade Now",
            onButtonClick = {}
        )

        // 3. Ultimate Plan
        PlanCard(
            planName = "Ultimate",
            price = "$20/month",
            backgroundColor = Color(0xFFD3CDE8), // Light Lavender
            buttonText = "Upgrade Now",
            onButtonClick = {}
        )
    }
}