package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.repository.GuestRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GuestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : GuestRepository {

    override fun getGuests(eventId: String): Flow<List<Guest>> = callbackFlow {
        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document("Guest")
            .collection("guest_members")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val guests = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Guest::class.java)?.copy(id = doc.id)
                    }
                    trySend(guests)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addGuest(eventId: String, guest: Guest) {
        firestore.collection("events").document(eventId)
            .collection("rooms").document("Guest")
            .collection("guest_members").document(guest.id)
            .set(guest).await()
    }

    override suspend fun updateGuest(eventId: String, guest: Guest) {
        firestore.collection("events").document(eventId)
            .collection("rooms").document("Guest")
            .collection("guest_members").document(guest.id)
            .set(guest).await()
    }

    override suspend fun deleteGuest(eventId: String, guestId: String) {
        firestore.collection("events").document(eventId)
            .collection("rooms").document("Guest")
            .collection("guest_members").document(guestId)
            .delete().await()
    }

    override suspend fun deleteMultipleGuests(eventId: String, guestIds: List<String>) {
        val batch = firestore.batch()
        guestIds.forEach { id ->
            val ref = firestore.collection("events").document(eventId)
                .collection("rooms").document("Guest")
                .collection("guest_members").document(id)
            batch.delete(ref)
        }
        batch.commit().await()
    }
}
