package com.harshdeep.jasnify.presentation.components.animations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.*

private data class Point3D(val x: Float, val y: Float, val z: Float)

@Composable
fun VoiceOrb(
    modifier: Modifier = Modifier,
    isMuted: Boolean = false,
    isAiSpeaking: Boolean = false,
    pointCount: Int = 1100,
    userActiveColor: Color = Color(0xFF9F4D1B),
    aiActiveColor: Color = Color(0xFF006363)
) {
    // 3D Fibonacci Sphere baseline points
    val basePoints = remember(pointCount) {
        val pts = ArrayList<Point3D>(pointCount)
        val phi = Math.PI * (3.0 - sqrt(5.0))
        for (i in 0 until pointCount) {
            val y = 1f - (i / (pointCount - 1f)) * 2f
            val radiusAtY = sqrt(max(0f, 1f - y * y))
            val theta = phi * i
            val x = (cos(theta) * radiusAtY).toFloat()
            val z = (sin(theta) * radiusAtY).toFloat()
            pts.add(Point3D(x, y, z))
        }
        pts
    }

    // Dynamic Color Transition
    val targetPrimaryColor = when {
        isMuted -> Color.Gray
        isAiSpeaking -> aiActiveColor
        else -> userActiveColor
    }

    val animatedColor by animateColorAsState(
        targetValue = targetPrimaryColor,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "OrbColorTransition"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "SeamlessVoiceOrb")

    // Smooth seamless rotation angle (0 to 2PI)
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isAiSpeaking) 5000 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Smooth seamless wave progress (0 to 2PI continuous integer multiplier for seamless loop)
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isAiSpeaking) 2400 else 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    // Dynamic breathing scale and dispersion
    val targetBreathingScale = if (isAiSpeaking) 1.15f else if (isMuted) 0.85f else 1.0f
    val dynamicBreathingScale by animateFloatAsState(
        targetValue = targetBreathingScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "dynamicBreathing"
    )

    val targetDispersion = if (isAiSpeaking) 0.22f else if (isMuted) 0.04f else 0.12f
    val dynamicDispersion by animateFloatAsState(
        targetValue = targetDispersion,
        animationSpec = tween(durationMillis = 400),
        label = "dynamicDispersion"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val baseRadius = (min(width, height) / 2f) * 0.76f * dynamicBreathingScale

        val cosRot = cos(rotationAngle)
        val sinRot = sin(rotationAngle)
        val tiltAngle = 0.35f
        val cosTilt = cos(tiltAngle)
        val sinTilt = sin(tiltAngle)

        val projectedOffsets = ArrayList<Offset>(pointCount)
        val particleAlphas = FloatArray(pointCount)
        val particleSizes = FloatArray(pointCount)

        for (i in 0 until pointCount) {
            val pt = basePoints[i]

            // Multi-harmonic seamless trigonometric displacement
            val displacement = sin(pt.x * 4.0f + pt.y * 3.0f + wavePhase) * 0.5f +
                    cos(pt.z * 5.0f - pt.x * 2.0f + wavePhase * 2.0f) * 0.5f
            val r = baseRadius * (1f + (displacement * dynamicDispersion))

            val x0 = pt.x * r
            val y0 = pt.y * r
            val z0 = pt.z * r

            // Y-axis Rotation
            val x1 = x0 * cosRot + z0 * sinRot
            val z1 = -x0 * sinRot + z0 * cosRot

            // X-axis Tilt
            val y2 = y0 * cosTilt - z1 * sinTilt
            val z2 = y0 * sinTilt + z1 * cosTilt

            // 3D Perspective Projection
            val cameraDist = baseRadius * 3.5f
            val scaleFactor = cameraDist / (cameraDist + z2)

            val projX = centerX + x1 * scaleFactor
            val projY = centerY + y2 * scaleFactor

            // Depth & Rim factor
            val normalZ = z2 / baseRadius
            val rimFactor = (1f - abs(normalZ).coerceIn(0f, 1f)).pow(1.5f)
            val depthBrightness = ((z2 / baseRadius) * 0.4f + 0.6f).coerceIn(0.2f, 1f)

            val alpha = ((depthBrightness * 0.55f) + (rimFactor * 0.45f)).coerceIn(0.2f, 1f)

            projectedOffsets.add(Offset(projX, projY))
            particleAlphas[i] = alpha
            particleSizes[i] = (2.2f * scaleFactor * (1f + rimFactor * 0.7f)).coerceIn(1.2f, 5.0f)
        }

        for (i in 0 until pointCount) {
            val offset = projectedOffsets[i]
            val alpha = particleAlphas[i]
            val pointSize = particleSizes[i]

            val pColor = if (alpha > 0.75f) {
                animatedColor.copy(alpha = alpha)
            } else {
                animatedColor.copy(alpha = alpha * 0.7f)
            }

            drawCircle(
                color = pColor,
                radius = pointSize / 2f,
                center = offset
            )
        }
    }
}