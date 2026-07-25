package com.harshdeep.jasnify.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
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
    
    Surface(
        onClick = onClick,
        color = SurfaceSecondary,
        shape = SquircleShape(CornerLarge),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceBrandSecondary)
            ) {
                AsyncImage(
                    model = R.drawable.ic_user_profile,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = enquiry.venueName,
                    style = JasnifyTheme.typography.labelXLarge,
                    color = ContentPrimary
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (lastMsg != null && lastMsg.senderId == currentUserId) {
                        MessageStatusTicks(status = lastMsg.status)
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = enquiry.lastMessage.ifBlank { "No messages yet" },
                        style = JasnifyTheme.typography.labelMedium,
                        color = if (enquiry.lastMessage.isBlank()) ContentTertiary else ContentSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = formatTime(enquiry.timestamp),
                style = JasnifyTheme.typography.labelSmall,
                color = ContentTertiary
            )
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
                modifier = Modifier.size(16.dp),
                tint = tickColor
            )
        }
        MessageStatus.DELIVERED, MessageStatus.SEEN -> {
            Icon(
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = tickColor
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
