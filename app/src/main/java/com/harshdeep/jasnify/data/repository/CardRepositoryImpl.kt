package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.repository.CardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CardRepository {

    override fun getCardData(eventId: String): Flow<CardData?> = callbackFlow {
        if (eventId.isEmpty()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val data = snapshot?.toObject(CardData::class.java)
                trySend(data)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun saveCardData(eventId: String, data: CardData) {
        if (eventId.isEmpty()) return
        
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .set(data)
            .await()
    }
}
