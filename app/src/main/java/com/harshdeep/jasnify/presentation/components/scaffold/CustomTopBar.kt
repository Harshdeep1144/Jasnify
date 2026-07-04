package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CustomTopBar(
    title: String? = null,
    subtitle: String? = null,
    image: Painter? = null,
    isLargeTitle: Boolean = false,
    textColor: Color = ContentPrimary,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    onDropdownClick: (() -> Unit)? = null,
    backIcon: TopIcon = TopIcon.Predefined.BACK,
    menuIcon: TopIcon = TopIcon.Predefined.MENU_VERTICAL,
    buttonStyle: ButtonBackground = ButtonBackground.OPAQUE,
    translucentAlpha: Float = 0.2f,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Action Slot
            Box(modifier = Modifier.width(40.dp)) {
                if (onBackClick != null) {
                    TopBarIconButton(
                        icon = backIcon,
                        onClick = onBackClick,
                        backgroundStyle = buttonStyle,
                        size = 40.dp,
                        iconSize = 24.dp,
                        iconColor = textColor,
                        translucentAlpha = translucentAlpha
                    )
                }
            }

            // Center Content Slot
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = if (image != null) Alignment.CenterStart else Alignment.Center
            ) {
                when {
                    image != null -> {
                        TopBarProfileLayout(
                            title = title.orEmpty(),
                            subtitle = subtitle,
                            image = image,
                            isLargeTitle = isLargeTitle,
                            textColor = textColor,
                            onClick = onDropdownClick
                        )
                    }
                    title != null -> {
                        TopBarTextLayout(
                            title = title,
                            subtitle = subtitle,
                            isLargeTitle = isLargeTitle,
                            textColor = textColor,
                            isClickable = onDropdownClick != null,
                            onClick = onDropdownClick ?: {},
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }

            // Right Action Slot
            Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterEnd) {
                if (onMenuClick != null) {
                    TopBarIconButton(
                        icon = menuIcon,
                        onClick = onMenuClick,
                        backgroundStyle = buttonStyle,
                        size = 40.dp,
                        iconSize = 24.dp,
                        iconColor = textColor,
                        translucentAlpha = translucentAlpha
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TopBarTextLayout(
    title: String,
    subtitle: String?,
    isLargeTitle: Boolean,
    textColor: Color,
    isClickable: Boolean,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Column(
        modifier = Modifier
            .then(
                if (isClickable) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ) else Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val sharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                Modifier.sharedElement(
                    rememberSharedContentState(key = "text_$title"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        } else Modifier

        Text(
            text = title,
            style = if (isLargeTitle) {
                JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium)
            } else {
                JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Normal)
            },
            color = textColor,
            modifier = sharedModifier
        )
        if (subtitle != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subtitle,
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary
                )
                if (isClickable) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = ContentSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBarProfileLayout(
    title: String,
    subtitle: String?,
    image: Painter,
    isLargeTitle: Boolean,
    textColor: Color,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier.clip(SquircleShape(100, 0f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = image,
                contentDescription = "Profile Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = if (isLargeTitle) {
                    JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium)
                } else {
                    JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Normal)
                },
                color = textColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = JasnifyTheme.typography.labelMedium,
                    color = ContentSecondary
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun CustomTopBarVariantsPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button Only
        CustomTopBar(onBackClick = {})

        // Back + Menu Buttons
        CustomTopBar(title = "Label", onBackClick = {}, onMenuClick = {})

        // Label + Subtitle + Dropdown (Center)
        CustomTopBar(
            title = "Label",
            subtitle = "Subtitle",
            onDropdownClick = {}
        )

        // Back + Label + Subtitle + Dropdown
        CustomTopBar(
            title = "Label",
            subtitle = "Subtitle",
            onBackClick = {},
            onDropdownClick = {}
        )

        // Back + Title + Action
        CustomTopBar(
            title = "Title",
            onBackClick = {},
            onMenuClick = {}
        )

        // Large Title
        CustomTopBar(
            title = "Large Title",
            isLargeTitle = true,
            onBackClick = {}
        )
    }
}
