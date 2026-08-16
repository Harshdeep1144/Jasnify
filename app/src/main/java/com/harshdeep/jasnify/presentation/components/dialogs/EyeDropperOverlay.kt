package com.harshdeep.jasnify.presentation.components.dialogs

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import kotlin.math.roundToInt

@Composable
fun EyeDropperOverlay(
    onColorPicked: (Color) -> Unit,
) {
    val view = LocalView.current
    val density = LocalDensity.current

    // Position of touch
    var touchPosition by remember { mutableStateOf(Offset.Unspecified) }
    var pickedColor by remember { mutableStateOf(Color.Transparent) }

    // Elevation of the eyedropper loupe above the thumb
    val offsetYDp = 70.dp
    val offsetYPx = with(density) { offsetYDp.toPx() }

    // Capture off-screen bitmap preview of the entire view canvas
    val viewBitmap = remember(view) {
        val width = view.width.coerceAtLeast(1)
        val height = view.height.coerceAtLeast(1)
        val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        bitmap
    }

    DisposableEffect(Unit) {
        onDispose {
            if (!viewBitmap.isRecycled) {
                viewBitmap.recycle()
            }
        }
    }

    // Accurate Bitmap pixel sampling at the exact crosshair position
    val sampleColor = remember(viewBitmap) {
        { crosshairPos: Offset ->
            if (crosshairPos != Offset.Unspecified && !viewBitmap.isRecycled) {
                val x = crosshairPos.x.roundToInt().coerceIn(0, viewBitmap.width - 1)
                val y = crosshairPos.y.roundToInt().coerceIn(0, viewBitmap.height - 1)

                val pixel = viewBitmap.getPixel(x, y)
                pickedColor = Color(pixel)
            }
        }
    }

    // Initialize sampling at center of screen
    LaunchedEffect(Unit) {
        val initialTouch = Offset(view.width / 2f, view.height / 2f + offsetYPx)
        touchPosition = initialTouch
        sampleColor(Offset(initialTouch.x, initialTouch.y - offsetYPx))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.05f))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var currentPos = down.position
                    touchPosition = currentPos

                    // Sample precisely where the '+' crosshair is pointing
                    sampleColor(Offset(currentPos.x, currentPos.y - offsetYPx))

                    drag(down.id) { change ->
                        change.consume()
                        currentPos = change.position
                        touchPosition = currentPos
                        sampleColor(Offset(currentPos.x, currentPos.y - offsetYPx))
                    }

                    // Automatically apply selected color when finger leaves the screen
                    if (pickedColor != Color.Transparent) {
                        onColorPicked(pickedColor)
                    }
                }
            }
    ) {
        // Render Eyedropper Donut UI Floating Loupe
        if (touchPosition != Offset.Unspecified) {
            val offsetXPx = touchPosition.x - with(density) { 60.dp.toPx() }
            val loupeTopYPx = touchPosition.y - with(density) { (60.dp + offsetYDp).toPx() }

            Box(
                modifier = Modifier.offset {
                    IntOffset(
                        offsetXPx.roundToInt(),
                        loupeTopYPx.roundToInt()
                    )
                }
            ) {
                EyeDropperToolUI(color = pickedColor)
            }
        }
    }
}

@Composable
fun EyeDropperToolUI(color: Color) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ringThickness = 22.dp.toPx()
            val centerOffset = Offset(
                size.width / 2f,
                size.height / 2f
            )

            val outerRadius =
                size.minDimension / 2f - ringThickness / 2f

            val displayColor =
                if (color == Color.Transparent) {
                    Color.LightGray
                } else {
                    color
                }

            // Donut
            drawCircle(
                color = displayColor,
                radius = outerRadius,
                center = centerOffset,
                style = Stroke(width = ringThickness)
            )

            // OUTER border
            drawCircle(
                color = Color(0xE53D3D3D),
                radius = outerRadius + ringThickness / 2f,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )

            // INNER border
            drawCircle(
                color = Color(0xE53D3D3D),
                radius = outerRadius - ringThickness / 2f,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )

            // Target Crosshair (+)
            val reticleSize = 14.dp.toPx()
            val strokeW = 2.dp.toPx()

            val crosshairColor =
                if (displayColor.luminance() < 0.5f) {
                    Color.White
                } else {
                    Color(0xCC000000)
                }

            drawLine(
                color = crosshairColor,
                start = Offset(
                    centerOffset.x - reticleSize,
                    centerOffset.y
                ),
                end = Offset(
                    centerOffset.x + reticleSize,
                    centerOffset.y
                ),
                strokeWidth = strokeW
            )

            drawLine(
                color = crosshairColor,
                start = Offset(
                    centerOffset.x,
                    centerOffset.y + reticleSize
                ),
                end = Offset(
                    centerOffset.x,
                    centerOffset.y - reticleSize
                ),
                strokeWidth = strokeW
            )
        }
    }
}