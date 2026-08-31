package com.harshdeep.jasnify.presentation.screens.chats

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil.compose.rememberAsyncImagePainter
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.chats.ChatInputBar
import com.harshdeep.jasnify.presentation.components.chats.DeleteMessageConfirmationDialog
import com.harshdeep.jasnify.presentation.components.chats.MessageBubble
import com.harshdeep.jasnify.presentation.components.chats.MessageInfoBottomSheet
import com.harshdeep.jasnify.presentation.viewmodels.RoomChatViewModel
import com.harshdeep.jasnify.presentation.viewmodels.RoomViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

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
    val roomPictureUrl by roomViewModel.roomPictureUrl.collectAsState()
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

    var initialLoadDone by remember { mutableStateOf(false) }
    var knownMessageIds by remember { mutableStateOf(setOf<String>()) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    // Reversed message list for reverseLayout chat UI
    val reversedMessages = remember(messages) {
        messages.asReversed()
    }

    // Handle system back button / back gesture when messages are selected or dialogs are shown
    BackHandler(enabled = selectedMessageIds.isNotEmpty() || showDeleteDialog || showMessageInfoSheet) {
        if (showDeleteDialog) {
            showDeleteDialog = false
        } else if (showMessageInfoSheet) {
            showMessageInfoSheet = false
        } else if (selectedMessageIds.isNotEmpty()) {
            selectedMessageIds = emptySet()
        }
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            if (!initialLoadDone) {
                knownMessageIds = messages.map { it.id }.toSet()
                initialLoadDone = true
            } else {
                val newIds = messages.map { it.id }.toSet() - knownMessageIds
                if (newIds.isNotEmpty()) {
                    knownMessageIds = knownMessageIds + newIds
                }
            }
        }
    }

    LaunchedEffect(eventId, roomType) {
        if (eventId.isNotBlank()) {
            chatViewModel.loadMessages(eventId, roomType)
            roomViewModel.loadRoomUsers(eventId, roomType)
            roomViewModel.loadRoomPicture(eventId, roomType)
        }
    }

    LaunchedEffect(messages, lifecycleState) {
        if (messages.isNotEmpty() && lifecycleState == Lifecycle.State.RESUMED) {
            chatViewModel.markRoomAsSeen()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        chatViewModel.markRoomAsSeen()
    }

    // Scroll to the latest message (index 0 in reversed list) when messages update
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(0)
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
                    val editWindowMs = 15 * 60 * 1000L // 15 minutes
                    val canEdit = singleSelectedMessage != null && 
                            singleSelectedMessage.senderId == currentUserUid && 
                            !singleSelectedMessage.deletedForEveryone &&
                            (System.currentTimeMillis() - singleSelectedMessage.timestamp) <= editWindowMs

                    CustomTopBar(
                        title = "${selectedMessageIds.size} selected",
                        onBackClick = { selectedMessageIds = emptySet() },
                        backIcon = TopIcon.Predefined.CLOSE,
                        buttonStyle = ButtonBackground.OPAQUE,
                        secondaryIcon = if (canEdit) TopIcon.CustomPainter(painterResource(R.drawable.ic_edit_pen)) else null,
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
                        menuIcon = if (singleSelectedMessage != null) TopIcon.CustomPainter(painterResource(R.drawable.ic_info)) else TopIcon.Predefined.MENU_VERTICAL,
                        onMenuClick = if (singleSelectedMessage != null) {
                            { showMessageInfoSheet = true }
                        } else null
                    )
                } else {
                    CustomTopBar(
                        title = "${roomType.replaceFirstChar { it.uppercase() }} Room",
                        subtitle = "${roomUsers.size} members",
                        image = if (!roomPictureUrl.isNullOrBlank()) {
                            rememberAsyncImagePainter(roomPictureUrl)
                        } else {
                            painterResource(R.drawable.ic_user_profile)
                        },
                        onBackClick = onBackClick,
                        onDropdownClick = onMembersClick,
                        buttonStyle = ButtonBackground.TRANSPARENT,
                        backIcon = TopIcon.Predefined.BACK_2
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(BackgroundPrimary)
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
        LazyColumn(
            state = scrollState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(reversedMessages, key = { it.id }) { message ->
                val isSentByMe = message.senderId == currentUserUid
                val sender = roomUsers.find { it.uid == message.senderId }
                val shouldAnimate = initialLoadDone && message.id !in knownMessageIds

                MessageBubble(
                    message = message,
                    sender = sender,
                    isSentByMe = isSentByMe,
                    isSelected = message.id in selectedMessageIds,
                    shouldAnimate = shouldAnimate,
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

    if (showDeleteDialog && selectedMessages.isNotEmpty()) {
        val allSentByMe = selectedMessages.all { it.senderId == currentUserUid && !it.deletedForEveryone }

        DeleteMessageConfirmationDialog(
            messageCount = selectedMessages.size,
            canDeleteForEveryone = allSentByMe,
            onDismissRequest = { showDeleteDialog = false },
            onDeleteForMe = {
                selectedMessages.forEach { msg ->
                    chatViewModel.deleteMessageForMe(msg.id)
                }
                selectedMessageIds = emptySet()
            },
            onDeleteForEveryone = {
                selectedMessages.forEach { msg ->
                    chatViewModel.deleteMessageForEveryone(msg.id)
                }
                selectedMessageIds = emptySet()
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
