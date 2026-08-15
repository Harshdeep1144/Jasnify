package com.harshdeep.jasnify.presentation.components.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape
import kotlin.math.*
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.TextElement
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentPrimary

@Composable
fun ColorPickerWheel(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    var hsv by remember {
        val hsvArr = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsvArr)
        mutableStateOf(Triple(hsvArr[0], hsvArr[1], hsvArr[2]))
    }

    val currentColor = remember(hsv) {
        Color.hsv(hsv.first, hsv.second, hsv.third)
    }

    val presetColors = remember {
        listOf(
            Color(0xFFFFFFFF), Color(0xFFE5E5E5), Color(0xFF9E9E9E), Color(0xFF8C3B2B), Color(0xFF4A0000), Color(0xFF4CAF50),
            Color(0xFF2E7D32), Color(0xFF3F51B5), Color(0xFF4FC3F7), Color(0xFF90CAF9), Color(0xFF0D1117), Color(0xFFCE93D8),
            Color(0xFFBA68C8), Color(0xFF9C27B0), Color(0xFF7B1FA2)
        )
    }

    Surface(
        modifier = Modifier
            .width(360.dp)
            .wrapContentHeight(),
        shape = SquircleShape(CornerExtraLarge),
        color = SurfacePrimary
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    icon = TopIcon.Predefined.CLOSE,
                    onClick = onDismiss,
                    backgroundStyle = ButtonBackground.OPAQUE,
                    iconSize = 18.dp
                )

                CustomIconButton(
                    onClick = {
                        onColorSelected(currentColor)
                        onDismiss()
                    },
                    icon = painterResource(R.drawable.ic_check),
                    contentColor = SurfacePrimary,
                    containerColor = ContentPrimary,
                    size = ButtonSize.Small,
                    modifier = Modifier
                        .width(56.dp)
                        .height(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Color Picker Wheel Area
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                HueRing(
                    hue = hsv.first,
                    onHueChange = { newHue ->
                        hsv = Triple(newHue, hsv.second, hsv.third)
                    }
                )

                SaturationValuePicker(
                    hue = hsv.first,
                    saturation = hsv.second,
                    value = hsv.third,
                    onSVChange = { s, v ->
                        hsv = Triple(hsv.first, s, v)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Preset Swatches
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
            ) {
                items(presetColors) { color ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.dp, Color(0x26000000), CircleShape)
                            .clickable {
                                val hsvArr = FloatArray(3)
                                android.graphics.Color.colorToHSV(color.toArgb(), hsvArr)
                                hsv = Triple(hsvArr[0], hsvArr[1], hsvArr[2])
                            }
                    )


                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HueRing(
    hue: Float,
    onHueChange: (Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val angle = atan2(
                        change.position.y - size.height / 2f,
                        change.position.x - size.width / 2f
                    ) * 180 / PI.toFloat()
                    val newHue = (angle + 360) % 360
                    onHueChange(newHue)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val angle = atan2(
                        offset.y - size.height / 2f,
                        offset.x - size.width / 2f
                    ) * 180 / PI.toFloat()
                    val newHue = (angle + 360) % 360
                    onHueChange(newHue)
                }
            }
    ) {
        val radius = size.minDimension / 2
        val strokeWidth = 30.dp.toPx()
        val ringRadius = radius - strokeWidth / 2

        val sweepGradient = Brush.sweepGradient(
            colors = listOf(
                Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
            )
        )

        drawCircle(
            brush = sweepGradient,
            radius = ringRadius,
            style = Stroke(width = strokeWidth)
        )

        // Handle
        val angleRad = (hue * PI / 180).toFloat()
        val handleX = center.x + ringRadius * cos(angleRad)
        val handleY = center.y + ringRadius * sin(angleRad)

        drawCircle(
            color = Color.Black.copy(alpha = 0.15f),
            radius = 18.dp.toPx(),
            center = Offset(handleX, handleY + 2.dp.toPx())
        )

        drawCircle(
            color = Color.White,
            radius = 16.dp.toPx(),
            center = Offset(handleX, handleY)
        )
        drawCircle(
            color = Color.hsv(hue, 1f, 1f),
            radius = 12.dp.toPx(),
            center = Offset(handleX, handleY)
        )
    }
}

@Composable
fun SaturationValuePicker(
    hue: Float,
    saturation: Float,
    value: Float,
    onSVChange: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .size(175.69.dp)
            .clip(CircleShape)
            .pointerInput(hue) {
                detectDragGestures { change, _ ->
                    val r = size.width / 2f
                    val x = change.position.x - r
                    val y = change.position.y - r

                    val dist = sqrt(x * x + y * y)
                    val constrainedX = if (dist > r) x * (r / dist) else x
                    val constrainedY = if (dist > r) y * (r / dist) else y

                    val s = ((constrainedX + r) / (2 * r)).coerceIn(0f, 1f)
                    val v = (1f - (constrainedY + r) / (2 * r)).coerceIn(0f, 1f)
                    onSVChange(s, v)
                }
            }
            .pointerInput(hue) {
                detectTapGestures { offset ->
                    val r = size.width / 2f
                    val x = offset.x - r
                    val y = offset.y - r

                    val s = ((x + r) / (2 * r)).coerceIn(0f, 1f)
                    val v = (1f - (y + r) / (2 * r)).coerceIn(0f, 1f)
                    onSVChange(s, v)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val hueColor = Color.hsv(hue, 1f, 1f)

            drawRect(Color.White)
            drawRect(brush = Brush.horizontalGradient(listOf(Color.White, hueColor)))
            drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
        }

        val handleX = (saturation * 150.dp.value)
        val handleY = ((1f - value) * 150.dp.value)

        Box(
            modifier = Modifier
                .offset(x = handleX.dp - 12.dp, y = handleY.dp - 12.dp)
                .size(24.dp)
                .shadow(4.dp, CircleShape)
                .border(2.5.dp, Color.White, CircleShape)
                .background(Color.Transparent, CircleShape)
        )
    }
}