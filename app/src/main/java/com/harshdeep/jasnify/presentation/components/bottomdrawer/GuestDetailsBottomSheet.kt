package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.harshdeep.jasnify.presentation.components.buttons.CustomIconButton
import com.harshdeep.jasnify.presentation.components.buttons.CustomTextButton
import com.harshdeep.jasnify.presentation.components.others.DashedDivider
import com.harshdeep.jasnify.theme.ContentInvPrimary
import com.harshdeep.jasnify.theme.ContentPrimary
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.ContentTertiary
import com.harshdeep.jasnify.theme.CornerExtraLarge
import com.harshdeep.jasnify.theme.CornerLargeIncrease
import com.harshdeep.jasnify.theme.CornerSmoothingDefault
import com.harshdeep.jasnify.theme.JasnifyTheme
import com.harshdeep.jasnify.theme.SurfacePrimary
import com.harshdeep.jasnify.theme.SurfaceSecondary
import sv.lib.squircleshape.SquircleShape

@Composable
fun GuestDetailsBottomSheet(
    isViewer: Boolean = false,
    guest: Guest,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onInviteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    labelColor: Color = Color(0xFF635994),
    onProgress: (Float) -> Unit = {}
) {
    CustomBottomSheet(
        heading = "Guest Details",
        onDismiss = onDismiss,
        onProgress = onProgress,
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
                    .clip(SquircleShape(CornerExtraLarge, CornerSmoothingDefault))
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

                // Recent Activity Card (Moved to top of info card)
                if (guest.invited || !guest.invitedBy.isNullOrBlank() || !guest.lastUpdatedBy.isNullOrBlank() || !guest.addedBy.isNullOrBlank()) {
                    RecentActivityCard(guest = guest)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Info Card (Guest Type, Contact No)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault),
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

private fun formatUserName(name: String?): String {
    if (name.isNullOrBlank()) return "Anonymous"
    val parts = name.trim().split("\\s+".toRegex())
    return if (parts.size >= 2) "${parts[0]} ${parts[1].take(1)}." else parts[0]
}

@Composable
fun RecentActivityCard(
    guest: Guest,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "arrow_rotation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(200, easing = EaseInOut)),
        shape = SquircleShape(CornerLargeIncrease, CornerSmoothingDefault),
        color = SurfacePrimary
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { expanded = !expanded }
                )
                .padding(16.dp)
        ) {
            // Dropdown Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clock_forward),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = ContentPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recent Activity",
                        style = JasnifyTheme.typography.labelXLarge,
                        color = ContentPrimary
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_down),
                    contentDescription = if (expanded) "Collapse updates" else "Expand updates",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotationAngle),
                    tint = ContentPrimary
                )
            }

            // Expandable Content
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(150)) + expandVertically(tween(150)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(16.dp))

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
                    if (!guest.lastUpdatedBy.isNullOrBlank()) {
                        if (guest.invited || !guest.invitedBy.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        ActivityItem(
                            iconRes = R.drawable.ic_edit_pen,
                            label = "Last updated by",
                            userName = guest.lastUpdatedBy,
                            timestamp = guest.lastUpdatedAt
                        )
                    }

                    // Added Row
                    if (!guest.addedBy.isNullOrBlank()) {
                        if (guest.invited || !guest.invitedBy.isNullOrBlank() || !guest.lastUpdatedBy.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(12.dp))
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
        }
    }
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
            tint = ContentTertiary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = JasnifyTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light,
                    color = ContentTertiary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatUserName(userName),
                    style = JasnifyTheme.typography.bodyMedium,
                    color = ContentTertiary
                )
            }

            if (!timestamp.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timestamp,
                    style = JasnifyTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light,
                    color = ContentTertiary
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
                lastUpdatedBy = "Anand K.",
                lastUpdatedAt = "Aug 25, 2026, 09:44pm",
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
