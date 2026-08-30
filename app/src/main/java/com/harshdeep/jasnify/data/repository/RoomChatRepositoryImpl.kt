package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FieldValue
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

    private fun getRoomDocId(roomType: String): String {
        return when (roomType.lowercase()) {
            "budget" -> "Budget"
            "catering" -> "Catering"
            "checklist" -> "Checklist"
            "vendors" -> "Vendors"
            "venue" -> "Venue"
            "guest" -> "Guest"
            "cards" -> "Cards"
            "moments" -> "Moments"
            else -> roomType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

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
        val roomDocId = getRoomDocId(roomType)
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
        val roomDocId = getRoomDocId(roomType)
        try {
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomDocId)
                .collection(collectionName)
                .document(message.id)
                .set(message)
                .await()
            android.util.Log.d("RoomChatRepo", "Successfully sent message to $roomDocId")
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error sending message to $roomDocId: ${e.message}")
        }
    }

    override suspend fun markMessagesAsDelivered(eventId: String, roomType: String, messageIds: List<String>, userId: String) {
        if (messageIds.isEmpty()) return
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = getRoomDocId(roomType)
        val roomRef = firestore.collection("events").document(eventId)
            .collection("rooms").document(roomDocId)
            .collection(collectionName)

        try {
            val batch = firestore.batch()
            messageIds.forEach { id ->
                batch.update(roomRef.document(id), "deliveredTo", FieldValue.arrayUnion(userId))
            }
            batch.commit().await()
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error marking messages as delivered: ${e.message}")
        }
    }

    override suspend fun markMessagesAsSeen(eventId: String, roomType: String, messageIds: List<String>, userId: String) {
        if (messageIds.isEmpty()) return
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = getRoomDocId(roomType)
        val roomRef = firestore.collection("events").document(eventId)
            .collection("rooms").document(roomDocId)
            .collection(collectionName)
            
        try {
            val batch = firestore.batch()
            val timestamp = System.currentTimeMillis()
            messageIds.forEach { id ->
                // Update seenBy map with user's UID and current timestamp
                batch.update(roomRef.document(id), "seenBy.$userId", timestamp)
            }
            batch.commit().await()
            android.util.Log.d("RoomChatRepo", "Marked ${messageIds.size} messages as seen in $roomDocId")
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error marking messages as seen: ${e.message}")
        }
    }

    override suspend fun editMessage(eventId: String, roomType: String, messageId: String, newText: String) {
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = getRoomDocId(roomType)
        try {
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomDocId)
                .collection(collectionName)
                .document(messageId)
                .update(
                    "text", newText,
                    "isEdited", true
                ).await()
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error editing message: ${e.message}")
        }
    }

    override suspend fun deleteMessageForMe(eventId: String, roomType: String, messageId: String, userId: String) {
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = getRoomDocId(roomType)
        try {
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomDocId)
                .collection(collectionName)
                .document(messageId)
                .update("deletedForUids", FieldValue.arrayUnion(userId))
                .await()
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error deleting message for me: ${e.message}")
        }
    }

    override suspend fun deleteMessageForEveryone(eventId: String, roomType: String, messageId: String) {
        val collectionName = getChatCollectionName(roomType)
        val roomDocId = getRoomDocId(roomType)
        try {
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomDocId)
                .collection(collectionName)
                .document(messageId)
                .update(
                    "deletedForEveryone", true,
                    "text", "This message was deleted"
                ).await()
        } catch (e: Exception) {
            android.util.Log.e("RoomChatRepo", "Error deleting message for everyone: ${e.message}")
        }
    }
}
