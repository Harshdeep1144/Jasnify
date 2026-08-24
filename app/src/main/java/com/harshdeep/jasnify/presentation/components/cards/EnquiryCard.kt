package com.harshdeep.jasnify.presentation.components.cards

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.Enquiry
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EnquiryCard(
    enquiry: Enquiry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentUserId: String = ""
) {
    val lastMsg = enquiry.messages.lastOrNull()
    val hasUnread = enquiry.unreadCount > 0

    Surface(
        onClick = onClick,
        color = SurfaceSecondary,
        shape = SquircleShape(CornerMedium, CornerSmoothingDefault),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Squircle styling & Brand Tint Fallback
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(SurfaceBrandSecondary)
            ) {
                AsyncImage(
                    model = enquiry.merchantProfileUrl ?: R.drawable.ic_user_profile,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(12.dp))

            // Main Content Area
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Top Row: Venue Name & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = enquiry.venueName.ifBlank { "Direct Enquiry" },
                        style = JasnifyTheme.typography.labelXLarge.copy(
                            fontWeight = if (hasUnread) FontWeight.Medium else FontWeight.Normal
                        ),
                        color = ContentPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = formatTime(enquiry.timestamp),
                        style = JasnifyTheme.typography.labelSmall,
                        color = if (hasUnread) ContentPrimary else ContentTertiary
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Bottom Row: Category Tag, Tick Status, Message Preview & Unread Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Micro category tag (Venue / Vendor)
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent)
                    ) {
                        Text(
                            text = enquiry.itemType.uppercase(Locale.getDefault()),
                            style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = ContentSecondary
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    if (lastMsg != null && lastMsg.senderId == currentUserId) {
                        MessageStatusTicks(status = lastMsg.status)
                        Spacer(Modifier.width(4.dp))
                    }

                    Text(
                        text = enquiry.lastMessage,
                        style = JasnifyTheme.typography.labelMedium.copy(
                            fontWeight = if (hasUnread) FontWeight.Medium else FontWeight.Normal
                        ),
                        color = if (hasUnread) ContentPrimary else if (enquiry.lastMessage.isBlank()) ContentTertiary else ContentSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Unread Count Badge
                    if (hasUnread) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(ContentBrand),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (enquiry.unreadCount > 99) "99+" else enquiry.unreadCount.toString(),
                                style = JasnifyTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = SurfacePrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageStatusTicks(status: MessageStatus) {
    val tickColor = if (status == MessageStatus.SEEN) Color(0xFF34B7F1) else ContentSecondary
    when (status) {
        MessageStatus.SENT -> {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = tickColor
            )
        }
        MessageStatus.DELIVERED, MessageStatus.SEEN -> {
            Icon(
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = tickColor
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val now = Calendar.getInstance()
    val msgTime = Calendar.getInstance().apply { timeInMillis = timestamp }

    return when {
        now.get(Calendar.DATE) == msgTime.get(Calendar.DATE) &&
                now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) -> {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
        }
        now.get(Calendar.DATE) - msgTime.get(Calendar.DATE) == 1 &&
                now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR) -> {
            "Yesterday"
        }
        else -> {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}




@Preview(showBackground = true, name = "Enquiry Cards Showcase", backgroundColor = 0xFFF8F9FA)
@Composable
private fun EnquiryCardShowcasePreview() {
    val currentUserId = "user_me"

    val unreadVenueEnquiry = Enquiry(
        id = "1",
        venueName = "Royal Orchid Palace",
        itemType = "Venue",
        lastMessage = "Yes, the hall is available for your selected date!",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 15, // 15 mins ago
        unreadCount = 3,
        messages = listOf(
            ChatMessage(
                id = "m1",
                senderId = "merchant_1",
                text = "Yes, the hall is available for your selected date!",
                status = MessageStatus.DELIVERED
            )
        )
    )

    val sentVendorEnquiry = Enquiry(
        id = "2",
        venueName = "Elite Lens Photography",
        itemType = "Vendor",
        lastMessage = "Could you share the pre-wedding portfolio?",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2, // 2 hours ago
        unreadCount = 0,
        messages = listOf(
            ChatMessage(
                id = "m2",
                senderId = currentUserId,
                text = "Could you share the pre-wedding portfolio?",
                status = MessageStatus.SEEN
            )
        )
    )

    val emptyMessageEnquiry = Enquiry(
        id = "3",
        venueName = "Grand Heritage Resort",
        itemType = "Venue",
        lastMessage = "",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // Yesterday
        unreadCount = 0,
        messages = emptyList()
    )

    JasnifyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnquiryCard(
                enquiry = unreadVenueEnquiry,
                onClick = {},
                currentUserId = currentUserId
            )
            EnquiryCard(
                enquiry = sentVendorEnquiry,
                onClick = {},
                currentUserId = currentUserId
            )
            EnquiryCard(
                enquiry = emptyMessageEnquiry,
                onClick = {},
                currentUserId = currentUserId
            )
        }
    }
}