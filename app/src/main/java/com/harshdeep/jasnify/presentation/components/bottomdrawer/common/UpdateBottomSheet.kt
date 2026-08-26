package com.harshdeep.jasnify.presentation.components.bottomdrawer.common

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.harshdeep.jasnify.notifications.model.NotificationConfig
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import sv.lib.squircleshape.SquircleShape

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun UpdateBottomSheet(
    config: NotificationConfig,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val maxSheetHeight = configuration.screenHeightDp.dp * 0.85f

    val containerColor = config.backgroundColor?.let { safeHex(it) }?.let { Color(it.toColorInt()) } ?: SurfacePrimary
    val buttonTextColor = config.textColor?.let { safeHex(it) }?.let { Color(it.toColorInt()) }
    val buttonBgColor = config.buttonColor?.let { safeHex(it) }?.let { Color(it.toColorInt()) }

    CustomBottomSheet(
        heading = config.title,
        onDismiss = onDismiss,
        isVisible = true,
        showDragHandle = false,
        showCloseButton = config.showCloseButton,
        containerColor = containerColor,
        sheetHeight = null,
        closeButtonBackgroundStyle = ButtonBackground.TRANSLUCENT,
        headerBackgroundImage = config.headerBackgroundImage?.takeIf { it.isNotBlank() }?.let { url ->
            {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(config.headerHeight.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxSheetHeight)
                .padding(start = 12.dp, top = 0.dp, bottom = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Box container with fixed height and transparent background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.imageHeight.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                config.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = SquircleShape(CornerLarge, CornerSmoothingDefault)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body Text
            if (config.body.isNotBlank()) {
                Text(
                    text = config.body,
                    style = JasnifyTheme.typography.bodyLarge,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Action Button
            config.buttonText?.takeIf { it.isNotBlank() }?.let { btnText ->
                Spacer(modifier = Modifier.height(24.dp))
                CustomTextButton(
                    text = btnText,
                    onClick = {
                        config.deepLink?.takeIf { it.isNotBlank() }?.let { link ->
                            val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                            context.startActivity(intent)
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shapeStyle = ButtonShapeStyle.Square,
                    contentColor = buttonTextColor,
                    containerColor = buttonBgColor
                )
            }
        }
    }
}

private fun safeHex(hex: String): String {
    return if (hex.startsWith("#")) hex else "#$hex"
}