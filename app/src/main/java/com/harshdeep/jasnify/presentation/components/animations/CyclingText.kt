package com.harshdeep.jasnify.presentation.components.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CyclingText(
    dynamicPhrases: List<String>,
    modifier: Modifier = Modifier,
    fixedPrefix: String = "",
    cycleIntervalMs: Long = 3000L,
    style: TextStyle = JasnifyTheme.typography.labelXLarge,
    color: Color = ContentSecondary
) {
    if (dynamicPhrases.isEmpty()) {
        if (fixedPrefix.isNotEmpty()) {
            Text(
                text = fixedPrefix,
                color = color,
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(dynamicPhrases, cycleIntervalMs) {
        while (true) {
            delay(cycleIntervalMs.milliseconds)
            currentIndex = (currentIndex + 1) % dynamicPhrases.size
        }
    }

    Row(
        modifier = modifier.clipToBounds(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Static prefix (e.g. "Search for ")
        if (fixedPrefix.isNotEmpty()) {
            Text(
                text = fixedPrefix,
                color = color,
                style = style,
                maxLines = 1
            )
        }

        // Animated cycling text (e.g. "budget", "vendors", "expenses")
        AnimatedContent(
            targetState = dynamicPhrases[currentIndex],
            transitionSpec = {
                // Rolls up from bottom to top
                (slideInVertically(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    initialOffsetY = { height -> (height * 1.2f).toInt() }
                ) + fadeIn(animationSpec = tween(durationMillis = 300))).togetherWith(
                    slideOutVertically(
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                        targetOffsetY = { height -> -(height * 1.2f).toInt() }
                    ) + fadeOut(animationSpec = tween(durationMillis = 250))
                )
            },
            label = "CyclingTextRollUp",
            modifier = Modifier.weight(1f, fill = false)
        ) { targetPhrase ->
            Text(
                text = targetPhrase,
                color = color,
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}