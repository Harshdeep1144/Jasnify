package com.harshdeep.jasnify.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

val BottomGradientBrush = Brush.verticalGradient(
    colorStops = Array(16) { index ->
        val fraction = index / 15f
        // Smoothstep interpolation formula: fading from transparent at top to white at bottom
        val easedAlpha = fraction * fraction * (3f - 2f * fraction)
        val alpha = easedAlpha * 0.95f

        fraction to Color.White.copy(alpha = alpha)
    }
)

val TopGradientBrush = Brush.verticalGradient(
    colorStops = Array(16) { index ->
        val fraction = index / 15f
        val x = 1f - fraction
        // Smoothstep interpolation formula: fading from black at top to transparent at bottom
        val easedAlpha = x * x * (3f - 2f * x)
        val alpha = easedAlpha * 0.95f

        fraction to Color.Black.copy(alpha = alpha)
    }
)

val TopGradientBrushLightTheme = Brush.verticalGradient(
    colorStops = Array(16) { index ->
        val fraction = index / 15f
        val x = 1f - fraction
        // Smoothstep interpolation formula: fading from black at top to transparent at bottom
        val easedAlpha = x * x * (3f - 2f * x)
        val alpha = easedAlpha * 0.95f

        fraction to Color.White.copy(alpha = alpha)
    }
)

val TopBrandGradientBrush = Brush.verticalGradient(
    colorStops = Array(16) { index ->
        val fraction = index / 15f
        val x = 1f - fraction

        // Smooth fade from #AFCCCC80 at the top to transparent at the bottom
        val easedAlpha = x * x * (3f - 2f * x)
        val alpha = easedAlpha * 0.5f

        fraction to Color(0xFFAFCCCC).copy(alpha = alpha)
    }
)