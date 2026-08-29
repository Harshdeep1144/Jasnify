package com.harshdeep.jasnify.presentation.screens.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.utils.TimeUtils
import com.harshdeep.jasnify.presentation.viewmodels.RoomChatViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import sv.lib.squircleshape.SquircleShape

@Composable
fun GroupChatScreen(
    eventId: String,
    roomType: String,
    onBackClick: () -> Unit,
    onMembersClick: () -> Unit,
    modifier: Modifier = Modifier,
    chatViewModel: RoomChatViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
) {
    val messages by chatViewModel.messages.collectAsState()
    val roomUsers by roomViewModel.roomUsers.collectAsState()
    val currentUserUid = chatViewModel.currentUserUid

    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(eventId, roomType) {
        if (eventId.isNotBlank()) {
            chatViewModel.loadMessages(eventId, roomType)
            roomViewModel.loadRoomUsers(eventId, roomType)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            Surface(
                color = SurfacePrimary,
                tonalElevation = 2.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                CustomTopBar(
                    title = "${roomType.replaceFirstChar { it.uppercase() }} Room",
                    subtitle = "${roomUsers.size} members",
                    image = painterResource(R.drawable.ic_user_profile), // Should ideally be room icon
                    onBackClick = onBackClick,
                    onDropdownClick = onMembersClick, // Clicking top bar opens members
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
                        chatViewModel.sendMessage(eventId, roomType, messageText)
                        messageText = ""
                    },
                    focusRequester = focusRequester
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
                val isSentByMe = message.senderId == currentUserUid
                val sender = roomUsers.find { it.uid == message.senderId }
                
                GroupMessageBubble(
                    message = message,
                    senderName = if (isSentByMe) "You" else sender?.name ?: "Unknown",
                    isSentByMe = isSentByMe
                )
            }
        }
    }
}

@Composable
fun GroupMessageBubble(
    message: ChatMessage,
    senderName: String,
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
        if (!isSentByMe) {
            Text(
                text = senderName,
                style = JasnifyTheme.typography.labelSmall,
                color = ContentSecondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
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
