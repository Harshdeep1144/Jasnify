package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.CardRoomData
import com.harshdeep.jasnify.domain.model.CardTheme
import com.harshdeep.jasnify.domain.repository.CardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CardRepository {

    override fun getMyCards(eventId: String): Flow<List<CardData>> = callbackFlow {
        if (eventId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .collection("my_cards")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val cards = snapshot?.toObjects(CardData::class.java) ?: emptyList()
                trySend(cards)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun saveMyCard(eventId: String, data: CardData) {
        if (eventId.isEmpty() || data.id.isEmpty()) return
        
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .collection("my_cards")
            .document(data.id)
            .set(data)
            .await()
    }

    override suspend fun deleteMyCard(eventId: String, cardId: String) {
        if (eventId.isEmpty() || cardId.isEmpty()) return
        
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .collection("my_cards")
            .document(cardId)
            .delete()
            .await()
    }

    override fun getLikedCards(eventId: String): Flow<List<CardData>> = callbackFlow {
        if (eventId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .collection("liked_cards")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val cards = snapshot?.toObjects(CardData::class.java) ?: emptyList()
                trySend(cards)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun toggleLikedCard(eventId: String, data: CardData) {
        if (eventId.isEmpty() || data.id.isEmpty()) return
        
        val docRef = firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .collection("liked_cards")
            .document(data.id)

        val doc = docRef.get().await()
        if (doc.exists()) {
            docRef.delete().await()
        } else {
            docRef.set(data).await()
        }
    }

    override fun getCardRoomData(eventId: String): Flow<CardRoomData?> = callbackFlow {
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
                val data = snapshot?.toObject(CardRoomData::class.java)
                trySend(data)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun initializeCardRoom(eventId: String, defaultThemes: List<CardTheme>) {
        if (eventId.isEmpty()) return
        
        val docRef = firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
        
        val doc = docRef.get().await()
        if (!doc.exists()) {
            docRef.set(CardRoomData(themes = defaultThemes)).await()
        }
    }

    override suspend fun saveCardTheme(eventId: String, theme: CardTheme) {
        if (eventId.isEmpty()) return
        
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")
            .update("themes", FieldValue.arrayUnion(theme))
            .await()
    }

    override suspend fun updateCardThemeName(eventId: String, themeId: String, newName: String) {
        if (eventId.isEmpty()) return
        
        val docRef = firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Cards")

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val roomData = snapshot.toObject(CardRoomData::class.java)
            if (roomData != null) {
                val updatedThemes = roomData.themes.map {
                    if (it.id == themeId) it.copy(name = newName) else it
                }
                transaction.update(docRef, "themes", updatedThemes)
            }
        }.await()
    }

    override fun getJasnifyCards(): Flow<List<CardData>> = callbackFlow {
        val listener = firestore.collection("jasnifyCards")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val cards = snapshot?.toObjects(CardData::class.java) ?: emptyList()
                trySend(cards)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveJasnifyCard(data: CardData) {
        if (data.id.isEmpty()) return
        firestore.collection("jasnifyCards").document(data.id).set(data).await()
    }

    override suspend fun incrementCardShare(cardId: String, isJasnifyCard: Boolean) {
        if (isJasnifyCard) {
            firestore.collection("jasnifyCards")
                .document(cardId)
                .update("sharesCount", FieldValue.increment(1))
                .await()
        }
    }

    override suspend fun updateCardLikes(cardId: String, isJasnifyCard: Boolean, newLikesCount: Int) {
        if (isJasnifyCard) {
            firestore.collection("jasnifyCards")
                .document(cardId)
                .update("likesCount", newLikesCount)
                .await()
        }
    }
}
