package com.harshdeep.jasnify.presentation.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentTertiary

/**
 * Disables the default ripple effect when clicking a component.
 */
@Composable
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = enabled,
    onClick = onClick
)

/**
 * Disables the default ripple effect when combined clicking a component.
 */
@Composable
fun Modifier.noRippleCombinedClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
): Modifier = this.combinedClickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = enabled,
    onClick = onClick,
    onLongClick = onLongClick
)


/**
 * Custom 360-degree drop shadow modifier.
 * Native Android `Modifier.shadow` uses a top-down light source that only casts shadows downward.
 * This custom draw method renders both an ambient halo (all 4 sides) and a directional spot shadow.
 */
fun Modifier.pill360Shadow(
    ambientColor: Color = Color.Black.copy(alpha = 0.08f),
    ambientBlur: Dp = 12.dp,
    ambientSpread: Dp = 0.dp,
    spotColor: Color = Color.Black.copy(alpha = 0.14f),
    spotBlur: Dp = 16.dp,
    spotOffsetY: Dp = 4.dp
) = this.drawWithCache {
    val ambientPaint = Paint()
    val frameworkAmbientPaint = ambientPaint.asFrameworkPaint()
    if (ambientBlur.toPx() > 0) {
        frameworkAmbientPaint.maskFilter = android.graphics.BlurMaskFilter(
            ambientBlur.toPx(),
            android.graphics.BlurMaskFilter.Blur.NORMAL
        )
    }
    frameworkAmbientPaint.color = ambientColor.toArgb()

    val spotPaint = Paint()
    val frameworkSpotPaint = spotPaint.asFrameworkPaint()
    if (spotBlur.toPx() > 0) {
        frameworkSpotPaint.maskFilter = android.graphics.BlurMaskFilter(
            spotBlur.toPx(),
            android.graphics.BlurMaskFilter.Blur.NORMAL
        )
    }
    frameworkSpotPaint.color = spotColor.toArgb()

    onDrawBehind {
        drawIntoCanvas { canvas ->
            val cornerRadius = size.height / 2f

            // 1. Ambient 360-degree halo shadow
            canvas.drawRoundRect(
                left = -ambientSpread.toPx(),
                top = -ambientSpread.toPx(),
                right = size.width + ambientSpread.toPx(),
                bottom = size.height + ambientSpread.toPx(),
                radiusX = cornerRadius,
                radiusY = cornerRadius,
                paint = ambientPaint
            )

            // 2. Directional spot shadow
            canvas.drawRoundRect(
                left = 0f,
                top = spotOffsetY.toPx(),
                right = size.width,
                bottom = size.height + spotOffsetY.toPx(),
                radiusX = cornerRadius,
                radiusY = cornerRadius,
                paint = spotPaint
            )
        }
    }
}



fun Modifier.drawScrollbar(
    scrollState: ScrollState,
    width: Dp = 4.dp,
    trackColor: Color = Color.Transparent,
    thumbColor: Color = ContentTertiary,
    paddingRight: Dp = 0.dp
): Modifier = this.drawWithContent {
    drawContent()

    val maxValue = scrollState.maxValue
    // Do not draw scrollbar if content fits without scrolling or is unconstrained
    if (maxValue <= 0 || maxValue == Int.MAX_VALUE) return@drawWithContent

    val viewportHeight = size.height
    if (viewportHeight <= 0f) return@drawWithContent

    val totalContentHeight = viewportHeight + maxValue

    // Calculate thumb height ratio
    val visibleRatio = viewportHeight / totalContentHeight
    val minThumbHeight = 32.dp.toPx()
    // Cap thumb height so it never fills the entire viewport when scrollable
    val maxThumbHeight = viewportHeight * 0.8f
    val thumbHeight = (viewportHeight * visibleRatio).coerceIn(minThumbHeight, maxThumbHeight)

    // Calculate scroll thumb offset
    val scrollProgress = (scrollState.value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    val thumbOffsetY = scrollProgress * (viewportHeight - thumbHeight)

    val widthPx = width.toPx()
    val cornerRadiusPx = widthPx / 2f
    val xOffset = size.width - widthPx - paddingRight.toPx()

    // 1. Draw Track
    drawRoundRect(
        color = trackColor,
        topLeft = Offset(xOffset, 0f),
        size = Size(widthPx, viewportHeight),
        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
    )

    // 2. Draw Thumb
    drawRoundRect(
        color = thumbColor,
        topLeft = Offset(xOffset, thumbOffsetY),
        size = Size(widthPx, thumbHeight),
        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
    )
}

fun Modifier.dashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round
) = this.drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    val dashLengthPx = dashLength.toPx()
    val gapLengthPx = gapLength.toPx()

    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path()

    when (outline) {
        is Outline.Rectangle -> path.addRect(outline.rect)
        is Outline.Rounded -> path.addRoundRect(outline.roundRect)
        is Outline.Generic -> path.addPath(outline.path)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidthPx,
            cap = cap,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashLengthPx, gapLengthPx),
                phase = 0f
            )
        )
    )
}