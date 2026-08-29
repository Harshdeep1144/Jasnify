package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface RoomChatRepository {
    fun getRoomMessages(eventId: String, roomType: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(eventId: String, roomType: String, message: ChatMessage)
    suspend fun markMessagesAsSeen(eventId: String, roomType: String, messageIds: List<String>)
}
