package com.harshdeep.jasnify.presentation.components.customDividers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AllCardsDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(55.26.dp)
            .background(Color.Transparent)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Left Title Section ---
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "EXPLORE",
                style = JasnifyTheme.typography.labelSmall,
                color = Color(0xFF4D2A15),
                letterSpacing = 2.sp
            )

            Text(
                text = "ALL CARDS",
                style = JasnifyTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily(Font(R.font.facadflux_bold)),
                ),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4D2A15),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Right Stacked Deck of Cards ---
        Box(
            modifier = Modifier.size(width = 96.dp, height = 55.26.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val rightmostOffsetX = 54.dp

            // Card 1: Slides out furthest left (bottom layer)
            StackedCardItem(
                imageResId = R.drawable.trending_card_preview_01,
                initialOffsetX = rightmostOffsetX,
                targetOffsetX = 0.dp,
                launchDelayMillis = 450,
                zIndex = 1f
            )

            // Card 2: Slides out left
            StackedCardItem(
                imageResId = R.drawable.trending_card_preview_02,
                initialOffsetX = rightmostOffsetX,
                targetOffsetX = 18.dp,
                launchDelayMillis = 300,
                zIndex = 2f
            )

            // Card 3: Slides slightly left
            StackedCardItem(
                imageResId = R.drawable.trending_card_preview_03,
                initialOffsetX = rightmostOffsetX,
                targetOffsetX = 36.dp,
                launchDelayMillis = 150,
                zIndex = 3f
            )

            // Card 4: Static right-most card (top layer)
            StackedCardItem(
                imageResId = R.drawable.trending_card_preview_04,
                initialOffsetX = rightmostOffsetX,
                targetOffsetX = rightmostOffsetX,
                launchDelayMillis = 0,
                zIndex = 4f
            )
        }
    }
}

@Composable
private fun StackedCardItem(
    imageResId: Int,
    targetOffsetX: Dp,
    modifier: Modifier = Modifier,
    initialOffsetX: Dp = targetOffsetX,
    zIndex: Float = 0f,
    width: Dp = 41.44.dp,
    height: Dp = 55.26.dp,
    launchDelayMillis: Int = 0
) {
    val cardShape = RoundedCornerShape(8.dp)
    val density = LocalDensity.current

    val launchProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (launchDelayMillis > 0) delay(launchDelayMillis.toLong().milliseconds)
        launchProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessVeryLow // Slower and smoother motion
            )
        )
    }

    val startXPx = with(density) { initialOffsetX.toPx() }
    val targetXPx = with(density) { targetOffsetX.toPx() }

    Box(
        modifier = modifier
            .zIndex(zIndex)
            .size(width = width, height = height)
            .graphicsLayer {
                val progress = launchProgress.value

                alpha = 1f
                shape = cardShape
                clip = false

                // Interpolates smoothly from rightmost position to target left position
                translationX = startXPx + (targetXPx - startXPx) * progress
                translationY = 0f
            }
            .background(color = ContentInvPrimary, shape = cardShape)
            .border(width = 1.5.dp, color = ContentInvPrimary, shape = cardShape)
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
private fun AllCardsDividerPreview() {
    AllCardsDivider()
}