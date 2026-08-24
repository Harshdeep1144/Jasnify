package com.harshdeep.jasnify.presentation.components.bottomdrawer.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.screens.invitation_cards.noRippleClickable
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun LocationAccessBottomSheet(
    title: String = "Discover the best things happening around you",
    subtitle: String = "Allow location permissions for best recommendations around you",
    onDismiss: () -> Unit,
    onAllowClick: () -> Unit,
    onManualClick: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    CustomBottomSheet(
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null,
        showDragHandle = false,
        showCloseButton = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Illustration (Layered Map and Pin)
                Box(
                    modifier = Modifier.height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.img_hero_venueaddress),
                        contentDescription = null,
                        modifier = Modifier.height(80.dp),
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = title,
                    style = JasnifyTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = ContentPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle
                Text(
                    text = subtitle,
                    style = JasnifyTheme.typography.labelLarge,
                    color = ContentSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Button
            CustomTextButton(
                text = "Allow location access",
                onClick = onAllowClick,
                shapeStyle = ButtonShapeStyle.Round,
                contentColor = ContentInvPrimary,
                containerColor = ContentPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Manual Button
            Text(
                text = "Enter location manually",
                color = ContentPrimary,
                style = JasnifyTheme.typography.labelLarge,
                modifier = Modifier
                    .noRippleClickable(
                        onClick = onManualClick
                    )
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val y = size.height

                        drawLine(
                            color = ContentPrimary,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(2.dp.toPx(), 3.dp.toPx())
                            )
                        )
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}