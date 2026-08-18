package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.utils.noRippleClickable
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerMedium
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
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
    borderColor: Color? = null,
    borderGradientColors: List<Color>? = null,
    borderWidth: Dp = 1.dp,
    translucentAlpha: Float = 0.2f,
    isLeftAligned: Boolean = false,
    titleIcon: Painter? = null,
    secondaryIcon: TopIcon? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isLeftAligned) {
                // Leftmost Element: Back button (if present)
                if (onBackClick != null && titleIcon == null) {
                    TopBarIconButton(
                        icon = backIcon,
                        onClick = onBackClick,
                        backgroundStyle = buttonStyle,
                        borderColor = borderColor,
                        borderGradientColors = borderGradientColors,
                        borderWidth = borderWidth,
                        size = 40.dp,
                        iconSize = 24.dp,
                        iconColor = textColor,
                        translucentAlpha = translucentAlpha
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Left Aligned Container: Icon + Title + Subtitle
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (onDropdownClick != null) Modifier.noRippleClickable(
                                onClick = onDropdownClick
                            ) else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (titleIcon != null) {
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                    shape = SquircleShape(CornerMedium, CornerSmoothingDefault)
                                ),
                            shape = SquircleShape(CornerMedium, CornerSmoothingDefault),
                            color = SurfaceSecondary
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    painter = titleIcon,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        val titleStyle = if (isLargeTitle) {
                            JasnifyTheme.typography.headingXLarge.copy(
                                fontWeight = FontWeight.Medium,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.None
                                )
                            )
                        } else {
                            JasnifyTheme.typography.headingLarge.copy(
                                fontWeight = FontWeight.Normal,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.None
                                )
                            )
                        }

                        if (title != null) {
                            if (subtitle == null && onDropdownClick != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = title,
                                        style = titleStyle,
                                        color = textColor
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .wrapContentSize(Alignment.Center),
                                        tint = ContentSecondary
                                    )
                                }
                            } else {
                                Text(
                                    text = title,
                                    style = titleStyle,
                                    color = textColor
                                )
                            }
                        }
                        if (subtitle != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = subtitle,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .basicMarquee(),
                                    style = JasnifyTheme.typography.labelMedium.copy(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.None
                                        )
                                    ),
                                    color = ContentSecondary
                                )
                                if (onDropdownClick != null) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .wrapContentSize(Alignment.Center),
                                        tint = ContentSecondary
                                    )
                                }
                            }
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
                            borderColor = borderColor,
                            borderGradientColors = borderGradientColors,
                            borderWidth = borderWidth,
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
                            borderColor = borderColor,
                            borderGradientColors = borderGradientColors,
                            borderWidth = borderWidth,
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
                    if (onBackClick != null && titleIcon == null) {
                        TopBarIconButton(
                            icon = backIcon,
                            onClick = onBackClick,
                            backgroundStyle = buttonStyle,
                            borderColor = borderColor,
                            borderGradientColors = borderGradientColors,
                            borderWidth = borderWidth,
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
                                borderColor = borderColor,
                                borderGradientColors = borderGradientColors,
                                borderWidth = borderWidth,
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
                                borderColor = borderColor,
                                borderGradientColors = borderGradientColors,
                                borderWidth = borderWidth,
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
                if (isClickable) Modifier.noRippleClickable(
                    onClick = onClick
                ) else Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val titleStyle = if (isLargeTitle) {
            JasnifyTheme.typography.headingXLarge.copy(
                fontWeight = FontWeight.Medium,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            )
        } else {
            JasnifyTheme.typography.headingLarge.copy(
                fontWeight = FontWeight.Normal,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            )
        }

        if (subtitle == null && isClickable) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = titleStyle,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .wrapContentSize(Alignment.Center),
                    tint = ContentSecondary
                )
            }
        } else {
            Text(
                text = title,
                style = titleStyle,
                color = textColor
            )
        }

        if (subtitle != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = subtitle,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .basicMarquee(),
                    style = JasnifyTheme.typography.labelMedium.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        )
                    ),
                    color = ContentSecondary
                )
                if (isClickable) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .wrapContentSize(Alignment.Center),
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
            .then(
                if (onClick != null) Modifier.noRippleClickable(
                    onClick = onClick
                ) else Modifier
            ),
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
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.Center
        ) {
            val titleStyle = if (isLargeTitle) {
                JasnifyTheme.typography.headingXLarge.copy(
                    fontWeight = FontWeight.Medium,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                )
            } else {
                JasnifyTheme.typography.headingLarge.copy(
                    fontWeight = FontWeight.Normal,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                )
            }

            if (subtitle == null && onClick != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = titleStyle,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .wrapContentSize(Alignment.Center),
                        tint = ContentSecondary
                    )
                }
            } else {
                Text(
                    text = title,
                    style = titleStyle,
                    color = textColor
                )
            }

            if (subtitle != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .basicMarquee(),
                        style = JasnifyTheme.typography.labelMedium.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            )
                        ),
                        color = ContentSecondary
                    )
                    if (onClick != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .wrapContentSize(Alignment.Center),
                            tint = ContentSecondary
                        )
                    }
                }
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
            subtitle = "General files contain common-purpose documents and resources that are not specific to any single project or department.",
            onBackClick = {},
            onDropdownClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.OPAQUE
        )

        // 6. Center-Left aligned Profile card layout with dropdown enabled
        CustomTopBar(
            title = "Jane Doe",
            subtitle = "Active 2 mins ago",
            image = painterResource(R.drawable.ic_google),
            onBackClick = {},
            onDropdownClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.OPAQUE
        )

        // 7. Left Aligned layout with dropdown active on subtitle
        CustomTopBar(
            title = "Checklist",
            titleIcon = painterResource(R.drawable.ic_checklists),
            isLeftAligned = true,
            isLargeTitle = true,
            secondaryIcon = TopIcon.Predefined.SEARCH,
            onSecondaryClick = {},
            subtitle = "New Delhi, India",
            onDropdownClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.OPAQUE
        )

        // 8. Translucent with custom gradient border
        CustomTopBar(
            onBackClick = {},
            secondaryIcon = TopIcon.Predefined.PIN,
            onSecondaryClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.TRANSLUCENT,
            borderColor = Color.Red
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