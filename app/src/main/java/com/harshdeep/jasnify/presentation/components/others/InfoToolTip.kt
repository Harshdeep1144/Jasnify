package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * A custom shape that constructs a speech-bubble box pointing downwards to form a tooltip look.
 * Designed to cleanly handle offsets to center the arrow on the triggering action button.
 */
class TooltipShape(
    private val cornerRadius: Dp = 8.dp,
    private val arrowWidth: Dp = 12.dp,
    private val arrowHeight: Dp = 8.dp,
    private val arrowOffsetPx: Float
) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val arrowWidthPx = with(density) { arrowWidth.toPx() }
        val arrowHeightPx = with(density) { arrowHeight.toPx() }

        val path = Path().apply {
            val width = size.width
            val height = size.height
            val rectHeight = height - arrowHeightPx

            // Top-Left to Top-Right
            moveTo(cornerRadiusPx, 0f)
            lineTo(width - cornerRadiusPx, 0f)
            quadraticBezierTo(width, 0f, width, cornerRadiusPx)

            // Top-Right to Bottom-Right
            lineTo(width, rectHeight - cornerRadiusPx)
            quadraticBezierTo(width, rectHeight, width - cornerRadiusPx, rectHeight)

            // Bottom-Right Arrow calculation logic
            val arrowRight = width - arrowOffsetPx + (arrowWidthPx / 2f)
            val arrowLeft = width - arrowOffsetPx - (arrowWidthPx / 2f)
            val arrowTip = width - arrowOffsetPx

            // Clip arrow bounds slightly to match layout container limits safely
            val arrowR = arrowRight.coerceIn(cornerRadiusPx, width - cornerRadiusPx)
            val arrowL = arrowLeft.coerceIn(cornerRadiusPx, width - cornerRadiusPx)
            val arrowT = arrowTip.coerceIn(cornerRadiusPx, width - cornerRadiusPx)

            // Draw arrow pointing downwards
            lineTo(arrowR, rectHeight)
            lineTo(arrowT, height)
            lineTo(arrowL, rectHeight)

            // Bottom-Right to Bottom-Left
            lineTo(cornerRadiusPx, rectHeight)
            quadraticBezierTo(0f, rectHeight, 0f, rectHeight - cornerRadiusPx)

            // Bottom-Left to Top-Left
            lineTo(0f, cornerRadiusPx)
            quadraticBezierTo(0f, 0f, cornerRadiusPx, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Position provider ensuring popups are aligned correctly over standard action icons.
 * Calculates exact arrow layout displacement dynamically.
 */
class TooltipPositionProvider(
    private val density: Density,
    private val tooltipSpacing: Dp = 0.dp,
    private val onArrowOffsetCalculated: (Float) -> Unit
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val spacingPx = with(density) { tooltipSpacing.toPx() }.toInt()
        val minX = with(density) { 12.dp.toPx() }.toInt()
        val maxX = windowSize.width - popupContentSize.width - minX

        val iconCenter = anchorBounds.left + anchorBounds.width / 2

        // Target layout: align popover right-side nicely over the trigger button
        val preferredArrowOffset = with(density) { 24.dp.toPx() }
        val rawX = (iconCenter + preferredArrowOffset - popupContentSize.width).toInt()

        // Clamp bounds to prevent tooltip layout scaling beyond screen margins
        val x = rawX.coerceIn(minX, maxX)
        val y = anchorBounds.top - popupContentSize.height - spacingPx

        // Compute displacement inside bounds to guide arrow center perfectly over trigger icon
        val popupRight = x + popupContentSize.width
        val actualOffset = (popupRight - iconCenter).toFloat().coerceIn(16f, popupContentSize.width - 16f)
        onArrowOffsetCalculated(actualOffset)

        return IntOffset(x, y)
    }
}


@Composable
fun TooltipBubble(
    tooltipText: String,
    arrowOffsetPx: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ContentBrand,
    textColor: Color = ContentInvPrimary
) {
    Box(
        modifier = modifier
            .widthIn(max = 240.dp)
            .shadow(
                elevation = 6.dp,
                shape = TooltipShape(arrowOffsetPx = arrowOffsetPx)
            )
            .clip(TooltipShape(arrowOffsetPx = arrowOffsetPx))
            .background(backgroundColor)
            .padding(start = 12.dp, top = 10.dp, end = 12.dp, bottom = 18.dp)
    ) {
        Text(
            text = tooltipText,
            style = JasnifyTheme.typography.labelMedium,
            color = textColor,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun InfoTooltip(
    visible: Boolean,
    tooltipText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ContentBrand,
    textColor: Color = ContentInvPrimary,
    autoDismissDelayMillis: Long = 2500L
) {
    if (!visible) return

    val density = LocalDensity.current
    var calculatedArrowOffsetPx by remember { mutableStateOf(0f) }

    // Automatically dismiss tooltip after specified duration
    LaunchedEffect(visible) {
        if (visible) {
            delay(autoDismissDelayMillis.milliseconds)
            onDismiss()
        }
    }

    Popup(
        popupPositionProvider = TooltipPositionProvider(
            density = density,
            tooltipSpacing = (-2).dp, // Negative offset positions arrow snugly against button edge
            onArrowOffsetCalculated = { calculatedArrowOffsetPx = it }
        ),
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        TooltipBubble(
            tooltipText = tooltipText,
            arrowOffsetPx = calculatedArrowOffsetPx,
            modifier = modifier,
            backgroundColor = backgroundColor,
            textColor = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoTooltipPreview() {
    val density = LocalDensity.current
    val mockArrowOffset = with(density) { 32.dp.toPx() } // Pre-calculated offset for preview rendering

    JasnifyTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            TooltipBubble(
                tooltipText = "Event type can't be changed",
                arrowOffsetPx = mockArrowOffset
            )
        }
    }
}