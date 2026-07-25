package com.harshdeep.jasnify.presentation.screens.others

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.util.TimeUtils
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.flow.flowOf
import sv.lib.squircleshape.SquircleShape

@Composable
fun ChatScreen(
    merchantId: String? = null,
    venueId: String? = null,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    venueViewModel: VenueViewModel = hiltViewModel(),
    enquiryViewModel: EnquiryViewModel = hiltViewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""
    
    val allVenues by venueViewModel.allVenues.collectAsState()
    val venue = remember(venueId, allVenues) {
        allVenues.find { it.id == venueId }
    }

    val merchantProfile by remember(merchantId) {
        if (merchantId != null) {
            enquiryViewModel.getMerchantProfile(merchantId)
        } else {
            flowOf(null)
        }
    }.collectAsState(initial = null)

    val activeStatus = remember(merchantProfile) {
        merchantProfile?.lastActive?.let { TimeUtils.formatLastActive(it) } ?: "Active some time ago"
    }

    var messageText by remember { mutableStateOf("") }
    
    val messages by remember(currentUserUid, merchantId, venueId) {
        if (merchantId != null && venueId != null) {
            enquiryViewModel.getChatMessages(currentUserUid, merchantId, venueId)
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    val scrollState = rememberLazyListState()

    // Reactive Status Updates: Mark as Seen
    LaunchedEffect(messages, merchantId, venueId) {
        if (merchantId != null && venueId != null && messages.isNotEmpty()) {
            val hasUnseenIncoming = messages.any { 
                it.senderId != currentUserUid && it.status != MessageStatus.SEEN 
            }
            if (hasUnseenIncoming) {
                enquiryViewModel.markMessagesAsSeen(currentUserUid, merchantId, venueId)
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = SurfacePrimary,
                tonalElevation = 2.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                CustomTopBar(
                    title = venue?.name ?: "Merchant",
                    subtitle = activeStatus,
                    image = painterResource(R.drawable.ic_user_profile),
                    onBackClick = onBackClick,
                    onMenuClick = { /* Handle menu */ },
                    buttonStyle = ButtonBackground.OPAQUE,
                    backIcon = TopIcon.Predefined.BACK
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                ChatInputBar(
                    value = messageText,
                    onValueChange = { messageText = it },
                    onSendClick = {
                        if (messageText.isNotBlank() && merchantId != null && venueId != null) {
                            enquiryViewModel.sendMessage(
                                userId = currentUserUid,
                                merchantId = merchantId,
                                itemId = venueId,
                                itemName = venue?.name ?: "Venue",
                                text = messageText
                            )
                            messageText = ""
                        }
                    }
                )
            }
        },
        containerColor = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    isSentByMe = message.senderId == currentUserUid
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isSentByMe: Boolean
) {
    val horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start
    val bubbleColor = if (isSentByMe) SurfaceBrandSecondary else SurfaceSecondary
    val contentColor = if (isSentByMe) ContentPrimary else ContentPrimary
    
    val shape = if (isSentByMe) {
        SquircleShape(16.dp, 16.dp, 16.dp, 4.dp)
    } else {
        SquircleShape(16.dp, 16.dp, 4.dp, 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Surface(
            color = bubbleColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = JasnifyTheme.typography.labelLarge,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = TimeUtils.formatChatTime(message.timestamp),
                        style = JasnifyTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = contentColor.copy(alpha = 0.7f),
                    )
                    if (isSentByMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        MessageStatusTicks(status = message.status)
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

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(SurfaceSecondary, RoundedCornerShape(28.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Camera Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(SurfacePrimary, CircleShape)
                    .clickable { /* Handle camera */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "Camera",
                    tint = ContentPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Plus Button
            IconButton(
                onClick = { /* Handle plus */ },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "Add",
                    tint = ContentPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Text Input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Type a message...",
                        style = JasnifyTheme.typography.labelLarge,
                        color = ContentSecondary
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = JasnifyTheme.typography.labelLarge.copy(color = ContentPrimary),
                    cursorBrush = SolidColor(ContentPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Send Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (value.isNotBlank()) SurfacePrimary else SurfacePrimary.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .clickable(enabled = value.isNotBlank()) { onSendClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = "Send",
                    tint = if (value.isNotBlank()) ContentPrimary else ContentSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    JasnifyTheme {
        ChatScreen()
    }
}
