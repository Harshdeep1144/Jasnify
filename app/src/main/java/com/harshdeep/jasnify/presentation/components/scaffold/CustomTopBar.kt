package com.harshdeep.jasnify.presentation.components.scaffold

import com.harshdeep.jasnify.R
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
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import sv.lib.squircleshape.SquircleShape

@Composable
fun CustomTopBar(
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    backIcon: TopIcon = TopIcon.Predefined.BACK,
    menuIcon: TopIcon = TopIcon.Predefined.MENU_VERTICAL,
    buttonStyle: ButtonBackground = ButtonBackground.OPAQUE,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Action Slot (Fixed width)
            Box(modifier = Modifier.width(40.dp)) {
                if (onBackClick != null) {
                    TopBarIconButton(
                        icon = backIcon,
                        onClick = onBackClick,
                        backgroundStyle = buttonStyle,
                        size = 40.dp,
                        iconSize = 24.dp
                    )
                }
            }

            // Center Content Slot
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }

            // Right Action Slot (Fixed width)
            Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterEnd) {
                if (onMenuClick != null) {
                    TopBarIconButton(
                        icon = menuIcon,
                        onClick = onMenuClick,
                        backgroundStyle = buttonStyle,
                        size = 40.dp,
                        iconSize = 24.dp
                    )
                }
            }
        }
    }
}

@Composable
fun TopBarSimpleTitle(title: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun TopBarSubtitleDropdown(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Typically no ripple for header dropdowns, or add indication if preferred
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = JasnifyTheme.typography.headingLarge,
            color = ContentPrimary
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = subtitle,
                style = JasnifyTheme.typography.labelMedium,
                color = ContentSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = ContentSecondary
            )
        }
    }
}

@Composable
fun TopBarProfileContent(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(SquircleShape(12, CornerSmoothingDefault))
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(SquircleShape(100, 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "Profile Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = JasnifyTheme.typography.headingLarge,
                color = ContentPrimary
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = subtitle,
                style = JasnifyTheme.typography.labelMedium,
                color = ContentSecondary
            )
        }
    }
}

// --- Comprehensive Preview showing all variants ---

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun CustomTopBarVariantsPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Menu Button Only
        CustomTopBar(onMenuClick = {}) {
            Spacer(Modifier.weight(1f))
        }

        // 2. Back Button Only
        CustomTopBar(onBackClick = {}) {
            Spacer(Modifier.weight(1f))
        }

        // 3. Back + Menu Button
        CustomTopBar(onBackClick = {}, onMenuClick = {}) {
            Spacer(Modifier.weight(1f))
        }

        // 4. Centered Dropdown (No actions)
        CustomTopBar {
            TopBarSubtitleDropdown("Label", "Subtitle", onClick = {})
        }

        // 5. Back + Centered Dropdown
        CustomTopBar(onBackClick = {}) {
            TopBarSubtitleDropdown("Label", "Subtitle", onClick = {})
        }

        // 6. Back + Centered Dropdown + Menu
        CustomTopBar(onBackClick = {}, onMenuClick = {}) {
            TopBarSubtitleDropdown("Label", "Subtitle", onClick = {})
        }

        // 7. Simple Centered Title (No actions)
        CustomTopBar {
            TopBarSimpleTitle("Label")
        }

        // 8. Back + Simple Centered Title
        CustomTopBar(onBackClick = {}) {
            TopBarSimpleTitle("Label")
        }

        // 9. Back + Simple Centered Title + Menu
        CustomTopBar(onBackClick = {}, onMenuClick = {}) {
            TopBarSimpleTitle("Label")
        }

        // 10. Profile Variant (Start aligned)
        CustomTopBar(onBackClick = {}, onMenuClick = {}) {
            TopBarProfileContent("Label", "Subtitle", onClick = {})
        }

        // 11. Translucent Button Style Variant
        CustomTopBar(
            onBackClick = {},
            onMenuClick = {},
            buttonStyle = ButtonBackground.TRANSLUCENT
        ) {
            TopBarSimpleTitle("Translucent")
        }
    }
}