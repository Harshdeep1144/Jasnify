package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import sv.lib.squircleshape.SquircleShape

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
    isAiSearch: Boolean = false, // Dynamic flag to toggle search background style/icon
    backgroundColor: Color = SurfaceSecondary,
    onActiveChange: (Boolean) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var isExpanded by remember { mutableStateOf(type == SearchBarType.DEFAULT) }
    val duration = 300

    // Sync the external 'active' state with the internal focus state
    LaunchedEffect(isFocused) {
        onActiveChange(isFocused)
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
                        shape = SquircleShape(100, 0f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isFocused) ContentBrandDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        shape = SquircleShape(100, 0f)
                    )
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = placeholder,
                            style = JasnifyTheme.typography.labelXLarge,
                            color = ContentSecondary
                        )
                    },
                    shape = SquircleShape(100, 0f),
                    leadingIcon = {
                        if (isFocused) {
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
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "Back",
                                    tint = ContentPrimary
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
                                tint = ContentSecondary
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
                                    tint = ContentPrimary
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

            // Auto-focus when expanding from COMPACT mode
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
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        shape = SquircleShape(100, 0f)
                    )
                    .background(
                        color = SurfaceSecondary,
                        shape = SquircleShape(100, 0f)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            isExpanded = true
                            // Since we haven't gained focus yet, notify active manually
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
                    tint = ContentSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomSearchBarPreview() {
    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    JasnifyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
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