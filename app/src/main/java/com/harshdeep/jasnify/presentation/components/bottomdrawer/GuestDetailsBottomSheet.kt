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

                // Info Card
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

                if (guest.invited || !guest.invitedBy.isNullOrBlank() || !guest.lastUpdatedBy.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom Updates Dropdown
                    UpdatesDropdownCard(guest = guest)
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
fun UpdatesDropdownCard(
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
                Text(
                    text = "Updates",
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentPrimary
                )
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
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_tick2),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp),
                                tint = ContentTertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val formattedName = guest.invitedBy?.trim()?.split("\\s+".toRegex()).let { parts ->
                                        parts?.size?.let { if (it >= 2) "${parts[0]} ${parts[1].take(1)}." else parts[0] }
                                    }
                                    Text(
                                        text = "Invited by",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Light,
                                        color = ContentTertiary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (!guest.invitedBy.isNullOrBlank()) "$formattedName" else "Anonymous",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        color = ContentTertiary
                                    )
                                }

                                if (!guest.invitedAt.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = guest.invitedAt ?: "",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Light,
                                        color = ContentTertiary
                                    )
                                }
                            }
                        }
                    }

                    // Last Updated Row
                    if (!guest.lastUpdatedBy.isNullOrBlank()) {
                        if (guest.invited || !guest.invitedBy.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit_pen),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp),
                                tint = ContentTertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                val formattedName = guest.lastUpdatedBy.trim()?.split("\\s+".toRegex()).let { parts ->
                                    parts?.size?.let { if (it >= 2) "${parts[0]} ${parts[1].take(1)}." else parts[0] }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Last updated by",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Light,
                                        color = ContentTertiary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (guest.lastUpdatedBy.isNotBlank()) "$formattedName" else "Anonymous",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        color = ContentTertiary
                                    )
                                }
                                if (!guest.lastUpdatedAt.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = guest.lastUpdatedAt ?: "",
                                        style = JasnifyTheme.typography.bodyMedium,
                                        color = ContentTertiary
                                    )
                                }
                            }
                        }
                    }
                }
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
                invitedAt = "Aug 24, 2025, 01:04pm",
                lastUpdatedBy = "Anand K.",
                lastUpdatedAt = "Aug 24, 2025, 01:04pm"
            ),
            onDismiss = {},
            onEditClick = {},
            onInviteClick = {},
            onDeleteClick = {}
        )
    }
}