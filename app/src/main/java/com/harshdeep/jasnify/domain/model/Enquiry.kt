package com.harshdeep.jasnify.domain.model

import java.util.UUID

enum class MessageStatus {
    SENT, DELIVERED, SEEN
}

data class Enquiry(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val userName: String = "",
    val userProfileUrl: String? = null,
    val merchantId: String = "",
    val venueId: String = "",
    val venueName: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val messages: List<ChatMessage> = emptyList()
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)
