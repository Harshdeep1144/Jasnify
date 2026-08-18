package com.harshdeep.jasnify.presentation.components.others

import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

enum class SearchBarType {
    DEFAULT,
    COMPACT
}

@Composable
fun CustomSearchBar(
    value: String,
    placeholder: String = "Search",
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: SearchBarType = SearchBarType.DEFAULT,
    isAiSearch: Boolean = false,
    backgroundColor: Color = SurfaceSecondary,
    borderColor: Color? = null,
    borderGradientColors: List<Color>? = null,
    borderWidth: Dp = 1.dp,
    isTranslucent: Boolean = false,
    translucentAlpha: Float = 0.2f,
    isTransparent: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var isExpanded by remember { mutableStateOf(type == SearchBarType.DEFAULT) }

    val duration = 50
    val transitionEasing = FastOutSlowInEasing

    val effectiveBackgroundColor = when {
        isTransparent -> Color.Transparent
        isTranslucent -> backgroundColor.copy(alpha = backgroundColor.alpha * translucentAlpha)
        else -> backgroundColor
    }

    val effectiveIsAiSearch = isAiSearch && isFocused

    LaunchedEffect(isFocused) {
        onActiveChange(isFocused)
        if (!isFocused && type == SearchBarType.COMPACT) {
            isExpanded = false
        }
    }

    val rotationAnimatable = remember { Animatable(0f) }
    val borderAlphaAnimatable = remember { Animatable(1f) }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            borderAlphaAnimatable.snapTo(1f)
            while (true) {
                rotationAnimatable.animateTo(
                    targetValue = rotationAnimatable.value + 360f,
                    animationSpec = tween(
                        durationMillis = 1200,
                        easing = LinearEasing
                    )
                )
            }
        } else {
            borderAlphaAnimatable.snapTo(1f)
            val rotationJob = launch {
                rotationAnimatable.animateTo(
                    targetValue = rotationAnimatable.value + 720f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = CubicBezierEasing(0.25f, 1.0f, 0.50f, 1.0f)
                    )
                )
            }

            delay(2500.milliseconds)
            borderAlphaAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 500)
            )

            rotationJob.join()
        }
    }

    val aiGradientBrush = remember(rotationAnimatable.value, borderAlphaAnimatable.value) {
        object : ShaderBrush() {
            override fun createShader(size: Size): android.graphics.Shader {
                val alpha = borderAlphaAnimatable.value
                val color1 = Color(0xFFE72EFF).copy(alpha = alpha).toArgb()
                val color2 = Color(0xFF5B39AB).copy(alpha = alpha).toArgb()
                val nativeShader = SweepGradient(
                    size.width / 2f,
                    size.height / 2f,
                    intArrayOf(color1, color2, color1),
                    null
                )
                val matrix = Matrix()
                matrix.postRotate(rotationAnimatable.value, size.width / 2f, size.height / 2f)
                nativeShader.setLocalMatrix(matrix)
                return nativeShader
            }
        }
    }

    // Gradient used strictly when the search bar is inactive
    val defaultOutline = MaterialTheme.colorScheme.outline
    val inactiveGradientColors = remember(borderGradientColors, borderColor, defaultOutline) {
        when {
            borderGradientColors != null -> borderGradientColors
            borderColor != null -> listOf(
                borderColor,
                borderColor.copy(alpha = 0.4f)
            )
            else -> listOf(
                defaultOutline.copy(alpha = 0.16f),
                defaultOutline.copy(alpha = 0.16f)
            )
        }
    }
    val inactiveBorderBrush = Brush.verticalGradient(inactiveGradientColors)

    AnimatedContent(
        targetState = isExpanded,
        modifier = modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = duration,
                easing = transitionEasing
            )
        ),
        transitionSpec = {
            (expandHorizontally(
                animationSpec = tween(durationMillis = duration, easing = transitionEasing),
                expandFrom = Alignment.Start
            ) + fadeIn(animationSpec = tween(durationMillis = duration, easing = transitionEasing)))
                .togetherWith(
                    shrinkHorizontally(
                        animationSpec = tween(durationMillis = duration, easing = transitionEasing),
                        shrinkTowards = Alignment.Start
                    ) + fadeOut(animationSpec = tween(durationMillis = duration, easing = transitionEasing))
                )
        },
        label = "SearchBarTransition"
    ) { targetIsExpanded ->
        if (targetIsExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .background(
                        color = effectiveBackgroundColor,
                        shape = RoundedCornerShape(100)
                    )
                    .then(
                        when {
                            effectiveIsAiSearch -> {
                                Modifier
                                    .border(
                                        width = 1.dp,
                                        color = ContentSecondary.copy(alpha = (1f - borderAlphaAnimatable.value) * 0.3f),
                                        shape = RoundedCornerShape(100)
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = aiGradientBrush,
                                        shape = RoundedCornerShape(100)
                                    )
                            }
                            isFocused -> {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ContentPrimary,
                                    shape = RoundedCornerShape(100)
                                )
                            }
                            else -> {
                                Modifier.border(
                                    width = borderWidth,
                                    brush = inactiveBorderBrush,
                                    shape = RoundedCornerShape(100)
                                )
                            }
                        }
                    )
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    textStyle = JasnifyTheme.typography.headingLarge.copy(color = ContentPrimary),
                    cursorBrush = SolidColor(ContentBrand),
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.padding(end = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFocused && !effectiveIsAiSearch) {
                                    IconButton(
                                        onClick = {
                                            if (type == SearchBarType.COMPACT) {
                                                isExpanded = false
                                            }
                                            onValueChange("")
                                            focusManager.clearFocus()
                                            onActiveChange(false)
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_left),
                                            contentDescription = "Back",
                                            tint = ContentPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else {
                                    val iconPainter = if (effectiveIsAiSearch) {
                                        painterResource(id = R.drawable.ic_ai)
                                    } else {
                                        rememberVectorPainter(image = Icons.Rounded.Search)
                                    }
                                    Box(
                                        modifier = Modifier.size(40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = iconPainter,
                                            contentDescription = if (effectiveIsAiSearch) "AI Search" else "Search",
                                            tint = if (effectiveIsAiSearch) Color.Unspecified else if (isFocused) ContentBrandDark else ContentSecondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (value.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = JasnifyTheme.typography.headingLarge,
                                        color = ContentSecondary
                                    )
                                }
                                innerTextField()
                            }

                            if (value.isNotEmpty()) {
                                Box(
                                    modifier = Modifier.padding(start = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { onValueChange("") },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Clear search",
                                            tint = ContentPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }

            LaunchedEffect(Unit) {
                if (type == SearchBarType.COMPACT) {
                    focusRequester.requestFocus()
                }
            }

        } else {
            Box(
                modifier = modifier
                    .size(56.dp)
                    .then(
                        if (effectiveIsAiSearch) {
                            Modifier
                                .border(
                                    width = 1.dp,
                                    color = ContentSecondary.copy(alpha = (1f - borderAlphaAnimatable.value) * 0.3f),
                                    shape = RoundedCornerShape(100)
                                )
                                .border(
                                    width = 1.dp,
                                    brush = aiGradientBrush,
                                    shape = RoundedCornerShape(100)
                                )
                        } else {
                            Modifier.border(
                                width = borderWidth,
                                brush = inactiveBorderBrush,
                                shape = RoundedCornerShape(100)
                            )
                        }
                    )
                    .background(
                        color = effectiveBackgroundColor,
                        shape = RoundedCornerShape(100)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            isExpanded = true
                            onActiveChange(true)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                val iconPainter = if (effectiveIsAiSearch) {
                    painterResource(id = R.drawable.ic_ai)
                } else {
                    rememberVectorPainter(image = Icons.Rounded.Search)
                }
                Icon(
                    painter = iconPainter,
                    contentDescription = if (effectiveIsAiSearch) "AI Search" else "Search",
                    tint = if (effectiveIsAiSearch) Color.Unspecified else ContentPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}