package com.harshdeep.jasnify.presentation.screens.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil.compose.AsyncImage
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.presentation.components.bottomdrawer.common.CustomBottomSheet
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
    val haptic = LocalHapticFeedback.current

    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    var selectedMessageIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    val selectedMessages = remember(selectedMessageIds, messages) {
        messages.filter { it.id in selectedMessageIds }
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMessageInfoSheet by remember { mutableStateOf(false) }
    var messageToEdit by remember { mutableStateOf<ChatMessage?>(null) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(eventId, roomType) {
        if (eventId.isNotBlank()) {
            chatViewModel.loadMessages(eventId, roomType)
            roomViewModel.loadRoomUsers(eventId, roomType)
        }
    }

    // Call markRoomAsSeen when messages update ONLY if screen is actively resumed
    LaunchedEffect(messages, lifecycleState) {
        if (messages.isNotEmpty() && lifecycleState == Lifecycle.State.RESUMED) {
            chatViewModel.markRoomAsSeen()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        chatViewModel.markRoomAsSeen()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        if (messageToEdit == null) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = SurfacePrimary,
                tonalElevation = 2.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                if (selectedMessageIds.isNotEmpty()) {
                    val singleSelectedMessage = if (selectedMessages.size == 1) selectedMessages.first() else null
                    val canEdit = singleSelectedMessage != null && 
                            singleSelectedMessage.senderId == currentUserUid && 
                            !singleSelectedMessage.deletedForEveryone

                    CustomTopBar(
                        title = "${selectedMessageIds.size} selected",
                        onBackClick = { selectedMessageIds = emptySet() },
                        backIcon = TopIcon.Predefined.CLOSE,
                        buttonStyle = ButtonBackground.OPAQUE,
                        secondaryIcon = if (canEdit) TopIcon.CustomPainter(rememberVectorPainter(Icons.Rounded.Edit)) else null,
                        onSecondaryClick = if (canEdit) {
                            {
                                messageToEdit = singleSelectedMessage
                                messageText = singleSelectedMessage.text
                                selectedMessageIds = emptySet()
                            }
                        } else null,
                        tertiaryIcon = TopIcon.Predefined.DELETE,
                        onTertiaryClick = {
                            showDeleteDialog = true
                        },
                        menuIcon = if (singleSelectedMessage != null) TopIcon.CustomPainter(rememberVectorPainter(Icons.Rounded.Info)) else TopIcon.Predefined.MENU_VERTICAL,
                        onMenuClick = if (singleSelectedMessage != null) {
                            { showMessageInfoSheet = true }
                        } else null
                    )
                } else {
                    CustomTopBar(
                        title = "${roomType.replaceFirstChar { it.uppercase() }} Room",
                        subtitle = "${roomUsers.size} members",
                        image = painterResource(R.drawable.ic_user_profile),
                        onBackClick = onBackClick,
                        onDropdownClick = onMembersClick,
                        buttonStyle = ButtonBackground.OPAQUE,
                        backIcon = TopIcon.Predefined.BACK
                    )
                }
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
                        if (messageToEdit != null) {
                            chatViewModel.editMessage(messageToEdit!!.id, messageText)
                            messageToEdit = null
                        } else {
                            chatViewModel.sendMessage(eventId, roomType, messageText)
                        }
                        messageText = ""
                    },
                    focusRequester = focusRequester,
                    isEditing = messageToEdit != null,
                    onCancelEdit = {
                        messageToEdit = null
                        messageText = ""
                    }
                )
            }
        },
        containerColor = BackgroundPrimary,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    val isSentByMe = message.senderId == currentUserUid
                    val sender = roomUsers.find { it.uid == message.senderId }
                    
                    GroupMessageBubble(
                        message = message,
                        sender = sender,
                        isSentByMe = isSentByMe,
                        isSelected = message.id in selectedMessageIds,
                        totalMembers = roomUsers.size,
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            selectedMessageIds = if (message.id in selectedMessageIds) {
                                selectedMessageIds - message.id
                            } else {
                                selectedMessageIds + message.id
                            }
                        },
                        onClick = {
                            if (selectedMessageIds.isNotEmpty()) {
                                selectedMessageIds = if (message.id in selectedMessageIds) {
                                    selectedMessageIds - message.id
                                } else {
                                    selectedMessageIds + message.id
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && selectedMessages.isNotEmpty()) {
        val allSentByMe = selectedMessages.all { it.senderId == currentUserUid && !it.deletedForEveryone }
        val isSingle = selectedMessages.size == 1

        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = if (isSingle) "Delete Message?" else "Delete ${selectedMessages.size} Messages?",
                    style = JasnifyTheme.typography.headingMedium
                )
            },
            text = {
                Text(
                    text = if (isSingle) "Choose how you want to delete this message." else "Choose how you want to delete these messages.",
                    style = JasnifyTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedMessages.forEach { msg ->
                        chatViewModel.deleteMessageForMe(msg.id)
                    }
                    selectedMessageIds = emptySet()
                    showDeleteDialog = false
                }) {
                    Text("Delete for me", color = ContentPrimary)
                }
            },
            dismissButton = {
                Row {
                    if (allSentByMe) {
                        TextButton(onClick = {
                            selectedMessages.forEach { msg ->
                                chatViewModel.deleteMessageForEveryone(msg.id)
                            }
                            selectedMessageIds = emptySet()
                            showDeleteDialog = false
                        }) {
                            Text("Delete for everyone", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = ContentSecondary)
                    }
                }
            }
        )
    }

    if (showMessageInfoSheet && selectedMessages.size == 1) {
        MessageInfoBottomSheet(
            message = selectedMessages.first(),
            roomUsers = roomUsers,
            onDismiss = { 
                showMessageInfoSheet = false 
                selectedMessageIds = emptySet()
            }
        )
    }
}

