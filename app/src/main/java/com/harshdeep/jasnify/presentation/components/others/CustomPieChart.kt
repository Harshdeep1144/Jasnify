package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlin.math.cos
import kotlin.math.sin

data class PieChartSlice(
    val value: Float,
    val color: Color,
    val label: String? = null
)

@Composable
fun CustomPieChart(
    slices: List<PieChartSlice>,
    modifier: Modifier = Modifier,
    centerTextPrimary: String = "₹ 1 Cr",
    centerTextSecondary: String = "TOTAL BUDGET",
    donutThickness: Dp = 42.dp,
    gapWidthDp: Dp = 1.dp,
    outerBorderWidthDp: Dp = 1.dp,
    animationDurationMillis: Int = 1000,
    innerCircleColor: Color = SurfacePrimary
) {
    // Validation to prevent division by zero or empty data rendering
    val totalValue = remember(slices) { slices.sumOf { it.value.toDouble() }.toFloat() }

    // Animation progress state going from 0f to 1f
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(slices) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = TweenSpec(durationMillis = animationDurationMillis)
        )
    }

    Box(
        modifier = modifier.size(240.dp).background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        // Inner container with padding to allow the shadow to render without being clipped
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp) // Padding avoids clipping the soft shadow bounds
                // Multi-layered shadow design for high-fidelity soft depth
                .shadow(elevation = 13.dp, shape = CircleShape, spotColor = ContentPrimary, ambientColor = ContentPrimary)
                .shadow(elevation = 24.dp, shape = CircleShape, spotColor = ContentPrimary, ambientColor = ContentPrimary)
                .shadow(elevation = 32.dp, shape = CircleShape, spotColor = ContentPrimary, ambientColor = ContentPrimary)
                .shadow(elevation = 38.dp, shape = CircleShape, spotColor = ContentPrimary, ambientColor = ContentPrimary)
                .shadow(elevation = 42.dp, shape = CircleShape, spotColor = ContentPrimary, ambientColor = ContentPrimary)
                .background(innerCircleColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                val minSize = minOf(width, height)
                val outerRadius = minSize / 2f
                val thicknessPx = donutThickness.toPx()
                val center = Offset(width / 2f, height / 2f)

                // The drawing radius is centered on the arc stroke width
                val arcRadius = outerRadius - (thicknessPx / 2f)
                val arcSize = Size(arcRadius * 2f, arcRadius * 2f)
                val arcTopLeft = Offset(center.x - arcRadius, center.y - arcRadius)

                if (totalValue <= 0f || slices.isEmpty()) {
                    // Draw a fallback empty grey donut chart
                    drawArc(
                        color = SurfaceSecondary,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = thicknessPx, cap = StrokeCap.Butt)
                    )
                    return@Canvas
                }

                var currentAngle = -90f // Start at top 12 o'clock position
                val animatedTotalProgress = animationProgress.value

                slices.forEach { slice ->
                    val sweepAngle = (slice.value / totalValue) * 360f
                    val animatedSweepAngle = sweepAngle * animatedTotalProgress

                    // Draw the segment arc
                    drawArc(
                        color = slice.color,
                        startAngle = currentAngle,
                        sweepAngle = animatedSweepAngle,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = thicknessPx, cap = StrokeCap.Butt)
                    )

                    currentAngle += sweepAngle
                }

                // Draw a beautiful outer white border to cleanly bound the segments
                val outerBorderPx = outerBorderWidthDp.toPx()
                if (outerBorderPx > 0f) {
                    drawCircle(
                        color = Color.White,
                        radius = outerRadius - (outerBorderPx / 2f),
                        center = center,
                        style = Stroke(width = outerBorderPx)
                    )
                }

                // Draw beautiful, clean divider white strokes between slices to match design
                if (slices.size > 1) {
                    var dividerAngle = -90f
                    val dividerWidth = gapWidthDp.toPx()
                    val innerRadius = outerRadius - thicknessPx

                    slices.forEach { slice ->
                        val angleRad = Math.toRadians(dividerAngle.toDouble())
                        val cosAngle = cos(angleRad).toFloat()
                        val sinAngle = sin(angleRad).toFloat()

                        val startPoint = Offset(
                            x = center.x + innerRadius * cosAngle,
                            y = center.y + innerRadius * sinAngle
                        )
                        val endPoint = Offset(
                            x = center.x + outerRadius * cosAngle,
                            y = center.y + outerRadius * sinAngle
                        )

                        drawLine(
                            color = Color.White,
                            start = startPoint,
                            end = endPoint,
                            strokeWidth = dividerWidth
                        )

                        dividerAngle += (slice.value / totalValue) * 360f
                    }
                }

                // Drawing the recessed core (inner shadow effect inside the hole)
                val innerRadius = outerRadius - thicknessPx

                // Draw base filled circle matching the background inside the Canvas hole
                drawCircle(
                    color = innerCircleColor,
                    radius = innerRadius,
                    center = center
                )

                // Draw soft radial inner shadow gradient to make the donut look dynamically elevated
                val radialGradient = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Transparent,
                        0.75f to Color.Transparent,
                        0.88f to ContentPrimary.copy(alpha = 0.02f),
                        0.95f to ContentPrimary.copy(alpha = 0.06f),
                        1.0f to ContentPrimary.copy(alpha = 0.12f)
                    ),
                    center = center,
                    radius = innerRadius
                )

                drawCircle(
                    brush = radialGradient,
                    radius = innerRadius,
                    center = center
                )

                // Subtle high-fidelity inner white stroke to outline the premium donut interior geometry
                drawCircle(
                    color = SurfacePrimary,
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Box overlays standard composable text inside the donut hole
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(donutThickness) // Restrict boundaries inside the donut hole
            ) {
                Text(
                    text = centerTextPrimary,
                    style = JasnifyTheme.typography.displayMedium,
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = centerTextSecondary.uppercase(),
                    style = JasnifyTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = ContentSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F2)
@Composable
fun CustomPieChartPreview() {
    val blueColor = Color(0xFF2B5D95)
    val redColor = Color(0xFFFC154A)
    val pinkColor = Color(0xFFED6D8D)
    val greenColor = Color(0xFF0FAD48)
    val tealColor = Color(0xFF4495B9)
    val purpleColor = Color(0xFF8B00C7)

    // Complete breakdown corresponding to screenshot design with custom core text
    val expensiveDataset = listOf(
        PieChartSlice(50f, blueColor, "Category A"),
        PieChartSlice(25f, redColor, "Category B"),
        PieChartSlice(15f, pinkColor, "Category C"),
        PieChartSlice(8f, greenColor, "Category D"),
        PieChartSlice(1.2f, tealColor, "Category E"),
        PieChartSlice(0.8f, purpleColor, "Category F")
    )

    CustomPieChart(
        slices = expensiveDataset,
        centerTextPrimary = "₹ 46.5 L",
        centerTextSecondary = "TOTAL EXPENSES",
    )
}