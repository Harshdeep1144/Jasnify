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

    fun loadMessages(eventId: String, roomType: String) {
        if (eventId.isBlank()) return
        
        // Cancel previous collection to prevent data leakage across rooms
        messageCollectionJob?.cancel()

        // Reset messages immediately to prevent state leakage from previous room
        _messages.value = emptyList()

        messageCollectionJob = viewModelScope.launch {
            roomChatRepository.getRoomMessages(eventId, roomType).collectLatest { msgs ->
                _messages.value = msgs

                // Mark received messages from others as SEEN
                val unseenIds = msgs.filter {
                    it.senderId != currentUserUid && it.status != MessageStatus.SEEN
                }.map { it.id }

                if (unseenIds.isNotEmpty()) {
                    roomChatRepository.markMessagesAsSeen(eventId, roomType, unseenIds)
                }
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
            
            // Update last active
            userRepository.updateLastActive(currentUserUid, false)
        }
    }
}
