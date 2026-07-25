package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.harshdeep.jasnify.domain.model.ChatMessage
import com.harshdeep.jasnify.domain.model.Enquiry
import com.harshdeep.jasnify.domain.model.MessageStatus
import com.harshdeep.jasnify.domain.repository.EnquiryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EnquiryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EnquiryRepository {

    override suspend fun createEnquiry(enquiry: Enquiry) {
        try {
            firestore.collection("enquiries")
                .document(enquiry.id)
                .set(enquiry)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("EnquiryRepo", "Error creating enquiry: ${e.message}")
        }
    }

    override fun getEnquiriesForUser(userId: String): Flow<List<Enquiry>> = callbackFlow {
        // Log query details for debugging
        android.util.Log.d("EnquiryRepo", "Fetching enquiries for user: $userId")

        val query = firestore.collection("enquiries")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("EnquiryRepo", "Error fetching user enquiries: ${error.message}")
                // If it's an index error, this will log the URL
                close(error)
                return@addSnapshotListener
            }
            
            val enquiries = snapshot?.documents?.mapNotNull { doc ->
                try {
                    doc.toObject(Enquiry::class.java)
                } catch (e: Exception) {
                    android.util.Log.e("EnquiryRepo", "Error mapping doc ${doc.id}: ${e.message}")
                    null
                }
            } ?: emptyList()
            
            android.util.Log.d("EnquiryRepo", "Found ${enquiries.size} enquiries for user")
            trySend(enquiries)
        }
        awaitClose { subscription.remove() }
    }

    override fun getEnquiriesForMerchant(merchantId: String): Flow<List<Enquiry>> = callbackFlow {
        val subscription = firestore.collection("enquiries")
            .whereEqualTo("merchantId", merchantId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val enquiries = snapshot?.documents?.mapNotNull { it.toObject(Enquiry::class.java) } ?: emptyList()
                trySend(enquiries)
            }
        awaitClose { subscription.remove() }
    }

    override fun getEnquiryById(enquiryId: String): Flow<Enquiry?> = callbackFlow {
        val subscription = firestore.collection("enquiries")
            .document(enquiryId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Enquiry::class.java))
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getEnquiryOnce(enquiryId: String): Enquiry? {
        return try {
            firestore.collection("enquiries").document(enquiryId).get().await()
                .toObject(Enquiry::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun sendMessage(enquiryId: String, message: ChatMessage) {
        try {
            firestore.collection("enquiries").document(enquiryId)
                .update(
                    "messages", FieldValue.arrayUnion(message),
                    "lastMessage", message.text,
                    "timestamp", message.timestamp
                ).await()
        } catch (e: Exception) {
            android.util.Log.e("EnquiryRepo", "Error sending message: ${e.message}")
        }
    }

    override suspend fun updateMessageStatus(enquiryId: String, userId: String, status: MessageStatus) {
        try {
            val docRef = firestore.collection("enquiries").document(enquiryId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val enquiry = snapshot.toObject(Enquiry::class.java)
                if (enquiry != null) {
                    var modified = false
                    val updatedMessages = enquiry.messages.map { msg ->
                        if (msg.senderId != userId && msg.status.ordinal < status.ordinal) {
                            modified = true
                            msg.copy(status = status)
                        } else {
                            msg
                        }
                    }
                    if (modified) {
                        transaction.update(docRef, "messages", updatedMessages)
                    }
                }
                null
            }.await()
        } catch (e: Exception) {
            android.util.Log.e("EnquiryRepo", "Error updating message status: ${e.message}")
        }
    }

    override suspend fun markAsDelivered(enquiryId: String, userId: String) {
        updateMessageStatus(enquiryId, userId, MessageStatus.DELIVERED)
    }
}
