package com.harshdeep.jasnify.presentation.components.bottomdrawer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentBrandDark
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceBrandPrimary
import com.harshdeep.jasnify.theme.SurfaceBrandSecondary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun DownloadPreferencesBottomSheet(
    fileCount: Int,
    initialQuality: String = "Standard Quality",
    onDismiss: () -> Unit,
    onDownload: (String, Boolean) -> Unit,
    onProgress: (Float) -> Unit
) {
    var selectedQuality by remember { mutableStateOf(initialQuality) }
    var rememberSettings by remember { mutableStateOf(false) }

    CustomBottomSheet(
        heading = "Download Preferences",
        onDismiss = onDismiss,
        sheetHeight = null,
        onProgress = onProgress,
        showCloseButton = true,
        showDragHandle = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QualityOption(
                title = "Low Quality",
                size = "4 MB",
                isSelected = selectedQuality == "Low Quality",
                onClick = { selectedQuality = "Low Quality" }
            )
            QualityOption(
                title = "Standard Quality",
                subtitle = "(Recommended)",
                size = "12 MB",
                isSelected = selectedQuality == "Standard Quality",
                onClick = { selectedQuality = "Standard Quality" }
            )
            QualityOption(
                title = "Original Quality",
                size = "12 MB",
                isSelected = selectedQuality == "Original Quality",
                onClick = { selectedQuality = "Original Quality" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSecondary, SquircleShape(CornerLarge))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remember my settings for 7 days",
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentSecondary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = rememberSettings,
                    onCheckedChange = { rememberSettings = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SurfaceBrandPrimary,
                        uncheckedThumbColor = ContentTertiary,
                        uncheckedTrackColor = SurfaceSecondary
                    )
                )
            }

            CustomTextButton(
                onClick = { onDownload(selectedQuality, rememberSettings) },
                text = "Download $fileCount ${if (fileCount > 1) "Files" else "File"}",
                modifier = Modifier.fillMaxWidth(),
                type = ButtonType.Primary,
                shapeStyle = ButtonShapeStyle.Round,
                leadingIcon = painterResource(R.drawable.ic_download)
            )
        }
    }
}

@Composable
private fun QualityOption(
    title: String,
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                color = if (isSelected) SurfaceBrandSecondary.copy(alpha = 0.3f) else Color.Transparent,
                shape = SquircleShape(CornerLarge)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) SurfaceBrandPrimary.copy(alpha = 0.3f) else Color.Transparent,
                shape = SquircleShape(CornerLarge)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = ContentBrandDark,
                unselectedColor = ContentTertiary
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = JasnifyTheme.typography.headingLarge,
                color = if (isSelected) ContentBrandDark else ContentPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = JasnifyTheme.typography.labelMedium,
                    color = if (isSelected) ContentBrandDark.copy(alpha = 0.7f) else ContentSecondary
                )
            }
        }
        Text(
            text = size,
            style = JasnifyTheme.typography.headingLarge,
            color = if (isSelected) ContentBrandDark else ContentPrimary
        )
    }
}