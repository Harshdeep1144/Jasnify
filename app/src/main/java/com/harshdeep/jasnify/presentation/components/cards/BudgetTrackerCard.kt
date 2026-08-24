package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

val ProgressColor = ContentBrandDark.copy(alpha = 0.8f)
val TrackColor = ContentTertiary

@Composable
fun BudgetTrackerCard(
    modifier: Modifier = Modifier,
    insight: String? = null,
    heading: String? = null,
    illustration: Painter? = null,
    progress: Float,
    amountText: String,
    labelText: String? = null,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "BudgetTrackerCardScaleAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minWidth = 180.dp, minHeight = 172.dp)
            .height(172.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
            .border(
                width = 1.dp,
                color = Color(0x33006363),
                shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault)
            )
            .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
            .noRippleClickable { onClick() },
        shape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFBED4D4),
            contentColor = ContentPrimary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Transparent)
            ) {
                Image(
                    painter = painterResource(R.drawable.bg_wave),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "wave background",
                    alignment = Alignment.BottomCenter,
                    colorFilter = ColorFilter.tint(Color(0x1A006363).copy(alpha = 0.9f)),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = heading ?: "Budget Tracker",
                    color = ContentPrimary,
                    style = JasnifyTheme.typography.headingLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insight ?: "insights",
                    color = Color(0xFF006363),
                    style = JasnifyTheme.typography.labelSmall
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressChart(
                            progress = progress,
                            amountText = amountText,
                            labelText = labelText ?: "Left"
                        )
                    }

                    Box(
                        modifier = Modifier.padding(0.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Image(
                            painter = illustration ?: painterResource(R.drawable.ill_budget_tracker_card),
                            contentDescription = "Card Illustration",
                            modifier = Modifier.height(80.dp),
                            contentScale = ContentScale.FillHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularProgressChart(
    progress: Float,
    amountText: String,
    labelText: String,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp,
    strokeWidth: Dp = 12.dp,
    durationMillis: Int = 2000,
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis)
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val strokeStyle = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Butt
            )

            drawArc(
                color = TrackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = strokeStyle
            )

            drawArc(
                color = ProgressColor,
                startAngle = 270f,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                style = strokeStyle
            )
        }

        Column(
            modifier = Modifier.size(56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = amountText,
                color = Color(0xFF006363),
                style = JasnifyTheme.typography.labelLarge
            )
            Text(
                text = labelText,
                color = Color(0xFF006363),
                style = JasnifyTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCircularProgressChart() {
    Column(modifier = Modifier.padding(12.dp)) {
        BudgetTrackerCard(
            insight = "See your budget",
            heading = "Budget Tracker",
            illustration = painterResource(R.drawable.ill_budget_tracker_card),
            progress = 0.45f,
            amountText = "₹46L",
            labelText = "left",
            onClick = {}
        )
    }
}