package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

enum class ProfileMenuVariant {
    HORIZONTAL,
    VERTICAL
}


@Composable
fun ProfileMenuCell(
    title: String,
    subtitle: String? = null,
    icon: Painter,
    modifier: Modifier = Modifier,
    variant: ProfileMenuVariant = ProfileMenuVariant.HORIZONTAL,
    hasBorder: Boolean = true,
    shape: Shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
    containerColor: Color = SurfacePrimary,
    contentColor: Color = ContentPrimary,
    showArrow: Boolean = true,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (hasBorder) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                        shape = shape
                    )
                } else Modifier
            )
            .clickable { onClick() },
        color = containerColor,
        shape = shape
    ) {
        if (variant == ProfileMenuVariant.HORIZONTAL) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = contentColor,
                        modifier = Modifier.weight(1f)
                    )
                    if (showArrow) {
                        Icon(
                            painter = painterResource(R.drawable.ic_right_chevron),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = contentColor
                        )
                    }
                }
                if (subtitle != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.width(24.dp + 8.dp))
                        Text(
                            text = subtitle,
                            style = JasnifyTheme.typography.labelXLarge,
                            color = if (contentColor == ContentPrimary) ContentSecondary else contentColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = contentColor
                    )
                    if (showArrow) {
                        Icon(
                            painter = painterResource(R.drawable.ic_right_chevron),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = contentColor
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = title,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = contentColor
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = JasnifyTheme.typography.labelXLarge,
                        color = if (contentColor == ContentPrimary) ContentSecondary else contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun PreviewProfileMenuCells() {
    JasnifyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceSecondary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Variant 1: Horizontal
            ProfileMenuCell(
                title = "Title",
                subtitle = "Subtitle",
                icon = painterResource(id = R.drawable.ic_profile),
                variant = ProfileMenuVariant.HORIZONTAL
            )

            // Variant 2: Vertical
            ProfileMenuCell(
                title = "Title",
                subtitle = "Subtitle",
                icon = painterResource(id = R.drawable.ic_profile),
                variant = ProfileMenuVariant.VERTICAL
            )
        }
    }
}
