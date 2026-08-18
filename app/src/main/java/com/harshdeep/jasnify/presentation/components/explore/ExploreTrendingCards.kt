package com.harshdeep.jasnify.presentation.components.explore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ExploreTrendingCards(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(102.48.dp)
            .background(Color.Transparent)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Left Card Fan ---
        Box {
            // Back Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_01,
                targetRotation = -12f,
                targetOffsetX = 8.dp,
                targetOffsetY = (-12).dp,
                launchDelayMillis = 0,
                idleDurationMillis = 2600,
                idleDeltaX = (-2.5f)..2.5f,
                idleDeltaY = (-2f)..2f,
                idleDeltaRotation = (-2f)..2f
            )

            // Front Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_04,
                targetRotation = -28f,
                targetOffsetX = (-4).dp,
                targetOffsetY = 8.dp,
                launchDelayMillis = 100,
                idleDurationMillis = 3200,
                idleDeltaX = 3f..(-3f),
                idleDeltaY = 2.5f..(-2f),
                idleDeltaRotation = 2.5f..(-2.5f)
            )
        }

        // --- Center Titles ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = "EXPLORE",
                style = JasnifyTheme.typography.labelSmall,
                color = Color(0xFF4D2A15),
            )

            Text(
                text = "TRENDING CARDS",
                style = JasnifyTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily(Font(R.font.facadflux_bold)),
                ),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4D2A15),
            )
        }

        // --- Right Card Fan ---
        Box {
            // Back Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_03,
                targetRotation = 12f,
                targetOffsetX = (-8).dp,
                targetOffsetY = (-12).dp,
                launchDelayMillis = 50,
                idleDurationMillis = 2900,
                idleDeltaX = 2.5f..(-2.5f),
                idleDeltaY = (-2.5f)..2.5f,
                idleDeltaRotation = (-2f)..2f
            )

            // Front Card
            TrendingCardItem(
                imageResId = R.drawable.img_trending_card_02,
                targetRotation = 28f,
                targetOffsetX = 4.dp,
                targetOffsetY = 8.dp,
                launchDelayMillis = 150,
                idleDurationMillis = 3400,
                idleDeltaX = (-3f)..3f,
                idleDeltaY = 2f..(-2f),
                idleDeltaRotation = (-3f)..3f
            )
        }
    }
}

@Composable
private fun TrendingCardItem(
    imageResId: Int,
    targetRotation: Float,
    modifier: Modifier = Modifier,
    targetOffsetX: Dp = 0.dp,
    targetOffsetY: Dp = 0.dp,
    width: Dp = 41.44.dp,
    height: Dp = 55.26.dp,
    launchDelayMillis: Int = 0,
    idleDurationMillis: Int = 3000,
    idleDeltaX: ClosedFloatingPointRange<Float> = (-2.5f)..2.5f,
    idleDeltaY: ClosedFloatingPointRange<Float> = (-2f)..2f,
    idleDeltaRotation: ClosedFloatingPointRange<Float> = (-2f)..2f,
) {
    val cardShape = RoundedCornerShape(8.dp)
    val density = LocalDensity.current

    // Entrance Spring Progress (0f -> 1f)
    val launchProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (launchDelayMillis > 0) delay(launchDelayMillis.toLong().milliseconds)
        launchProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    // Continuous Idle Floating (Left/Right & Up/Down Sway)
    val infiniteTransition = rememberInfiniteTransition(label = "card_floating_idle")

    val idleOffsetX by infiniteTransition.animateFloat(
        initialValue = idleDeltaX.start,
        targetValue = idleDeltaX.endInclusive,
        animationSpec = infiniteRepeatable(
            animation = tween(idleDurationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway_x"
    )

    val idleOffsetY by infiniteTransition.animateFloat(
        initialValue = idleDeltaY.start,
        targetValue = idleDeltaY.endInclusive,
        animationSpec = infiniteRepeatable(
            animation = tween((idleDurationMillis * 1.15f).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway_y"
    )

    val idleRot by infiniteTransition.animateFloat(
        initialValue = idleDeltaRotation.start,
        targetValue = idleDeltaRotation.endInclusive,
        animationSpec = infiniteRepeatable(
            animation = tween((idleDurationMillis * 0.9f).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway_rot"
    )

    val maxShadowElevationPx = with(density) { 6.dp.toPx() }
    val initialYOffsetPx = with(density) { 16.dp.toPx() }
    val targetYOffsetPx = with(density) { targetOffsetY.toPx() }
    val targetXOffsetPx = with(density) { targetOffsetX.toPx() }
    val densityValue = density.density

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .graphicsLayer {
                val progress = launchProgress.value
                val clampedProgress = progress.coerceIn(0f, 1f)

                alpha = clampedProgress
                scaleX = 0.7f + (0.3f * progress)
                scaleY = 0.7f + (0.3f * progress)

                // Unified Hardware Shadow (No modifier layering collision)
                shadowElevation = maxShadowElevationPx * clampedProgress
                shape = cardShape
                clip = false

                // Rotation + Idle Sway
                rotationZ = (targetRotation + (idleRot * clampedProgress)) * progress

                // Translation + Idle Sway
                val idleXPx = idleOffsetX * densityValue
                val idleYPx = idleOffsetY * densityValue

                translationX = (targetXOffsetPx + idleXPx) * progress
                translationY = (initialYOffsetPx * (1f - progress)) + ((targetYOffsetPx + idleYPx) * progress)
            }
            .background(color = ContentInvPrimary, shape = cardShape)
            .border(width = 2.dp, color = ContentInvPrimary, shape = cardShape)
            .clip(cardShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
private fun ExploreTrendingCardsPreview() {
    ExploreTrendingCards()
}