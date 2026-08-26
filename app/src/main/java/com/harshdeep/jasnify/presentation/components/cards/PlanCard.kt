package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet.PlanType
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import sv.lib.squircleshape.SquircleShape

/**
 * A customizable Plan Card component displaying plan details, active status badge,
 * optional upgrade action button, and a "View Benefits" trigger.
 */
@Composable
fun PlanCard(
    planName: String,
    price: String,
    modifier: Modifier = Modifier,
    planType: PlanType = PlanType.BASIC,
    isCurrentPlan: Boolean = false,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    onViewBenefitsClick: () -> Unit = {},
    buttonEnabled: Boolean = true,
    buttonLeadingIcon: Painter? = null
) {
    val cardShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

    // Pro dark vertical gradient
    val proGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1B2020),
            Color(0xFF324844)
        )
    )

    // Ultimate vertical gradient matching the image color progression from top to bottom
    val ultimateGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF351B24), // Dark wine/plum at top
            0.28f to Color(0xFF423522), // Olive/amber undertone
            0.62f to Color(0xFF1B5554), // Deep teal in the midsection
            1.0f to Color(0xFF00B57F)  // Vibrant aqua/cyan at the bottom
        )
    )

    val isDarkCard = planType != PlanType.BASIC
    val primaryTextColor = if (isDarkCard) ContentInvPrimary else ContentPrimary
    val actionTextColor = if (isDarkCard) ContentInvPrimary else ContentBrandDark

    val backgroundModifier = when (planType) {
        PlanType.BASIC -> Modifier.background(SurfaceBrandSecondary)
        PlanType.PRO -> Modifier.background(proGradient)
        PlanType.ULTIMATE -> Modifier.background(ultimateGradient)
    }

    Surface(
        modifier = modifier.width(280.dp),
        shape = cardShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color(0x1A000000))
    ) {
        Column(
            modifier = Modifier
                .then(backgroundModifier)
                .padding(20.dp),
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
                        color = primaryTextColor
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = price,
                        style = JasnifyTheme.typography.labelMedium,
                        color = primaryTextColor
                    )
                }

                if (isCurrentPlan) {
                    Surface(
                        color = (if (isDarkCard) ContentInvPrimary else ContentPrimary).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(100)
                    ) {
                        Text(
                            text = "Current",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = JasnifyTheme.typography.labelMedium,
                            color = if (isDarkCard) ContentPrimary else ContentInvPrimary
                        )
                    }
                }
            }

            // Footer: Action Button and View Benefits
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (buttonText != null && onButtonClick != null) {
                        Surface(
                            onClick = { if (buttonEnabled) onButtonClick() },
                            color = (if (isDarkCard) Color(0xFF142928) else ContentPrimary).copy(
                                alpha = if (buttonEnabled) 0.7f else 0.3f
                            ),
                            shape = RoundedCornerShape(100),
                            modifier = Modifier.padding(end = 10.dp),
                            enabled = buttonEnabled
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
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
                            color = actionTextColor
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = actionTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewPlanCards() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PlanCard(
            planName = "Basic",
            price = "FREE",
            planType = PlanType.BASIC,
            isCurrentPlan = true
        )

        PlanCard(
            planName = "Pro",
            price = "$5/month",
            planType = PlanType.PRO,
            buttonText = "Upgrade Now",
            onButtonClick = {}
        )

        PlanCard(
            planName = "Ultimate",
            price = "$20/month",
            planType = PlanType.ULTIMATE,
            buttonText = "Upgrade Now",
            onButtonClick = {}
        )
    }
}