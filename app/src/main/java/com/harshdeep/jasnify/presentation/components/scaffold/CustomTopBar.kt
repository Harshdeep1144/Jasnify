package com.harshdeep.jasnify.presentation.components.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle // Import TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.SurfaceInvPrimary
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopBarIconButton
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.theme.BackgroundBrand
import com.harshdeep.jasnify.theme.JasnifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    content: @Composable RowScope.() -> Unit, // Flexible center content
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
) {
    Surface(
        color = BackgroundPrimary,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Icon
            if (onBackClick != null) {
                TopBarIconButton(
                    onClick = { onBackClick() },
                    icon = TopIcon.Predefined.BACK,
                    backgroundStyle = ButtonBackground.TRANSLUCENT
                )
            } else {
                Spacer(modifier = Modifier.size(40.dp)) // keep layout consistent
            }

            // Title Content - takes up available space
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }

            // Action Icon
            if (onMenuClick != null) {
                TopBarIconButton(
                    onClick = { onMenuClick() },
                    icon = TopIcon.Predefined.BACK,
                    backgroundStyle = ButtonBackground.TRANSLUCENT
                )
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}

// --- Content Composables for Center Area ---

@Composable
fun TopBarTitleContent(
    title: String,
    typography: TextStyle = JasnifyTheme.typography.headingXLarge
) {
    Text(
        text = title,
        style = typography, 
        color = SurfaceInvPrimary
    )
}

@Composable
fun TopBarSubtitleContent(
    title: String,
    subtitle: String,
    onSubtitleClick: (() -> Unit)? = null,
    typography: TextStyle = JasnifyTheme.typography.headingXLarge
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(enabled = onSubtitleClick != null, onClick = { onSubtitleClick?.invoke() })
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = typography, // Use the provided style
                color = SurfaceInvPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subtitle,
                    // Subtitle style remains fixed
                    style = MaterialTheme.typography.bodySmall,
                    color = SurfaceInvPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 4.dp)
                )
                if (onSubtitleClick != null) {
                    Icon(
                        painter = painterResource(R.drawable.ic_left), // Placeholder for dropdown
                        contentDescription = "Dropdown",
                        tint = SurfaceInvPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBarImageSubtitleTitleContent(
    title: String,
    subtitle: String,
    imagePainter: Painter,
    onImageClick: (() -> Unit)? = null,
    typography: TextStyle = JasnifyTheme.typography.headingXLarge
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.6f) // Give more space for left alignment
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(32.dp)
                .clip(MaterialTheme.shapes.small) // Use a small clip for rectangular image
                .clickable(enabled = onImageClick != null, onClick = { onImageClick?.invoke() })
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = title,
                style = typography,
                color = SurfaceInvPrimary
            )
            Text(
                text = subtitle,
                style = JasnifyTheme.typography.labelMedium,
                color = SurfaceInvPrimary.copy(alpha = 0.7f)
            )
        }
    }
}



// --- Preview Composables ---

@Preview(showBackground = true)
@Composable
fun CustomTopBarVariantsPreview() {
    val imagePlaceholder = painterResource(R.drawable.ic_right)

    // Example of using a different typography for a title
    val headingTypography = MaterialTheme.typography.headlineSmall

    Column(
        modifier = Modifier.fillMaxWidth().
        background(SurfaceInvPrimary)
    ) {

        // 1. Title Content with default typography (titleLarge)
        CustomTopBar(
            onBackClick = {},
            content = { TopBarTitleContent(title = "Label") }
        )
        Spacer(Modifier.height(20.dp))

        // 2. Title Content with custom typography (headlineSmall)
        CustomTopBar(
            onBackClick = {},
            onMenuClick = {},
            content = { TopBarTitleContent(title = "Label", typography = headingTypography) }
        )
        Spacer(Modifier.height(20.dp))

        // 3. Subtitle Content with custom typography
        CustomTopBar(
            onBackClick = {},
            onMenuClick = {},
            content = {
                TopBarSubtitleContent(
                    title = "Label",
                    subtitle = "New Delhi",
                    onSubtitleClick = {},
                    typography = headingTypography
                )
            }
        )
        Spacer(Modifier.height(20.dp))

        // 4. Image Subtitle Content with default typography
        CustomTopBar(
            onBackClick = {},
            onMenuClick = {},
            content = {
                TopBarImageSubtitleTitleContent(
                    title = "Label",
                    subtitle = "Subtitle",
                    imagePainter = imagePlaceholder,
                )
            }
        )
        Spacer(Modifier.height(20.dp))
    }
}