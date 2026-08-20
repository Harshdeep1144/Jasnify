package com.harshdeep.jasnify.presentation.components.animations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.*

private class Point3D(val x: Float, val y: Float, val z: Float)

@Composable
fun VoiceOrb(
    modifier: Modifier = Modifier,
    isMuted: Boolean = false,
    isAiSpeaking: Boolean = false,
    audioRms: Float = 0f,
    pointCount: Int = 1800,
    userActiveColor: Color = Color(0xFF9F4D1B),
    aiActiveColor: Color = Color(0xFF006363)
) {
    val basePoints = remember(pointCount) {
        val pts = Array(pointCount) { Point3D(0f, 0f, 0f) }
        val phi = Math.PI * (3.0 - sqrt(5.0))
        for (i in 0 until pointCount) {
            val linearY = 1f - (i / (pointCount - 1f)) * 2f
            val y = sign(linearY) * abs(linearY).pow(1.35f)
            val radiusAtY = sqrt(max(0f, 1f - y * y))
            val theta = phi * i
            val x = (cos(theta) * radiusAtY).toFloat()
            val z = (sin(theta) * radiusAtY).toFloat()
            pts[i] = Point3D(x, y, z)
        }
        pts
    }

    val targetPrimaryColor = when {
        isAiSpeaking -> aiActiveColor
        isMuted -> Color.Black
        else -> userActiveColor
    }

    val animatedColor by animateColorAsState(
        targetValue = targetPrimaryColor,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "OrbColorTransition"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "SeamlessVoiceOrb")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isAiSpeaking) 5000 else 7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isAiSpeaking) 2000 else 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    // Responsive spring damping for smooth tracking with lively response
    val smoothedRms by animateFloatAsState(
        targetValue = if (isMuted) 0f else audioRms,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "smoothedRms"
    )

    // Noticeable 20% max sphere bounce/expansion
    val targetScale = when {
        isAiSpeaking -> 1.05f
        !isMuted -> 1.0f + (smoothedRms * 0.20f)
        else -> 1.0f
    }

    // Well-balanced fluid ripple depth
    val targetDispersion = when {
        isAiSpeaking -> 0.08f
        isMuted -> 0.02f
        else -> 0.04f + (smoothedRms * 0.18f)
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val baseRadius = (min(width, height) / 2f) * 0.72f * targetScale

        val cosRot = cos(rotationAngle)
        val sinRot = sin(rotationAngle)
        val tiltAngle = 0.35f
        val cosTilt = cos(tiltAngle)
        val sinTilt = sin(tiltAngle)

        val cameraDist = baseRadius * 3.8f

        for (i in 0 until pointCount) {
            val pt = basePoints[i]

            // Balanced wave harmonics: visible rolling fluid swells without tearing
            val wave1 = sin(pt.x * 2.0f + pt.y * 1.8f + wavePhase)
            val wave2 = cos(pt.z * 2.2f - pt.x * 1.5f + wavePhase * 1.2f)
            val displacement = (wave1 * 0.6f + wave2 * 0.4f) * targetDispersion

            val r = baseRadius * (1f + displacement)

            val x0 = pt.x * r
            val y0 = pt.y * r
            val z0 = pt.z * r

            // Y-axis rotation
            val x1 = x0 * cosRot + z0 * sinRot
            val z1 = -x0 * sinRot + z0 * cosRot

            // Tilt
            val y2 = y0 * cosTilt - z1 * sinTilt
            val z2 = y0 * sinTilt + z1 * cosTilt

            val scaleFactor = cameraDist / (cameraDist + z2)
            val projX = centerX + x1 * scaleFactor
            val projY = centerY + y2 * scaleFactor

            val dx = projX - centerX
            val dy = projY - centerY
            val dist2D = sqrt(dx * dx + dy * dy)
            val radialFactor = (dist2D / baseRadius).coerceIn(0f, 1f)

            val depthFactor = ((z2 / baseRadius) * 0.35f + 0.65f).coerceIn(0.35f, 1f)
            val alpha = ((0.35f + 0.60f * radialFactor.pow(1.2f)) * depthFactor).coerceIn(0.15f, 1f)

            val voicePointSizeBoost = if (!isAiSpeaking) smoothedRms * 1.4f else 0.4f
            val pointRadius = (((2.2f + voicePointSizeBoost) * scaleFactor * (0.85f + 0.35f * radialFactor)) / 2f).coerceIn(0.8f, 4.8f)

            drawCircle(
                color = animatedColor.copy(alpha = alpha),
                radius = pointRadius,
                center = Offset(projX, projY)
            )
        }
    }
}