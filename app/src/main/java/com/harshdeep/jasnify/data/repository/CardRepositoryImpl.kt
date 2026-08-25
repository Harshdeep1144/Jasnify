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

    override suspend fun saveGlobalCardTheme(theme: CardTheme) {
        firestore.collection("app_config")
            .document("card_screen")
            .update("cardBg", FieldValue.arrayUnion(theme))
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
        val listener = firestore.collection("jasnify_cards")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val cards = snapshot?.toObjects(CardData::class.java) ?: emptyList()
                trySend(cards)
            }
        awaitClose { listener.remove() }
    }

    override fun getGlobalCardThemes(): Flow<List<CardTheme>> = callbackFlow {
        val listener = firestore.collection("app_config")
            .document("card_screen")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val themes = snapshot?.toObject(com.harshdeep.jasnify.domain.model.CardScreenConfig::class.java)?.cardBg ?: emptyList()
                trySend(themes)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveJasnifyCard(data: CardData) {
        if (data.id.isEmpty()) return
        firestore.collection("jasnify_cards").document(data.id).set(data).await()
    }

    override suspend fun saveJasnifyCards(cards: List<CardData>) {
        if (cards.isEmpty()) return
        
        // 1. Fetch existing global themes for uniqueness check
        val configDoc = firestore.collection("app_config")
            .document("card_screen")
            .get()
            .await()
            
        val existingThemes = configDoc.toObject(com.harshdeep.jasnify.domain.model.CardScreenConfig::class.java)?.cardBg ?: emptyList()
        val existingUrls = existingThemes.mapNotNull { it.url }.toSet()

        val batch = firestore.batch()
        val newThemesToUpload = mutableListOf<CardTheme>()

        cards.forEach { card ->
            if (card.id.isNotEmpty()) {
                val docRef = firestore.collection("jasnify_cards").document(card.id)
                batch.set(docRef, card)
                
                // 2. Check for unique backgroundUrl to add to global themes
                val url = card.backgroundUrl
                if (!url.isNullOrEmpty() && !existingUrls.contains(url)) {
                    val isAlreadyPending = newThemesToUpload.any { it.url == url }
                    if (!isAlreadyPending) {
                        newThemesToUpload.add(
                            CardTheme(
                                name = card.bgName.ifBlank { "New Theme" },
                                url = url,
                                isDefault = false
                            )
                        )
                    }
                }
            }
        }
        
        // 3. Update global themes if new ones found
        if (newThemesToUpload.isNotEmpty()) {
            firestore.collection("app_config")
                .document("card_screen")
                .update("cardBg", FieldValue.arrayUnion(*newThemesToUpload.toTypedArray()))
                .await()
        }

        batch.commit().await()
    }

    override suspend fun incrementCardShare(cardId: String, isJasnifyCard: Boolean) {
        if (isJasnifyCard) {
            firestore.collection("jasnify_cards")
                .document(cardId)
                .update("sharesCount", FieldValue.increment(1))
                .await()
        }
    }

    override suspend fun updateCardLikes(cardId: String, isJasnifyCard: Boolean, newLikesCount: Int) {
        if (isJasnifyCard) {
            firestore.collection("jasnify_cards")
                .document(cardId)
                .update("likesCount", newLikesCount)
                .await()
        }
    }

    override fun checkIsCardsAdmin(uid: String): Flow<Boolean> = callbackFlow {
        if (uid.isEmpty()) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("admins").document("card_admins")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(false)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val docUid = snapshot.getString("uid")
                    val isActive = snapshot.getBoolean("isActive") ?: false
                    trySend(isActive && docUid == uid)
                } else {
                    trySend(false)
                }
            }
        awaitClose { listener.remove() }
    }
}