package com.harshdeep.jasnify.presentation.screens.chats

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.presentation.components.buttons.ButtonBackground
import com.harshdeep.jasnify.presentation.components.buttons.TopIcon
import com.harshdeep.jasnify.presentation.components.scaffold.CustomTopBar
import com.harshdeep.jasnify.presentation.components.chats.ChatInputBar
import com.harshdeep.jasnify.presentation.components.chats.DeleteMessageConfirmationDialog
import com.harshdeep.jasnify.presentation.components.chats.MessageBubble
import com.harshdeep.jasnify.presentation.components.chats.MessageInfoBottomSheet
import com.harshdeep.jasnify.presentation.utils.TimeUtils
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf

@Composable
fun ChatScreen(
    merchantId: String? = null,
    itemId: String? = null,
    itemType: String = "Venue",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    venueViewModel: VenueViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    enquiryViewModel: EnquiryViewModel = hiltViewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    val allVenues by venueViewModel.allVenues.collectAsState()
    val allVendors by vendorViewModel.allVendors.collectAsState()

    val merchantInfo = remember(itemId, itemType, allVenues, allVendors) {
        if (itemType == "Venue") {
            val v = allVenues.find { it.id == itemId }
            Pair(v?.name, v?.phoneNumber)
        } else {
            val v = allVendors.find { it.id == itemId }
            Pair(v?.name, v?.phoneNumber)
        }
    }
    val itemName = merchantInfo.first ?: "Merchant"
    val itemPhoneNumber = merchantInfo.second

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
    var messageToEdit by remember { mutableStateOf<ChatMessage?>(null) }

    val messages by remember(currentUserUid, merchantId, itemId) {
        if (merchantId != null && itemId != null) {
            enquiryViewModel.getChatMessages(currentUserUid, merchantId, itemId)
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    val scrollState = rememberLazyListState()

    // Reactive Status Updates: Mark as Seen
    LaunchedEffect(messages, merchantId, itemId) {
        if (merchantId != null && itemId != null && messages.isNotEmpty()) {
            val hasUnseenIncoming = messages.any {
                it.senderId != currentUserUid && it.status != MessageStatus.SEEN
            }
            if (hasUnseenIncoming) {
                enquiryViewModel.markMessagesAsSeen(currentUserUid, merchantId, itemId)
            }
        }
    }

    ChatContent(
        itemName = itemName,
        activeStatus = activeStatus,
        messages = messages,
        currentUserUid = currentUserUid,
        messageText = messageText,
        onMessageChange = { messageText = it },
        onSendClick = {
            if (messageText.isNotBlank() && merchantId != null && itemId != null) {
                if (messageToEdit != null) {
                    enquiryViewModel.editMessage(currentUserUid, merchantId, itemId, messageToEdit!!.id, messageText)
                    messageToEdit = null
                } else {
                    enquiryViewModel.sendMessage(
                        userId = currentUserUid,
                        merchantId = merchantId,
                        itemId = itemId,
                        itemName = itemName,
                        itemType = itemType,
                        text = messageText,
                        merchantProfileUrl = merchantProfile?.profilePictureUrl,
                        merchantPhoneNumber = itemPhoneNumber
                    )
                }
                messageText = ""
            }
        },
        messageToEdit = messageToEdit,
        onStartEdit = { msg ->
            messageToEdit = msg
            messageText = msg.text
        },
        onCancelEdit = {
            messageToEdit = null
            messageText = ""
        },
        onDeleteForMe = { msgIds ->
            if (merchantId != null && itemId != null) {
                msgIds.forEach { id ->
                    enquiryViewModel.deleteMessageForMe(currentUserUid, merchantId, itemId, id)
                }
            }
        },
        onDeleteForEveryone = { msgIds ->
            if (merchantId != null && itemId != null) {
                msgIds.forEach { id ->
                    enquiryViewModel.deleteMessageForEveryone(currentUserUid, merchantId, itemId, id)
                }
            }
        },
        onBackClick = onBackClick,
        scrollState = scrollState,
        modifier = modifier
    )
}

@Composable
fun ChatContent(
    itemName: String,
    activeStatus: String,
    messages: List<ChatMessage>,
    currentUserUid: String,
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    messageToEdit: ChatMessage? = null,
    onStartEdit: (ChatMessage) -> Unit = {},
    onCancelEdit: () -> Unit = {},
    onDeleteForMe: (Set<String>) -> Unit = {},
    onDeleteForEveryone: (Set<String>) -> Unit = {},
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    val haptic = LocalHapticFeedback.current
    val focusRequester = remember { FocusRequester() }

    var selectedMessageIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    val selectedMessages = remember(selectedMessageIds, messages) {
        messages.filter { it.id in selectedMessageIds }
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMessageInfoSheet by remember { mutableStateOf(false) }

    var initialLoadDone by remember { mutableStateOf(false) }
    var knownMessageIds by remember { mutableStateOf(setOf<String>()) }

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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(Unit) {
        delay(150)
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
                                onStartEdit(singleSelectedMessage)
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
                        title = itemName,
                        subtitle = activeStatus,
                        image = painterResource(R.drawable.ic_user_profile),
                        onBackClick = onBackClick,
                        buttonStyle = ButtonBackground.OPAQUE,
                        backIcon = TopIcon.Predefined.BACK
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
                    onValueChange = onMessageChange,
                    onSendClick = onSendClick,
                    focusRequester = focusRequester,
                    isEditing = messageToEdit != null,
                    onCancelEdit = onCancelEdit
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
                val shouldAnimate = initialLoadDone && message.id !in knownMessageIds

                MessageBubble(
                    message = message,
                    isSentByMe = isSentByMe,
                    isSelected = message.id in selectedMessageIds,
                    shouldAnimate = shouldAnimate,
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
                onDeleteForMe(selectedMessageIds)
                selectedMessageIds = emptySet()
            },
            onDeleteForEveryone = {
                onDeleteForEveryone(selectedMessageIds)
                selectedMessageIds = emptySet()
            }
        )
    }

    if (showMessageInfoSheet && selectedMessages.size == 1) {
        MessageInfoBottomSheet(
            message = selectedMessages.first(),
            roomUsers = emptyList(),
            onDismiss = {
                showMessageInfoSheet = false
                selectedMessageIds = emptySet()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val sampleMessages = listOf(
        ChatMessage(
            id = "1",
            senderId = "merchant_1",
            text = "Hello! Thanks for reaching out. How can we help you today?",
            timestamp = System.currentTimeMillis() - 3600000,
            status = MessageStatus.SEEN
        ),
        ChatMessage(
            id = "2",
            senderId = "current_user",
            text = "Hi! Is the venue available for booking this Saturday?",
            timestamp = System.currentTimeMillis() - 1800000,
            status = MessageStatus.SEEN
        ),
        ChatMessage(
            id = "3",
            senderId = "merchant_1",
            text = "Yes, it is available from 2 PM to 10 PM.",
            timestamp = System.currentTimeMillis() - 900000,
            status = MessageStatus.DELIVERED
        ),
        ChatMessage(
            id = "4",
            senderId = "current_user",
            text = "Awesome, what are the catering options?",
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT
        )
    )

    JasnifyTheme {
        ChatContent(
            itemName = "Grand Orchid Ballroom",
            activeStatus = "Online",
            messages = sampleMessages,
            currentUserUid = "current_user",
            messageText = "",
            onMessageChange = {},
            onSendClick = {},
            onBackClick = {}
        )
    }
}
