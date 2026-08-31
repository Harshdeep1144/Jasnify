package com.harshdeep.jasnify.presentation.components.chats

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.presentation.utils.TimeUtils
import com.harshdeep.jasnify.theme.*
import sv.lib.squircleshape.SquircleShape

@Composable
fun MessageBubble(
    message: ChatMessage,
    isSentByMe: Boolean,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    shouldAnimate: Boolean = false,
    sender: User? = null,
    totalMembers: Int? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val scale = remember { Animatable(if (shouldAnimate) 0f else 1f) }

    LaunchedEffect(Unit) {
        if (shouldAnimate) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.65f,
                    stiffness = 350f
                )
            )
        }
    }

    val transformOrigin = remember(isSentByMe) {
        if (isSentByMe) TransformOrigin(1f, 1f) else TransformOrigin(0f, 1f)
    }

    val horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start
    val bubbleColor = if (isSentByMe) SurfaceBrandSecondary else SurfaceSecondary
    val contentColor = ContentPrimary

    val shape = if (isSentByMe) {
        SquircleShape(16.dp, 16.dp, 16.dp, 4.dp)
    } else {
        SquircleShape(16.dp, 16.dp, 4.dp, 16.dp)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.transformOrigin = transformOrigin
            }
            .background(
                color = if (isSelected) Color.Black.copy(0.2f) else Color.Transparent
            )
            .then(
                if (onLongClick != null || onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongClick?.invoke() },
                            onTap = { onClick?.invoke() }
                        )
                    }
                } else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isSentByMe && sender != null) {
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    color = SurfaceSecondary
                ) {
                    AsyncImage(
                        model = sender.profilePictureUrl ?: R.drawable.ic_user_default,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(horizontalAlignment = horizontalAlignment) {
                if (!isSentByMe && sender != null) {
                    Text(
                        text = sender.name.ifBlank { "Unknown" },
                        style = JasnifyTheme.typography.labelSmall,
                        color = ContentSecondary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                    )
                }
                Surface(
                    color = bubbleColor,
                    shape = shape,
                    modifier = Modifier.widthIn(max = 260.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = message.text,
                            style = JasnifyTheme.typography.labelLarge,
                            color = if (message.deletedForEveryone) ContentSecondary else contentColor
                        )

                        if (message.isEdited && !message.deletedForEveryone) {
                            Text(
                                text = "edited",
                                style = JasnifyTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = contentColor.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 4.dp)
                        ) {
                            Text(
                                text = TimeUtils.formatChatTime(message.timestamp),
                                style = JasnifyTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = contentColor.copy(alpha = 0.7f)
                            )
                            if (isSentByMe && !message.deletedForEveryone) {
                                Spacer(modifier = Modifier.width(4.dp))
                                if (totalMembers != null) {
                                    GroupMessageStatusTicks(
                                        message = message,
                                        totalMembers = totalMembers
                                    )
                                } else {
                                    MessageStatusTick(status = message.status)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageStatusTick(status: MessageStatus) {
    val icon = when (status) {
        MessageStatus.SENT -> Icons.Rounded.Done
        MessageStatus.DELIVERED, MessageStatus.SEEN -> Icons.Rounded.DoneAll
    }
    val color = when (status) {
        MessageStatus.SEEN -> Color(0xFF34B7F1)
        else -> ContentSecondary
    }
    Icon(
        imageVector = icon,
        contentDescription = status.name,
        modifier = Modifier.size(16.dp),
        tint = color
    )
}

@Composable
fun GroupMessageStatusTicks(
    message: ChatMessage,
    totalMembers: Int
) {
    val othersCount = (totalMembers - 1).coerceAtLeast(1)
    val deliveredCount = message.deliveredTo.size
    val seenCount = message.seenBy.size

    val isSeenByAll = seenCount >= othersCount
    val isDeliveredToAll = deliveredCount >= othersCount

    val tickColor = if (isSeenByAll) Color(0xFF34B7F1) else ContentSecondary

    when {
        isSeenByAll -> {
            Icon(
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = "Seen by all",
                modifier = Modifier.size(16.dp),
                tint = tickColor
            )
        }
        isDeliveredToAll -> {
            Icon(
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = "Delivered to all",
                modifier = Modifier.size(16.dp),
                tint = tickColor
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = "Sent",
                modifier = Modifier.size(16.dp),
                tint = tickColor
            )
        }
    }
}





@Preview(name = "Chat Bubble Variations", showBackground = true)
@Composable
private fun MessageBubblePreview() {
    val sampleSender = User(
        uid = "user_2",
        name = "Alex Rivera",
        profilePictureUrl = null
    )

    JasnifyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column{
                // Incoming message with sender info
                MessageBubble(
                    message = ChatMessage(
                        id = "1",
                        text = "Hey! Did you check out the new design update?",
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.SEEN
                    ),
                    isSentByMe = false,
                    sender = sampleSender
                )

                // Outgoing message (Seen status)
                MessageBubble(
                    message = ChatMessage(
                        id = "2",
                        text = "Yes, looks super clean! Especially the squircle shapes.",
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.SEEN
                    ),
                    isSentByMe = true
                )

                // Outgoing edited message (Delivered status)
                MessageBubble(
                    message = ChatMessage(
                        id = "3",
                        text = "Let's deploy it to production today.",
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.DELIVERED,
                        isEdited = true
                    ),
                    isSentByMe = true
                )

                // Outgoing group message (Sent status)
                MessageBubble(
                    message = ChatMessage(
                        id = "4",
                        text = "Waiting on QA approval first.",
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.SENT,
                        deliveredTo = listOf("user_2"),
                        seenBy = emptyMap()
                    ),
                    isSentByMe = true,
                    totalMembers = 4
                )

                // Selected message state
                MessageBubble(
                    message = ChatMessage(
                        id = "5",
                        text = "This message is currently selected.",
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.SEEN,
                        isEdited = true
                    ),
                    isSentByMe = false,
                    sender = sampleSender,
                    isSelected = true,
                )

                // Deleted for everyone
                MessageBubble(
                    message = ChatMessage(
                        id = "6",
                        text = "This message was deleted",
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.SEEN,
                        deletedForEveryone = true,
                        isEdited = true
                    ),
                    isSentByMe = true,
                    isSelected = true
                )
            }
        }
    }
}