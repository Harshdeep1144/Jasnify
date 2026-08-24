package com.harshdeep.jasnify.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

/**
 * Standardized duration constants (in milliseconds) across the application.
 */
object MotionConstants {
    const val DURATION_FAST = 150
    const val DURATION_DEFAULT = 200
    const val DURATION_SLOW = 300
}

/**
 * Pre-configured ContentTransform instances for navigation transitions.
 */
object ScreenTransitions {

    val smoothDepthEasing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)

    // Zoom Depth Forward (Home -> Detail)
    val ZoomDepthForwardTransition: ContentTransform =
        (scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 100,
                easing = smoothDepthEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 100,
                easing = smoothDepthEasing
            )
        )).togetherWith(
            scaleOut(
                targetScale = 1.1f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = smoothDepthEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = smoothDepthEasing
                )
            )
        )

    // Zoom Depth Return (Detail -> Home)
    val ZoomDepthReturnTransition: ContentTransform =
        (scaleIn(
            initialScale = 1.1f,
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 80,
                easing = smoothDepthEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 80,
                easing = smoothDepthEasing
            )
        )).togetherWith(
            scaleOut(
                targetScale = 0.9f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = smoothDepthEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = smoothDepthEasing
                )
            )
        )

// =========================================================================
//  STATIC BACKGROUND TRANSITIONS (Previous screen DOES NOT change position)
// =========================================================================


    // Slide in from bottom-to-top (Fast - 250ms)
    val SlideBottomToTopFastTransition: ContentTransform =
        (slideInVertically(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
            initialOffsetY = { fullHeight -> fullHeight }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))).togetherWith(
            fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
        ).apply {
            targetContentZIndex = 1f
        }

    // Slide out top-to-bottom (Fast - 250ms)
    val SlideTopToBottomFastTransition: ContentTransform =
        fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST)).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
        ).apply {
            targetContentZIndex = 0f
        }

    // Slide out top-to-bottom (Slow - 500ms)
    val SlideTopToBottomSlowTransition: ContentTransform =
        fadeIn(animationSpec = tween(MotionConstants.DURATION_SLOW)).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_SLOW),
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_SLOW))
        ).apply {
            targetContentZIndex = 0f
        }


    // Standard Fade In & Out (Default - 300ms)
    val FadeInOutDefaultTransition: ContentTransform =
        fadeIn(animationSpec = tween(MotionConstants.DURATION_DEFAULT)).togetherWith(
            fadeOut(animationSpec = tween(MotionConstants.DURATION_DEFAULT))
        ).apply {
            targetContentZIndex = 0f
        }

// =========================================================================
//   HORIZONTAL SLIDE TRANSITIONS (For Tab Switches)
// =========================================================================

    // Slide in from Right to Left
    val SlideInFromRightTransition = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(MotionConstants.DURATION_FAST)
    ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))

    val SlideOutToLeftTransition = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(MotionConstants.DURATION_FAST)
    ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))

    // Slide in from Left to Right
    val SlideInFromLeftTransition = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(MotionConstants.DURATION_FAST)
    ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))

    val SlideOutToRightTransition = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(MotionConstants.DURATION_FAST)
    ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
}