package com.harshdeep.jasnify.presentation.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val cornerRadius = size.height / 2f

        // 1. Ambient 360-degree halo shadow (casts evenly on top, bottom, left, right)
        val ambientPaint = Paint()
        val frameworkAmbientPaint = ambientPaint.asFrameworkPaint()
        if (ambientBlur.toPx() > 0) {
            frameworkAmbientPaint.maskFilter = android.graphics.BlurMaskFilter(
                ambientBlur.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }
        frameworkAmbientPaint.color = ambientColor.toArgb()

        canvas.drawRoundRect(
            left = -ambientSpread.toPx(),
            top = -ambientSpread.toPx(),
            right = size.width + ambientSpread.toPx(),
            bottom = size.height + ambientSpread.toPx(),
            radiusX = cornerRadius,
            radiusY = cornerRadius,
            paint = ambientPaint
        )

        // 2. Directional spot shadow (adds downward depth)
        val spotPaint = Paint()
        val frameworkSpotPaint = spotPaint.asFrameworkPaint()
        if (spotBlur.toPx() > 0) {
            frameworkSpotPaint.maskFilter = android.graphics.BlurMaskFilter(
                spotBlur.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }
        frameworkSpotPaint.color = spotColor.toArgb()

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
