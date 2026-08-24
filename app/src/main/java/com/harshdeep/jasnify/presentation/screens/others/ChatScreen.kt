package com.harshdeep.jasnify.presentation.screens.others

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.harshdeep.jasnify.presentation.utils.TimeUtils
import com.harshdeep.jasnify.presentation.viewmodels.EnquiryViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VendorViewModel
import com.harshdeep.jasnify.presentation.viewmodels.VenueViewModel
import com.harshdeep.jasnify.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import sv.lib.squircleshape.SquircleShape

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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
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
                messageText = ""
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
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(150) // Small delay to allow enter transition/layout to complete smoothly
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
                    title = itemName,
                    subtitle = activeStatus,
                    image = painterResource(R.drawable.ic_user_profile),
                    onBackClick = onBackClick,
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
                    onValueChange = onMessageChange,
                    onSendClick = onSendClick,
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
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier
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
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Input Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }

            // Send Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (value.isNotBlank()) SurfacePrimary else SurfacePrimary.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .clickable(
                        enabled = value.isNotBlank(),
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSendClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = "Send",
                    tint = if (value.isNotBlank()) ContentPrimary else ContentSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
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