@Composable
fun GroupMessageBubble(
    message: ChatMessage,
    sender: User?,
    isSentByMe: Boolean,
    isSelected: Boolean,
    totalMembers: Int,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val horizontalAlignment = if (isSentByMe) Alignment.End else Alignment.Start
    val bubbleColor = if (isSelected) {
        if (isSentByMe) SurfaceBrandPrimary.copy(alpha = 0.8f) else SurfaceSecondary.copy(alpha = 0.8f)
    } else {
        if (isSentByMe) SurfaceBrandSecondary else SurfaceSecondary
    }
    val contentColor = ContentPrimary

    val shape = if (isSentByMe) {
        SquircleShape(16.dp, 16.dp, 16.dp, 4.dp)
    } else {
        SquircleShape(16.dp, 16.dp, 4.dp, 16.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() },
                    onTap = { onClick() }
                )
            },
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isSentByMe) {
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                color = SurfaceSecondary
            ) {
                AsyncImage(
                    model = sender?.profilePictureUrl ?: R.drawable.ic_user_default,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = horizontalAlignment) {
            if (!isSentByMe) {
                Text(
                    text = sender?.name ?: "Unknown",
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
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    ) {
                        Text(
                            text = TimeUtils.formatChatTime(message.timestamp),
                            style = JasnifyTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = contentColor.copy(alpha = 0.7f),
                        )
                        if (isSentByMe && !message.deletedForEveryone) {
                            Spacer(modifier = Modifier.width(4.dp))
                            GroupMessageStatusTicks(
                                message = message,
                                totalMembers = totalMembers
                            )
                        }
                    }
                }
            }
        }
    }
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

@Composable
fun MessageInfoBottomSheet(
    message: ChatMessage,
    roomUsers: List<User>,
    onDismiss: () -> Unit
) {
    CustomBottomSheet(
        heading = "Message Info",
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Seen by",
                style = JasnifyTheme.typography.labelMedium,
                color = ContentSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            roomUsers.filter { it.uid != message.senderId }.forEach { user ->
                val seenAt = message.seenBy[user.uid]
                val delivered = message.deliveredTo.contains(user.uid)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = user.profilePictureUrl ?: R.drawable.ic_user_default,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = user.name, style = JasnifyTheme.typography.labelLarge)
                        Text(
                            text = if (seenAt != null) "Seen ${TimeUtils.formatChatTime(seenAt)}" 
                                   else if (delivered) "Delivered" 
                                   else "Sent",
                            style = JasnifyTheme.typography.labelSmall,
                            color = if (seenAt != null) Color(0xFF34B7F1) else ContentSecondary
                        )
                    }
                }
            }
        }
    }
}
