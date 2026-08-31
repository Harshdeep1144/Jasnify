package com.harshdeep.jasnify.presentation.components.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
import com.harshdeep.jasnify.presentation.utils.TimeUtils
import com.harshdeep.jasnify.theme.ContentSecondary
import com.harshdeep.jasnify.theme.JasnifyTheme

@Composable
fun MessageInfoBottomSheet(
    message: ChatMessage,
    roomUsers: List<User>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val otherUsers = remember(roomUsers, message.senderId) {
        roomUsers.filter { it.uid != message.senderId }
    }

    val seenUsers = remember(otherUsers, message.seenBy) {
        otherUsers.filter { message.seenBy.containsKey(it.uid) }
    }

    val deliveredUsers = remember(otherUsers, message.deliveredTo, message.seenBy) {
        otherUsers.filter { message.deliveredTo.contains(it.uid) && !message.seenBy.containsKey(it.uid) }
    }

    val sentUsers = remember(otherUsers, message.deliveredTo, message.seenBy) {
        otherUsers.filter { !message.deliveredTo.contains(it.uid) && !message.seenBy.containsKey(it.uid) }
    }

    CustomBottomSheet(
        heading = "Message Info",
        showDragHandle = false,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (otherUsers.isEmpty()) {
                // 1-on-1 Chat Info
                Text(
                    text = "Status",
                    style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = ContentSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val statusText = when (message.status) {
                    MessageStatus.SEEN -> "Seen ${TimeUtils.formatChatTime(message.timestamp)}"
                    MessageStatus.DELIVERED -> "Delivered"
                    MessageStatus.SENT -> "Sent"
                }
                val statusColor = if (message.status == MessageStatus.SEEN) Color(0xFF1A7FAF) else ContentSecondary
                Text(
                    text = statusText,
                    style = JasnifyTheme.typography.bodyLarge,
                    color = statusColor
                )
            } else {
                // Group Chat Info
                if (seenUsers.isNotEmpty()) {
                    Text(
                        text = "Seen by (${seenUsers.size})",
                        style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = ContentSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    seenUsers.forEach { user ->
                        val seenAt = message.seenBy[user.uid]
                        UserStatusRow(
                            user = user,
                            statusText = if (seenAt != null) "Seen ${TimeUtils.formatChatTime(seenAt)}" else "Seen",
                            statusColor = Color(0xFF1A7FAF)
                        )
                    }

                    if (deliveredUsers.isNotEmpty() || sentUsers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (deliveredUsers.isNotEmpty()) {
                    Text(
                        text = "Delivered to (${deliveredUsers.size})",
                        style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = ContentSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    deliveredUsers.forEach { user ->
                        UserStatusRow(
                            user = user,
                            statusText = "Delivered",
                            statusColor = ContentSecondary
                        )
                    }

                    if (sentUsers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (sentUsers.isNotEmpty()) {
                    Text(
                        text = "Sent to (${sentUsers.size})",
                        style = JasnifyTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = ContentSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    sentUsers.forEach { user ->
                        UserStatusRow(
                            user = user,
                            statusText = "Sent",
                            statusColor = ContentSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserStatusRow(
    user: User,
    statusText: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profilePictureUrl ?: R.drawable.ic_user_profile,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name.ifBlank { user.email },
                style = JasnifyTheme.typography.labelLarge
            )
            Text(
                text = statusText,
                style = JasnifyTheme.typography.labelSmall,
                color = statusColor
            )
        }
    }
}

// ========================================================= Preview ========================================================


private val previewUsers = listOf(
    User(
        uid = "user_1",
        name = "Harsh Deep",
        username = "harshdeep",
        email = "harsh@example.com",
        role = UserRole.OWNER,
        profilePictureUrl = null
    ),
    User(
        uid = "user_2",
        name = "Jane Doe",
        username = "janedoe",
        email = "jane@example.com",
        role = UserRole.EDITOR,
        profilePictureUrl = null
    ),
    User(
        uid = "user_3",
        name = "Alex Smith",
        username = "alexsmith",
        email = "alex@example.com",
        role = UserRole.VIEWER,
        profilePictureUrl = null
    )
)

@Preview(name = "Message Info - 1-on-1 Chat", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MessageInfoOneOnOnePreview() {
    JasnifyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            MessageInfoBottomSheet(
                message = ChatMessage(
                    id = "msg_1",
                    senderId = "current_user",
                    text = "Hello there!",
                    timestamp = System.currentTimeMillis() - 3600000,
                    status = MessageStatus.SEEN
                ),
                roomUsers = emptyList(),
                onDismiss = {}
            )
        }
    }
}

@Preview(name = "Message Info - Group Chat", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MessageInfoGroupPreview() {
    JasnifyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            MessageInfoBottomSheet(
                message = ChatMessage(
                    id = "msg_2",
                    senderId = "current_user",
                    text = "Meeting is confirmed for tomorrow.",
                    timestamp = System.currentTimeMillis() - 1800000,
                    status = MessageStatus.DELIVERED,
                    seenBy = mapOf("user_1" to (System.currentTimeMillis() - 900000)),
                    deliveredTo = listOf("user_1", "user_2")
                ),
                roomUsers = previewUsers,
                onDismiss = {}
            )
        }
    }
}