package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.domain.repository.RoomChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoomChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoomChatRepository {

    private fun getChatCollectionName(roomType: String): String {
        return when (roomType.lowercase()) {
            "budget" -> "budget_room_chats"
            "catering" -> "catering_room_chats"
            "checklist" -> "checklist_room_chats"
            "vendors" -> "vendors_room_chats"
            "venue" -> "venue_room_chats"
            "guest" -> "guest_room_chats"
            "cards" -> "card_room_chats"
            "moments" -> "moments_room_chats"
            else -> "${roomType.lowercase()}_room_chats"
        }
    }

    override fun getRoomMessages(eventId: String, roomType: String): Flow<List<ChatMessage>> = callbackFlow {
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = roomType.lowercase()
        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document(roomDocId)
            .collection(collectionName)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("RoomChatRepo", "Error listening to room messages for $roomDocId: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(eventId: String, roomType: String, message: ChatMessage) {
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = roomType.lowercase()
        try {
            // Start as SENT
            val docRef = firestore.collection("events").document(eventId)
                .collection("rooms").document(roomDocId)
                .collection(collectionName)
                .document(message.id)
            
            docRef.set(message).await()
            
            // Once written to server, update to DELIVERED (Double Tick)
            docRef.update("status", MessageStatus.DELIVERED.name).await()
            
            android.util.Log.d("RoomChatRepo", "Successfully sent and delivered message to $roomDocId")
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error sending message to $roomDocId: ${e.message}")
        }
    }

    override suspend fun markMessagesAsSeen(eventId: String, roomType: String, messageIds: List<String>) {
        if (messageIds.isEmpty()) return
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = roomType.lowercase()
        val roomRef = firestore.collection("events").document(eventId)
            .collection("rooms").document(roomDocId)
            .collection(collectionName)
            
        try {
            val batch = firestore.batch()
            messageIds.forEach { id ->
                batch.update(roomRef.document(id), "status", MessageStatus.SEEN.name)
            }
            batch.commit().await()
            android.util.Log.d("RoomChatRepo", "Marked ${messageIds.size} messages as seen in $roomDocId")
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error marking messages as seen: ${e.message}")
        }
    }
}
