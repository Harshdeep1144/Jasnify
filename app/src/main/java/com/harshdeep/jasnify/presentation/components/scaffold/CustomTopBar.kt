package com.harshdeep.jasnify.presentation.components.scaffold

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

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
    isLeftAligned: Boolean = false,
    titleIcon: TopIcon? = null,
    secondaryIcon: TopIcon? = null,
    onSecondaryClick: (() -> Unit)? = null
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

            if (isLeftAligned) {
                // Leftmost Element: Back button (if present)
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
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Left Aligned Container: Icon + Title + Subtitle
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (titleIcon != null) {
                        TopBarIconButton(
                            icon = titleIcon,
                            onClick = {},
                            backgroundStyle = ButtonBackground.TRANSPARENT,
                            size = 32.dp,
                            iconSize = 24.dp,
                            iconColor = textColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column {
                        if (title != null) {
                            Text(
                                text = title,
                                style = if (isLargeTitle) {
                                    JasnifyTheme.typography.headingXLarge.copy(fontWeight = FontWeight.Medium)
                                } else {
                                    JasnifyTheme.typography.headingLarge.copy(fontWeight = FontWeight.Normal)
                                },
                                color = textColor
                            )
                        }
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = JasnifyTheme.typography.labelMedium,
                                color = ContentSecondary
                            )
                        }
                    }
                }

                // Right Side Multi-Actions (Supports up to 2 icons horizontally)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (secondaryIcon != null && onSecondaryClick != null) {
                        TopBarIconButton(
                            icon = secondaryIcon,
                            onClick = onSecondaryClick,
                            backgroundStyle = buttonStyle,
                            size = 40.dp,
                            iconSize = 24.dp,
                            iconColor = textColor,
                            translucentAlpha = translucentAlpha
                        )
                    }
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

            } else {
                // Standard Center-Aligned Layout
                // Back Button Box (Width locked for centered alignment calculations)
                Box(modifier = Modifier.width(40.dp)) {
                    if (onBackClick != null) {
                        TopBarIconButton(
                            icon = backIcon,
                            onClick = onBackClick,
                            backgroundStyle = buttonStyle,
                            size = 40.dp,
                            iconSize = 18.dp,
                            iconColor = textColor,
                            translucentAlpha = translucentAlpha
                        )
                    }
                }

                // Center Title Container
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
                                onClick = onDropdownClick ?: {}
                            )
                        }
                    }
                }

                // Right Actions box (Matches width dynamically to support double or single actions gracefully)
                val actionWidth = if (secondaryIcon != null && onSecondaryClick != null) 88.dp else 40.dp
                Box(modifier = Modifier.width(actionWidth), contentAlignment = Alignment.CenterEnd) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (secondaryIcon != null && onSecondaryClick != null) {
                            TopBarIconButton(
                                icon = secondaryIcon,
                                onClick = onSecondaryClick,
                                backgroundStyle = buttonStyle,
                                size = 40.dp,
                                iconSize = 24.dp,
                                iconColor = textColor,
                                translucentAlpha = translucentAlpha
                            )
                        }
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
    }
}

@Composable
private fun TopBarTextLayout(
    title: String,
    subtitle: String?,
    isLargeTitle: Boolean,
    textColor: Color,
    isClickable: Boolean,
    onClick: () -> Unit
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

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun CustomTopBarVariantsPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundPrimary),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {

        // 1. Back button only
        CustomTopBar(
            onBackClick = {},
            buttonStyle = ButtonBackground.OPAQUE
        )

        // 2. Simple Center Label
        CustomTopBar(
            title = "Simple Label",
            buttonStyle = ButtonBackground.TRANSPARENT
        )

        // 3. Center Label + Subtitle (Dropdown click disabled)
        CustomTopBar(
            title = "Label Only",
            subtitle = "Non-interactive subtitle",
            onBackClick = {}
        )

        // 4. Center Label + Subtitle + Interactive Chevron Dropdown
        CustomTopBar(
            title = "Dropdown Active",
            subtitle = "Click to open folder",
            onBackClick = {},
            onDropdownClick = {}
        )

        // 5. Classic Center Layout (Back + Label + Dropdown + Menu)
        CustomTopBar(
            title = "Standard Workspace",
            subtitle = "General files",
            onBackClick = {},
            onDropdownClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.OPAQUE
        )

        // 6. Center-Left aligned Profile card layout with action controls
        CustomTopBar(
            title = "Jane Doe",
            subtitle = "Active 2 mins ago",
            image = painterResource(R.drawable.ic_google), // Using vector checklist painter as placeholder
            onBackClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.OPAQUE
        )

        CustomTopBar(
            title = "Checklist",
            titleIcon = TopIcon.Predefined.CHECKLIST,
            isLeftAligned = true,
            isLargeTitle = true,
            secondaryIcon = TopIcon.Predefined.SEARCH,
            onSecondaryClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.OPAQUE
        )

        CustomTopBar(
            onBackClick = {},
            secondaryIcon = TopIcon.Predefined.PIN,
            onSecondaryClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.TRANSLUCENT,
            translucentAlpha = 0.25f,
            textColor = Color.Black
        )

        // 9. Custom Color top bar (Purple/Blue text styling)
        CustomTopBar(
            title = "Custom Brand Accent",
            subtitle = "Subtitles remain secondary",
            textColor = Color(0xFF6200EE),
            onBackClick = {},
            onMenuClick = {}
        )
    }
}