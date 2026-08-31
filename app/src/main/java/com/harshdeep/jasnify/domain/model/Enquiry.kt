package com.harshdeep.jasnify.domain.model

import com.google.firebase.firestore.PropertyName
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
    val merchantProfileUrl: String? = null,
    val merchantPhoneNumber: String? = null,
    val itemType: String = "Venue", // "Venue" or "Vendor"
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
    val status: MessageStatus = MessageStatus.SENT,
    @get:PropertyName("isEdited") @set:PropertyName("isEdited") var isEdited: Boolean = false,
    val deletedForEveryone: Boolean = false,
    val deletedForUids: List<String> = emptyList(),
    val deliveredTo: List<String> = emptyList(), // UIDs who have received it
    val seenBy: Map<String, Long> = emptyMap() // UID to seen timestamp
)
