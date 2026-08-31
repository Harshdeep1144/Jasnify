package com.harshdeep.jasnify.presentation.components.dialogs

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.core.view.doOnPreDraw
import kotlin.math.roundToInt

@Composable
fun EyeDropperOverlay(
    onColorPicked: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val density = LocalDensity.current

    var touchPosition by remember { mutableStateOf(Offset.Unspecified) }
    var pickedColor by remember { mutableStateOf(Color.Transparent) }
    var viewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val loupeRadiusDp = 60.dp
    val offsetYDp = 70.dp
    val offsetYPx = with(density) { offsetYDp.toPx() }

    // Capture the screen safely using PixelCopy (API 26+) or software canvas fallback
    DisposableEffect(view) {
        val captureAction = {
            captureViewToBitmap(view) { capturedBmp ->
                viewBitmap?.recycle()
                viewBitmap = capturedBmp
            }
        }

        if (view.isLaidOut && view.width > 0 && view.height > 0) {
            captureAction()
        } else {
            view.doOnPreDraw { captureAction() }
        }

        onDispose {
            viewBitmap?.recycle()
            viewBitmap = null
        }
    }

    // Accurate pixel sampling from the captured Bitmap
    val sampleColor: (Offset) -> Unit = remember(viewBitmap) {
        { targetPos ->
            val bmp = viewBitmap
            if (targetPos != Offset.Unspecified && bmp != null && !bmp.isRecycled) {
                val x = targetPos.x.roundToInt().coerceIn(0, bmp.width - 1)
                val y = targetPos.y.roundToInt().coerceIn(0, bmp.height - 1)
                pickedColor = Color(bmp.getPixel(x, y))
            }
        }
    }

    // Default loupe position at center screen once bitmap is ready
    LaunchedEffect(viewBitmap) {
        if (viewBitmap != null && touchPosition == Offset.Unspecified) {
            val initialTouch = Offset(view.width / 2f, view.height / 2f + offsetYPx)
            touchPosition = initialTouch
            sampleColor(Offset(initialTouch.x, initialTouch.y - offsetYPx))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.05f))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var currentPos = down.position
                    touchPosition = currentPos

                    sampleColor(Offset(currentPos.x, currentPos.y - offsetYPx))

                    drag(down.id) { change ->
                        change.consume()
                        currentPos = change.position
                        touchPosition = currentPos
                        sampleColor(Offset(currentPos.x, currentPos.y - offsetYPx))
                    }

                    if (pickedColor != Color.Transparent) {
                        onColorPicked(pickedColor)
                    }
                }
            }
    ) {
        if (touchPosition != Offset.Unspecified) {
            val loupeRadiusPx = with(density) { loupeRadiusDp.toPx() }
            val loupeTopLeftX = touchPosition.x - loupeRadiusPx
            val loupeTopLeftY = touchPosition.y - (loupeRadiusPx + offsetYPx)

            Box(
                modifier = Modifier.offset {
                    IntOffset(
                        loupeTopLeftX.roundToInt(),
                        loupeTopLeftY.roundToInt()
                    )
                }
            ) {
                EyeDropperToolUI(color = pickedColor)
            }
        }
    }
}

@Composable
fun EyeDropperToolUI(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ringThickness = 22.dp.toPx()
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension / 2f - ringThickness / 2f

            val displayColor = if (color == Color.Transparent) Color.LightGray else color

            // Loupe color preview ring
            drawCircle(
                color = displayColor,
                radius = outerRadius,
                center = centerOffset,
                style = Stroke(width = ringThickness)
            )

            // Outer border ring
            drawCircle(
                color = Color(0xE53D3D3D),
                radius = outerRadius + ringThickness / 2f,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )

            // Inner border ring
            drawCircle(
                color = Color(0xE53D3D3D),
                radius = outerRadius - ringThickness / 2f,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )

            // Dynamic contrast crosshair
            val reticleSize = 14.dp.toPx()
            val strokeW = 2.dp.toPx()
            val crosshairColor = if (displayColor.luminance() < 0.5f) {
                Color.White
            } else {
                Color(0xCC000000)
            }

            // Horizontal crosshair line
            drawLine(
                color = crosshairColor,
                start = Offset(centerOffset.x - reticleSize, centerOffset.y),
                end = Offset(centerOffset.x + reticleSize, centerOffset.y),
                strokeWidth = strokeW
            )

            // Vertical crosshair line
            drawLine(
                color = crosshairColor,
                start = Offset(centerOffset.x, centerOffset.y - reticleSize),
                end = Offset(centerOffset.x, centerOffset.y + reticleSize),
                strokeWidth = strokeW
            )
        }
    }
}

/**
 * Captures the target view into an ARGB_8888 software Bitmap.
 * Uses PixelCopy on API 26+ to avoid "Software rendering doesn't support hardware bitmaps" exceptions.
 */
private fun captureViewToBitmap(view: View, onBitmapReady: (Bitmap) -> Unit) {
    if (view.width <= 0 || view.height <= 0) return

    val bitmap = createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val activity = view.context.findActivity()
        val window = activity?.window

        if (window != null) {
            val location = IntArray(2)
            view.getLocationInWindow(location)

            val rect = Rect(
                location[0],
                location[1],
                location[0] + view.width,
                location[1] + view.height
            )

            PixelCopy.request(
                window,
                rect,
                bitmap,
                { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        onBitmapReady(bitmap)
                    } else {
                        bitmap.recycle()
                    }
                },
                Handler(Looper.getMainLooper())
            )
            return
        }
    }

    // Fallback for API < 26 or non-Activity contexts
    try {
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        onBitmapReady(bitmap)
    } catch (e: IllegalArgumentException) {
        bitmap.recycle()
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}