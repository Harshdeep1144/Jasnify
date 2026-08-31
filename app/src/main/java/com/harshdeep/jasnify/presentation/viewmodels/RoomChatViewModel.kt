package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.domain.repository.RoomChatRepository
import com.harshdeep.jasnify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RoomChatViewModel @Inject constructor(
    private val roomChatRepository: RoomChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    private var messageCollectionJob: Job? = null
    private var currentEventId: String = ""
    private var currentRoomType: String = ""

    fun loadMessages(eventId: String, roomType: String) {
        if (eventId.isBlank()) return
        currentEventId = eventId
        currentRoomType = roomType
        
        messageCollectionJob?.cancel()
        _messages.value = emptyList()

        messageCollectionJob = viewModelScope.launch {
            roomChatRepository.getRoomMessages(eventId, roomType).collectLatest { msgs ->
                // Filter out messages deleted for the current user
                val visibleMessages = msgs.filter { !it.deletedForUids.contains(currentUserUid) }
                _messages.value = visibleMessages
                
                // Mark incoming messages as delivered if not already
                val undeliveredIds = visibleMessages.filter { 
                    it.senderId != currentUserUid && !it.deliveredTo.contains(currentUserUid) 
                }.map { it.id }
                
                if (undeliveredIds.isNotEmpty()) {
                    roomChatRepository.markMessagesAsDelivered(eventId, roomType, undeliveredIds, currentUserUid)
                }
            }
        }
    }

    fun markRoomAsSeen() {
        if (currentEventId.isBlank() || currentRoomType.isBlank()) return
        
        viewModelScope.launch {
            val unseenIds = _messages.value.filter {
                it.senderId != currentUserUid && !it.seenBy.containsKey(currentUserUid)
            }.map { it.id }
            
            if (unseenIds.isNotEmpty()) {
                roomChatRepository.markMessagesAsSeen(currentEventId, currentRoomType, unseenIds, currentUserUid)
            }
        }
    }

    fun sendMessage(eventId: String, roomType: String, text: String) {
        if (text.isBlank() || eventId.isBlank()) return
        
        viewModelScope.launch {
            val message = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = text,
                senderId = currentUserUid,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENT
            )
            roomChatRepository.sendMessage(eventId, roomType, message)
            userRepository.updateLastActive(currentUserUid, false)
        }
    }

    fun editMessage(messageId: String, newText: String) {
        if (currentEventId.isBlank() || currentRoomType.isBlank()) return
        val msg = _messages.value.find { it.id == messageId }
        val editWindowMs = 15 * 60 * 1000L // 15 minutes
        if (msg == null || (System.currentTimeMillis() - msg.timestamp) <= editWindowMs) {
            viewModelScope.launch {
                roomChatRepository.editMessage(currentEventId, currentRoomType, messageId, newText)
            }
        }
    }

    fun deleteMessageForMe(messageId: String) {
        if (currentEventId.isBlank() || currentRoomType.isBlank()) return
        viewModelScope.launch {
            roomChatRepository.deleteMessageForMe(currentEventId, currentRoomType, messageId, currentUserUid)
        }
    }

    fun deleteMessageForEveryone(messageId: String) {
        if (currentEventId.isBlank() || currentRoomType.isBlank()) return
        viewModelScope.launch {
            roomChatRepository.deleteMessageForEveryone(currentEventId, currentRoomType, messageId)
        }
    }
}
