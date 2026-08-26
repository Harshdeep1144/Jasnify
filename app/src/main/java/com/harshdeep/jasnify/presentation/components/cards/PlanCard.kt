package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.plansheet.PlanType
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import kotlin.math.cos
import kotlin.math.sin
import sv.lib.squircleshape.SquircleShape

@Composable
fun FluidWaterGradientBackground(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "fluidWaterTransition")

    // Continuous time accumulator for liquid simulation
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waterPhase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val centerTeal = Offset(
            x = w * (0.50f + 0.18f * sin(t * 1.0f) + 0.10f * cos(t * 1.7f + 1.2f)),
            y = h * (0.45f + 0.16f * cos(t * 0.8f) + 0.08f * sin(t * 2.1f + 0.4f))
        )

        val topCerulean = Offset(
            x = w * (0.28f + 0.22f * cos(t * 0.9f + 0.5f) + 0.08f * sin(t * 1.5f)),
            y = h * (0.25f + 0.18f * sin(t * 1.1f + 1.8f) + 0.07f * cos(t * 2.3f))
        )

        val bottomAqua = Offset(
            x = w * (0.70f + 0.20f * sin(t * 0.7f + 2.4f) + 0.09f * cos(t * 1.3f)),
            y = h * (0.75f + 0.16f * cos(t * 1.2f + 0.9f) + 0.08f * sin(t * 1.9f))
        )

        val deepIndigoSwirl = Offset(
            x = w * (0.22f + 0.17f * sin(t * 1.3f + 3.1f) + 0.07f * cos(t * 0.6f)),
            y = h * (0.78f + 0.19f * cos(t * 0.9f + 2.0f) + 0.06f * sin(t * 1.8f))
        )

        val midCobaltSwirl = Offset(
            x = w * (0.80f + 0.15f * cos(t * 1.1f + 1.1f) + 0.08f * sin(t * 1.4f)),
            y = h * (0.30f + 0.17f * sin(t * 0.85f + 3.5f) + 0.07f * cos(t * 2.0f))
        )

        val rTeal = w * (0.85f + 0.10f * sin(t * 1.4f))
        val rCerulean = w * (0.90f + 0.12f * cos(t * 1.1f + 0.8f))
        val rAqua = w * (0.75f + 0.09f * sin(t * 1.6f + 2.0f))
        val rIndigo = w * (0.85f + 0.11f * cos(t * 0.9f + 1.4f))
        val rCobalt = w * (0.80f + 0.10f * sin(t * 1.2f + 0.5f))

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF03111E),
                    Color(0xFF062338),
                    Color(0xFF021B2B)
                ),
                start = Offset.Zero,
                end = Offset(w, h)
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0E386B).copy(alpha = 0.85f),
                    Color(0xFF061E38).copy(alpha = 0.50f),
                    Color.Transparent
                ),
                center = deepIndigoSwirl,
                radius = rIndigo
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0B6396).copy(alpha = 0.80f),
                    Color(0xFF073B5B).copy(alpha = 0.45f),
                    Color.Transparent
                ),
                center = topCerulean,
                radius = rCerulean
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0D7EAF).copy(alpha = 0.75f),
                    Color(0xFF064766).copy(alpha = 0.40f),
                    Color.Transparent
                ),
                center = midCobaltSwirl,
                radius = rCobalt
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0AA6B8).copy(alpha = 0.70f),
                    Color(0xFF065963).copy(alpha = 0.35f),
                    Color.Transparent
                ),
                center = centerTeal,
                radius = rTeal
            )
        )

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF26D0CE).copy(alpha = 0.65f),
                    Color(0xFF148382).copy(alpha = 0.30f),
                    Color.Transparent
                ),
                center = bottomAqua,
                radius = rAqua
            )
        )
    }
}

@Composable
fun PlanCard(
    planName: String,
    price: String,
    modifier: Modifier = Modifier,
    planType: PlanType = PlanType.BASIC,
    isCurrentPlan: Boolean = false,
    buttonText: String? = "Upgrade Now",
    onUpgradeNowClick: (() -> Unit)? = null,
    onViewBenefitsClick: () -> Unit = {},
    buttonLeadingIcon: Painter? = null
) {
    val cardShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)

    val proGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF435853),
            Color(0xFF5A7B73),
            Color(0xFF759D93)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val isDarkCard = planType != PlanType.BASIC
    val primaryTextColor = if (isDarkCard) ContentInvPrimary else ContentPrimary
    val actionTextColor = if (isDarkCard) ContentInvPrimary else ContentBrandDark

    val backgroundModifier = when (planType) {
        PlanType.BASIC -> Modifier.background(SurfaceBrandSecondary)
        PlanType.PRO -> Modifier.background(proGradient)
        PlanType.ULTIMATE -> Modifier
    }

    Surface(
        modifier = modifier
            .widthIn(min = 280.dp, max = 320.dp)
            .height(160.dp),
        shape = cardShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color(0x0D000000))
    ) {
        Box(
            modifier = Modifier
                .then(backgroundModifier)
                .fillMaxSize()
        ) {
            if (planType == PlanType.BASIC) {
                Image(
                    painter = painterResource(id = R.drawable.bg_basic_plan_pattern),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.6f),
                    alignment = Alignment.CenterEnd,
                    contentScale = ContentScale.Inside
                )
            }
            if (planType == PlanType.PRO) {
                Image(
                    painter = painterResource(id = R.drawable.bg_pro_plan_pattern),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.6f),
                    alignment = Alignment.CenterEnd,
                    contentScale = ContentScale.Inside
                )
            }
            if (planType == PlanType.ULTIMATE) {
                FluidWaterGradientBackground(
                    modifier = Modifier.matchParentSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
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
                            fontWeight = FontWeight.Medium,
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
                        if (buttonText != null && onUpgradeNowClick != null) {
                            Surface(
                                onClick = onUpgradeNowClick,
                                color = (if (isDarkCard) Color(0xFF142928) else ContentPrimary).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(100),
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (buttonLeadingIcon != null) {
                                        Icon(
                                            painter = buttonLeadingIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = ContentInvPrimary
                                        )
                                    }
                                    Text(
                                        text = buttonText,
                                        style = JasnifyTheme.typography.labelMedium.copy(
                                            lineHeightStyle = androidx.compose.ui.text.style.LineHeightStyle(
                                                alignment = androidx.compose.ui.text.style.LineHeightStyle.Alignment.Center,
                                                trim = androidx.compose.ui.text.style.LineHeightStyle.Trim.None
                                            ),
                                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                                includeFontPadding = false
                                            )
                                        ),
                                        color = ContentInvPrimary,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
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
            buttonLeadingIcon = painterResource(R.drawable.ic_lock),
            onUpgradeNowClick = {}
        )

        PlanCard(
            planName = "Ultimate",
            price = "$20/month",
            planType = PlanType.ULTIMATE,
            buttonLeadingIcon = painterResource(R.drawable.ic_lock),
            onUpgradeNowClick = {}
        )
    }
}