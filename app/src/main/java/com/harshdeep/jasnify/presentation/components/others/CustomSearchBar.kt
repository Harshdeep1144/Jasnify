package com.harshdeep.jasnify.presentation.components.others

import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
    onActiveChange: (Boolean) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var isExpanded by remember { mutableStateOf(type == SearchBarType.DEFAULT) }
    val duration = 300

    // Sync the external 'active' state and collapse COMPACT search bar when it loses focus
    LaunchedEffect(isFocused) {
        onActiveChange(isFocused)
        if (!isFocused && type == SearchBarType.COMPACT) {
            isExpanded = false
        }
    }

    // Dynamic rotation angle and alpha/fade state animations
    val rotationAnimatable = remember { Animatable(0f) }
    val borderAlphaAnimatable = remember { Animatable(1f) } // 1f = Full AI Gradient, 0f = ContentSecondary

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
            // Reset to visible gradient on focus loss before starting the decay transition
            borderAlphaAnimatable.snapTo(1f)

            // Perform smooth decelerating 720-degree rotation over 3 seconds
            val rotationJob = launch {
                rotationAnimatable.animateTo(
                    targetValue = rotationAnimatable.value + 720f,
                    animationSpec = tween(
                        durationMillis = 3000,
                        easing = CubicBezierEasing(0.25f, 1.0f, 0.50f, 1.0f)
                    )
                )
            }

            // After 2.5 seconds, start fading out gradient (fading in ContentSecondary outline over 500ms)
            delay(2500.milliseconds)
            borderAlphaAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 500)
            )

            rotationJob.join()
        }
    }

    // High performance sweep gradient brush with seamless colors and localized matrix rotation
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

    AnimatedContent(
        targetState = isExpanded,
        modifier = modifier.animateContentSize(tween(durationMillis = duration)),
        transitionSpec = {
            (expandHorizontally(
                animationSpec = tween(durationMillis = duration),
                expandFrom = Alignment.Start
            ) + fadeIn(animationSpec = tween(durationMillis = duration)))
                .togetherWith(
                    shrinkHorizontally(
                        animationSpec = tween(durationMillis = duration),
                        shrinkTowards = Alignment.Start
                    ) + fadeOut(animationSpec = tween(durationMillis = duration))
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
                        color = backgroundColor,
                        shape = RoundedCornerShape(100)
                    )
                    .then(
                        if (isAiSearch) {
                            Modifier
                                .border(
                                    width = 1.dp,
                                    color = ContentSecondary.copy(alpha = (1f - borderAlphaAnimatable.value) * 0.3f),
                                    shape = RoundedCornerShape(100)
                                )
                                // Overlaid rotating AI Gradient border
                                .border(
                                    width = 1.5.dp,
                                    brush = aiGradientBrush,
                                    shape = RoundedCornerShape(100)
                                )
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = if (isFocused) ContentBrandDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(100)
                            )
                        }
                    )
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .background(Color.Transparent)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = placeholder,
                            style = JasnifyTheme.typography.headingLarge,
                            color = ContentSecondary
                        )
                    },
                    textStyle = JasnifyTheme.typography.headingLarge,
                    shape = RoundedCornerShape(100),
                    leadingIcon = {
                        if (isFocused && !isAiSearch) {
                            IconButton(
                                onClick = {
                                    if (type == SearchBarType.COMPACT) {
                                        isExpanded = false
                                    }
                                    onValueChange("")
                                    focusManager.clearFocus()
                                    // Manually trigger false just in case focus clear takes a frame
                                    onActiveChange(false)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_left),
                                    contentDescription = "Back",
                                    tint = ContentPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            val iconPainter = if (isAiSearch) {
                                painterResource(id = R.drawable.ic_ai)
                            } else {
                                rememberVectorPainter(image = Icons.Rounded.Search)
                            }
                            Icon(
                                painter = iconPainter,
                                contentDescription = if (isAiSearch) "AI Search" else "Search",
                                tint = if (isAiSearch) Color.Unspecified else if (isFocused) ContentBrandDark else ContentSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        if (value.isNotEmpty()) {
                            IconButton(
                                onClick = { onValueChange("") }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear search",
                                    tint = ContentPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = ContentBrand,
                    ),
                    interactionSource = interactionSource,
                )
            }

            // Autofocus when expanding from COMPACT mode
            LaunchedEffect(Unit) {
                if (type == SearchBarType.COMPACT) {
                    focusRequester.requestFocus()
                }
            }

        } else {
            // COMPACT Mode
            Box(
                modifier = modifier
                    .size(56.dp)
                    .then(
                        if (isAiSearch) {
                            Modifier
                                .border(
                                    width = 1.dp,
                                    color = ContentSecondary.copy(alpha = (1f - borderAlphaAnimatable.value) * 0.3f),
                                    shape = RoundedCornerShape(100)
                                )
                                // Overlaid rotating AI Gradient border
                                .border(
                                    width = 1.5.dp,
                                    brush = aiGradientBrush,
                                    shape = RoundedCornerShape(100)
                                )
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(100)
                            )
                        }
                    )
                    .background(
                        color = SurfaceSecondary,
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
                val iconPainter = if (isAiSearch) {
                    painterResource(id = R.drawable.ic_ai)
                } else {
                    rememberVectorPainter(image = Icons.Rounded.Search)
                }
                Icon(
                    painter = iconPainter,
                    contentDescription = if (isAiSearch) "AI Search" else "Search",
                    tint = if (isAiSearch) Color.Unspecified else ContentPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}



// ------------------------------------------------------------- Preview ---------------------------------------------------------------



@Preview(showBackground = true)
@Composable
fun CustomSearchBarPreview() {
    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    JasnifyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Capture taps on the background container to clear active focus
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { focusManager.clearFocus() }
                    )
                }
                .padding(16.dp)
        ) {
            Text(
                text = "Is Search Active: $isSearchActive",
                color = if (isSearchActive) ContentBrand else ContentSecondary
            )

            Spacer(Modifier.height(10.dp))

            // 1. Default Standard Search
            CustomSearchBar(
                value = text,
                placeholder = "Standard Search",
                onValueChange = { text = it },
                onActiveChange = { isSearchActive = it }
            )

            Spacer(Modifier.height(16.dp))

            // 2. Default AI Search (isAiSearch = true)
            CustomSearchBar(
                value = text,
                placeholder = "AI Sparkle Search",
                onValueChange = { text = it },
                isAiSearch = true,
                onActiveChange = { isSearchActive = it }
            )

            Spacer(Modifier.height(20.dp))

            // 3. Compact Standard Search
            CustomSearchBar(
                value = text,
                onValueChange = { text = it },
                type = SearchBarType.COMPACT,
                onActiveChange = { isSearchActive = it }
            )

            Spacer(Modifier.height(16.dp))

            // 4. Compact AI Search (isAiSearch = true)
            CustomSearchBar(
                value = text,
                onValueChange = { text = it },
                type = SearchBarType.COMPACT,
                isAiSearch = true,
                onActiveChange = { isSearchActive = it }
            )
        }
    }
}