package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.theme.BackgroundPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerLarge
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun GuestInfoBottomSheet(
    guest: Guest,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onInviteClick: () -> Unit,
    onProgress: (Float) -> Unit = {}
) {
    CustomBottomSheet(
        heading = "Guest Info",
        onDismiss = onDismiss,
        onProgress = onProgress,
        sheetHeight = null // Dynamic height
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                if (guest.imageUrl != null) {
                    AsyncImage(
                        model = guest.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_user_profile),
                        error = painterResource(id = R.drawable.ic_user_profile)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_profile),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name
            Text(
                text = guest.name,
                style = JasnifyTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = ContentPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Invite Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CustomTextButton(
                    onClick = onInviteClick,
                    text = if (guest.isInvited) "Invited" else "Mark as invited",
                    size = ButtonSize.Small,
                    type = ButtonType.Primary,
                    shapeStyle = ButtonShapeStyle.Round,
                    containerColor = if (guest.isInvited) Color.White else Color.Black,
                    contentColor = if (guest.isInvited) Color.Black else Color.White,
                    enabled = true,
                    leadingIcon = if (guest.isInvited) painterResource(R.drawable.ic_tick) else null,
                    customBorder = if (guest.isInvited) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
                )

                if (guest.isInvited && guest.invitedBy != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Invited by ${guest.invitedBy}",
                        style = JasnifyTheme.typography.labelLarge,
                        color = ContentSecondary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = guest.invitedAt ?: "",
                        style = JasnifyTheme.typography.labelLarge,
                        color = ContentSecondary.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = SquircleShape(CornerLarge, CornerSmoothingDefault),
                color = SurfaceSecondary
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Guest Type
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = BackgroundPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_user_profile),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = ContentSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Guest Type",
                                style = JasnifyTheme.typography.labelSmall,
                                color = ContentSecondary
                            )
                            Text(
                                text = guest.type,
                                style = JasnifyTheme.typography.labelXLarge,
                                color = Color(0xFF635994), // Purple-ish color from image
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(start = 52.dp)
                    )

                    // Contact No.
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = BackgroundPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_phone),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = ContentSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Contact No.",
                                style = JasnifyTheme.typography.labelSmall,
                                color = ContentSecondary
                            )
                            Text(
                                text = guest.contactNo.ifEmpty { "Not Provided" },
                                style = JasnifyTheme.typography.labelXLarge,
                                color = ContentPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Edit Details Button
            CustomTextButton(
                onClick = onEditClick,
                text = "Edit Details",
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Medium,
                type = ButtonType.Secondary,
                shapeStyle = ButtonShapeStyle.Square,
                containerColor = Color(0xFFDEE9E8), // Light teal-ish from image
                contentColor = Color(0xFF005858)   // Dark teal from image
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGuestInfoBottomSheet() {
    JasnifyTheme {
        GuestInfoBottomSheet(
            guest = Guest(
                name = "Akriti R.",
                type = "Close Friend",
                contactNo = "+91 9875462130",
                isInvited = true,
                invitedBy = "Anand K.",
                invitedAt = "Aug 24, 2025, 01:04pm"
            ),
            onDismiss = {},
            onEditClick = {},
            onInviteClick = {}
        )
    }
}
