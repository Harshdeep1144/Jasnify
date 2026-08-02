package com.harshdeep.jasnify.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

/**
 * Standardized duration constants (in milliseconds) across the application.
 */
object MotionConstants {
    const val DURATION_FAST = 250
    const val DURATION_DEFAULT = 300
    const val DURATION_SLOW = 500
}

/**
 * Pre-configured ContentTransform instances for navigation transitions.
 */
object ScreenTransitions {

    // =========================================================================
    // 1. STATIC BACKGROUND TRANSITIONS (Previous screen DOES NOT change position)
    // =========================================================================

    // Slide in from bottom-to-top while previous screen stays static (Slow - 500ms)
    // Primary usage: Location Selector Screen (Entering)
    val SlideBottomToTopStaticSlowTransition: ContentTransform =
        (slideInVertically(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_SLOW),
            initialOffsetY = { fullHeight -> fullHeight }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_SLOW))).togetherWith(
            fadeOut(animationSpec = tween(MotionConstants.DURATION_SLOW))
        ).apply {
            targetContentZIndex = 1f
        }

    // Slide out top-to-bottom while previous screen stays static (Slow - 500ms)
    // Primary usage: Location Selector Screen (Exiting)
    val SlideTopToBottomStaticSlowTransition: ContentTransform =
        fadeIn(animationSpec = tween(MotionConstants.DURATION_SLOW)).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_SLOW),
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_SLOW))
        ).apply {
            targetContentZIndex = 0f
        }

    // Slide in from bottom-to-top while previous screen stays static (Fast - 250ms)
    val SlideBottomToTopStaticFastTransition: ContentTransform =
        (slideInVertically(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
            initialOffsetY = { fullHeight -> fullHeight }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))).togetherWith(
            fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
        ).apply {
            targetContentZIndex = 1f
        }

    // Slide out top-to-bottom while previous screen stays static (Fast - 250ms)
    val SlideTopToBottomStaticFastTransition: ContentTransform =
        fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST)).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
        ).apply {
            targetContentZIndex = 0f
        }



    // =========================================================================
    // 2. MOVING BACKGROUND TRANSITIONS (Previous screen CHANGES position / shifts)
    // =========================================================================



    // Slide in from bottom-to-top while previous screen shifts up by 1/3 (Fast - 250ms)
    // Primary usage: Timeline Detail Screen & Vendor Detail Screen (Entering)
    val SlideBottomToTopMovingFastTransition: ContentTransform =
        (slideInVertically(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
            initialOffsetY = { fullHeight -> fullHeight }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
                targetOffsetY = { fullHeight -> -fullHeight / 3 }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
        ).apply {
            targetContentZIndex = 1f
        }

    // Slide out top-to-bottom while previous screen shifts down into place (Fast - 250ms)
    // Primary usage: Timeline Detail Screen & Vendor Detail Screen (Exiting)
    val SlideTopToBottomMovingFastTransition: ContentTransform =
        (slideInVertically(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
            initialOffsetY = { fullHeight -> -fullHeight / 3 }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
        ).apply {
            targetContentZIndex = 0f
        }

    // Slide in from bottom-to-top while previous screen shifts up by 1/3 (Slow - 500ms)
    val SlideBottomToTopMovingSlowTransition: ContentTransform =
        (slideInVertically(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_SLOW),
            initialOffsetY = { fullHeight -> fullHeight }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_SLOW))).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_SLOW),
                targetOffsetY = { fullHeight -> -fullHeight / 3 }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_SLOW))
        ).apply {
            targetContentZIndex = 1f
        }

    // Slide out top-to-bottom while previous screen shifts down into place (Slow - 500ms)
    val SlideTopToBottomMovingSlowTransition: ContentTransform =
        (slideInVertically(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_SLOW),
            initialOffsetY = { fullHeight -> -fullHeight / 3 }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_SLOW))).togetherWith(
            slideOutVertically(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_SLOW),
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_SLOW))
        ).apply {
            targetContentZIndex = 0f
        }



    // =========================================================================
    // 3. HORIZONTAL & FADE TRANSITIONS
    // =========================================================================



    // Slide in from right-to-left (Fast - 250ms)
    val SlideRightToLeftFastTransition: ContentTransform =
        (slideInHorizontally(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
            initialOffsetX = { fullWidth -> fullWidth }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))).togetherWith(
            slideOutHorizontally(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
                targetOffsetX = { fullWidth -> -fullWidth / 3 }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
        ).apply {
            targetContentZIndex = 1f
        }

    // Slide out left-to-right (Fast - 250ms)
    val SlideLeftToRightFastTransition: ContentTransform =
        (slideInHorizontally(
            animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
            initialOffsetX = { fullWidth -> -fullWidth / 3 }
        ) + fadeIn(animationSpec = tween(MotionConstants.DURATION_FAST))).togetherWith(
            slideOutHorizontally(
                animationSpec = tween(durationMillis = MotionConstants.DURATION_FAST),
                targetOffsetX = { fullWidth -> fullWidth }
            ) + fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST))
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
}