package com.harshdeep.jasnify.presentation.components.sliders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraSmall
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape
import com.harshdeep.jasnify.R

/**
 * A full composite card containing the header (icon, title, reset button, value) and the interactive slider track.
 */
@Composable
fun CustomSliderCard(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    unit: String = "px",
    icon: Painter? = null,
    iconTint: Color = ContentSecondary,
    labelColor: Color = ContentSecondary,
    badgeTextColor: Color = ContentBrandDark,
    valueFormatter: ((Float) -> String)? = null,
    isAuto: Boolean = false,
    onReset: (() -> Unit)? = null,
    showReset: Boolean = true,
    trackHeight: Dp = 4.dp,
    thumbSize: Dp = 28.dp,
    verticalSpacing: Dp = 8.dp,
    containerColor: Color = SurfaceSecondary,
    shape: SquircleShape = SquircleShape(CornerExtraSmall)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        CustomSliderHeader(
            label = label,
            value = value,
            unit = unit,
            icon = icon,
            iconTint = iconTint,
            labelColor = labelColor,
            badgeTextColor = badgeTextColor,
            valueFormatter = valueFormatter,
            isAuto = isAuto,
            onReset = onReset,
            showReset = showReset
        )

        Box(
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            CustomSlider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                trackHeight = trackHeight,
                thumbSize = thumbSize,
                modifier = Modifier.height(44.dp)
            )
        }
    }
}

/**
 * Standalone Header component containing Icon, Label, Reset Button, and Value Badge display.
 */
@Composable
fun CustomSliderHeader(
    label: String,
    value: Float,
    modifier: Modifier = Modifier,
    unit: String = "px",
    icon: Painter? = null,
    iconTint: Color = ContentSecondary,
    labelColor: Color = ContentSecondary,
    badgeTextColor: Color = ContentBrandDark,
    valueFormatter: ((Float) -> String)? = null,
    isAuto: Boolean = false,
    onReset: (() -> Unit)? = null,
    showReset: Boolean = true
) {
    val formattedValue = when {
        isAuto -> "Auto"
        valueFormatter != null -> valueFormatter(value)
        else -> "${value.toInt()} $unit"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            }
            Text(
                text = label,
                color = labelColor,
                style = JasnifyTheme.typography.headingMedium
            )
        }


        // Reset Button placed directly after the label text
        Row {
            if (onReset != null && showReset) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onReset() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_reset),
                        contentDescription = "Reset $label to last saved value",
                        tint = ContentBrandDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = formattedValue,
                color = badgeTextColor,
                style = JasnifyTheme.typography.headingMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Base custom slider component handling drag/tap gestures and custom canvas track drawing.
 */
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    trackHeight: Dp = 4.dp,
    thumbSize: Dp = 28.dp
) {
    var trackWidthPx by remember { mutableFloatStateOf(0f) }

    val normalizedValue = if (valueRange.endInclusive == valueRange.start) 0f
    else ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { (thumbSize / 2).toPx() }

    fun updateValueFromX(xPx: Float) {
        if (trackWidthPx > thumbRadiusPx * 2) {
            val newNormalized = ((xPx - thumbRadiusPx) / (trackWidthPx - 2 * thumbRadiusPx)).coerceIn(0f, 1f)
            val newValue = valueRange.start + newNormalized * (valueRange.endInclusive - valueRange.start)
            onValueChange(newValue)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize)
            .onGloballyPositioned { coordinates ->
                trackWidthPx = coordinates.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    updateValueFromX(offset.x)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    updateValueFromX(change.position.x)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
        ) {
            val cornerRadius = CornerRadius(size.height / 2f)

            // Inactive track
            drawRoundRect(
                color = Color.Black.copy(0.1f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                cornerRadius = cornerRadius
            )

            // Active track
            val activeWidth = size.width * normalizedValue
            drawRoundRect(
                color = ContentBrandDark,
                topLeft = Offset(0f, 0f),
                size = Size(activeWidth, size.height),
                cornerRadius = cornerRadius
            )
        }

        // Thumb Box
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = ((trackWidthPx - 2 * thumbRadiusPx) * normalizedValue).toInt(),
                        y = 0
                    )
                }
                .size(thumbSize)
                .shadow(elevation = 5.dp, shape = CircleShape)
                .background(ContentInvPrimary, CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomSliderCardPreview() {
    var sliderValue by remember { mutableFloatStateOf(40f) }

    Box(modifier = Modifier.padding(16.dp)) {
        CustomSliderCard(
            label = "Font Size",
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..100f,
            unit = "px",
            icon = rememberVectorPainter(Icons.Default.FormatSize),
            onReset = { sliderValue = 40f }
        )
    }
}