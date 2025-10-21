package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmall
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
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
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: SearchBarType = SearchBarType.DEFAULT
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val focusManager = LocalFocusManager.current
    // 1. Create a FocusRequester
    val focusRequester = remember { FocusRequester() }

    var isExpanded by remember { mutableStateOf(type == SearchBarType.DEFAULT) }

    val duration = 300

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
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .focusRequester(focusRequester), // Apply the focus requester here
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search",
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentSecondary
                    )
                },
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
                leadingIcon = {
                    if (isFocused) {
                        IconButton(
                            onClick = {
                                if (type == SearchBarType.COMPACT) {
                                    isExpanded = false
                                    onValueChange("")
                                } else {
                                    onValueChange("")
                                }
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = ContentPrimary
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
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
                    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                    focusedContainerColor = SurfaceSecondary,
                    unfocusedContainerColor = SurfaceSecondary,
                    cursorColor = ContentBrand,
                ),
                interactionSource = interactionSource,
            )

            // Request focus when the Composable enters the composition (i.e., when expanded)
            LaunchedEffect(Unit) {
                if (type == SearchBarType.COMPACT) {
                    focusRequester.requestFocus()
                }
            }

        } else {
            Box(
                modifier = modifier
                    .size(56.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                    )
                    .background(
                        color = SurfaceSecondary,
                        shape = SquircleShape(CornerLarge, CornerSmoothingDefault)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            isExpanded = true
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
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

    JasnifyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CustomSearchBar(
                value = text,
                onValueChange = {text = it},
            )

            Spacer(Modifier.height(20.dp))

            CustomSearchBar(
                value = "Compose Search Query",
                onValueChange = {},
            )

            Spacer(Modifier.height(20.dp))

            CustomSearchBar(
                value = text,
                onValueChange = { text = it },
                type = SearchBarType.COMPACT
            )
        }
    }
}