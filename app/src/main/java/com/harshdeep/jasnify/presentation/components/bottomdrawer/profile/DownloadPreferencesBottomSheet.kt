package com.harshdeep.jasnify.presentation.components.bottomdrawer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.OptionSelector
import com.harshdeep.jasnify.theme.ContentBrand
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val FooterCardShape = SquircleShape(
    radius = CornerLarge,
    cornerSmoothing = CornerSmoothingDefault
)

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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OptionSelector(
                label = "Low Quality",
                trailingText = "4 MB",
                isSelected = selectedQuality == "Low Quality",
                onClick = { selectedQuality = "Low Quality" }
            )

            OptionSelector(
                label = "Standard Quality",
                bodyText = "(Recommended)",
                trailingText = "12 MB",
                isSelected = selectedQuality == "Standard Quality",
                onClick = { selectedQuality = "Standard Quality" }
            )

            OptionSelector(
                label = "Original Quality",
                trailingText = "12 MB",
                isSelected = selectedQuality == "Original Quality",
                onClick = { selectedQuality = "Original Quality" }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Unified Footer Card matching ContactPickerBottomSheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FooterCardShape)
                    .background(SurfaceSecondary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FooterCardShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            rememberSettings = !rememberSettings
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Remember my settings for 7 days",
                        style = JasnifyTheme.typography.headingSmall,
                        color = ContentSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = rememberSettings,
                        onCheckedChange = { rememberSettings = it },
                        modifier = Modifier
                            .height(24.dp)
                            .scale(0.8f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ContentInvPrimary,
                            checkedTrackColor = ContentBrand,
                            uncheckedThumbColor = ContentInvPrimary,
                            uncheckedTrackColor = ContentTertiary,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }

                CustomTextButton(
                    onClick = { onDownload(selectedQuality, rememberSettings) },
                    text = "Download $fileCount ${if (fileCount > 1) "Files" else "File"}",
                    leadingIcon = painterResource(id = R.drawable.ic_download),
                    modifier = Modifier.fillMaxWidth(),
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Square
                )
            }
        }
    }
}