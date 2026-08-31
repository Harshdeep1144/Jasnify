package com.harshdeep.jasnify.presentation.components.bottomdrawer.guests

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.components.buttons.ButtonShapeStyle
import com.harshdeep.jasnify.presentation.components.buttons.ButtonSize
import com.harshdeep.jasnify.presentation.components.buttons.ButtonType
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

private val GuestDetailsOuterShape = SquircleShape(CornerExtraLarge, CornerSmoothingDefault)
private val GuestDetailsCardShape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault)
private val WhitespaceRegex = Regex("\\s+")

@Composable
fun GuestDetailsBottomSheet(
    isViewer: Boolean = false,
    guest: Guest,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onInviteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRecentActivityClick: () -> Unit = {},
    labelColor: Color = Color(0xFF635994),
    onProgress: (Float) -> Unit = {}
) {
    CustomBottomSheet(
        heading = "Guest Details",
        onDismiss = onDismiss,
        onProgress = onProgress,
        showDragHandle = false,
        sheetHeight = null
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
                    .clip(GuestDetailsOuterShape)
                    .background(SurfaceSecondary)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(ContentSecondary)
                ) {
                    if (guest.imageUrl != null) {
                        AsyncImage(
                            model = guest.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                            error = painterResource(id = R.drawable.ic_profile_placeholder)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_placeholder),
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
                    fontWeight = FontWeight.Medium,
                    color = ContentPrimary
                )

                if (!isViewer) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Invite Button
                    CustomTextButton(
                        onClick = onInviteClick,
                        text = if (guest.invited) "Invited" else "Mark as invited",
                        size = ButtonSize.Small,
                        type = ButtonType.Primary,
                        shapeStyle = ButtonShapeStyle.Round,
                        containerColor = if (guest.invited) ContentInvPrimary else ContentPrimary,
                        contentColor = if (guest.invited) ContentPrimary else ContentInvPrimary,
                        enabled = true,
                        leadingIcon = if (guest.invited) painterResource(R.drawable.ic_check) else null,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Activity Row
                if (guest.invited || !guest.invitedBy.isNullOrBlank() || !guest.updatedBy.isNullOrBlank() || !guest.addedBy.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(GuestDetailsCardShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onRecentActivityClick
                            ),
                        color = SurfacePrimary,
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_clock_forward),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = ContentSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recent Activity",
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentSecondary
                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_right_chevron),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = ContentPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Info Card (Guest Type, Contact No)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = GuestDetailsCardShape,
                    color = SurfacePrimary
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Guest Type
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = SurfaceSecondary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_default_user_curved),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = ContentSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Guest Type",
                                    style = JasnifyTheme.typography.labelLarge,
                                    color = ContentSecondary
                                )
                                Text(
                                    text = guest.type,
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = labelColor,
                                )
                            }
                        }

                        DashedDivider()

                        // Contact No.
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = SurfaceSecondary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_phone),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = ContentSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Contact No.",
                                    style = JasnifyTheme.typography.labelLarge,
                                    color = ContentSecondary
                                )
                                Text(
                                    text = guest.contactNo.ifEmpty { "Not Provided" },
                                    style = JasnifyTheme.typography.labelXLarge,
                                    color = ContentPrimary,
                                )
                            }
                        }
                    }
                }
            }

            if (!isViewer) {
                Spacer(Modifier.height(24.dp))

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconButton(
                        onClick = onDeleteClick,
                        icon = painterResource(R.drawable.ic_delete),
                        shapeStyle = ButtonShapeStyle.Square,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )

                    CustomTextButton(
                        onClick = onEditClick,
                        text = "Edit Details",
                        modifier = Modifier.weight(1f),
                        type = ButtonType.Secondary,
                        shapeStyle = ButtonShapeStyle.Square,
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivityBottomSheet(
    guest: Guest,
    onDismiss: () -> Unit,
    onProgress: (Float) -> Unit = {}
) {
    CustomBottomSheet(
        heading = "Recent Activity",
        onDismiss = onDismiss,
        onProgress = onProgress,
        showDragHandle = false,
        sheetHeight = 312.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(GuestDetailsCardShape)
                    .border(1.dp, SurfaceSecondary, GuestDetailsCardShape),
                color = SurfacePrimary,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Invited Row
                    if (guest.invited || !guest.invitedBy.isNullOrBlank()) {
                        ActivityItem(
                            iconRes = R.drawable.ic_tick2,
                            label = "Invited by",
                            userName = guest.invitedBy,
                            timestamp = guest.invitedAt
                        )
                    }

                    // Last Updated Row
                    if (!guest.updatedBy.isNullOrBlank()) {
                        if (guest.invited || !guest.invitedBy.isNullOrBlank()) {
                            DashedDivider()
                        }
                        ActivityItem(
                            iconRes = R.drawable.ic_edit_pen,
                            label = "Last Edited by",
                            userName = guest.updatedBy,
                            timestamp = guest.updatedAt
                        )
                    }

                    // Added Row
                    if (!guest.addedBy.isNullOrBlank()) {
                        if (guest.invited || !guest.invitedBy.isNullOrBlank() || !guest.updatedBy.isNullOrBlank()) {
                            DashedDivider()
                        }
                        ActivityItem(
                            iconRes = R.drawable.ic_add_circle,
                            label = "Added by",
                            userName = guest.addedBy,
                            timestamp = guest.addedAt
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatUserName(name: String?): String {
    if (name.isNullOrBlank()) return "Anonymous"
    val parts = name.trim().split(WhitespaceRegex)
    return if (parts.size >= 2) "${parts[0]} ${parts[1].take(1)}." else parts[0]
}

@Composable
private fun ActivityItem(
    iconRes: Int,
    label: String,
    userName: String?,
    timestamp: String?
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = ContentSecondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = JasnifyTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light,
                    color = ContentSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatUserName(userName),
                    style = JasnifyTheme.typography.bodyLarge,
                    color = ContentSecondary
                )
            }

            if (!timestamp.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timestamp,
                    style = JasnifyTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light,
                    color = ContentSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGuestInfoBottomSheet() {
    JasnifyTheme {
        GuestDetailsBottomSheet(
            guest = Guest(
                name = "Akriti R.",
                type = "Close Friend",
                contactNo = "+91 9875462130",
                invited = true,
                invitedBy = "Anand K.",
                invitedAt = "Aug 30, 2026, 12:09pm",
                updatedBy = "Anand K.",
                updatedAt = "Aug 25, 2026, 09:44pm",
                addedBy = "Steve R.",
                addedAt = "Aug 24, 2026, 01:04pm"
            ),
            onDismiss = {},
            onEditClick = {},
            onInviteClick = {},
            onDeleteClick = {}
        )
    }
}