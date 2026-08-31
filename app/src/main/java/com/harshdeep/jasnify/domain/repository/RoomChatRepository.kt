package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface RoomChatRepository {
    fun getRoomMessages(eventId: String, roomType: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(eventId: String, roomType: String, message: ChatMessage)
    suspend fun markMessagesAsDelivered(eventId: String, roomType: String, messageIds: List<String>, userId: String)
    suspend fun markMessagesAsSeen(eventId: String, roomType: String, messageIds: List<String>, userId: String)
    suspend fun editMessage(eventId: String, roomType: String, messageId: String, newText: String)
    suspend fun deleteMessageForMe(eventId: String, roomType: String, messageId: String, userId: String)
    suspend fun deleteMessageForEveryone(eventId: String, roomType: String, messageId: String)
}
