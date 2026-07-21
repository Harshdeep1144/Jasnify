package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.SavedVenueDao
import com.harshdeep.jasnify.data.local.SavedVenueEntity
import com.harshdeep.jasnify.domain.model.SavedVenue
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.VenueReview
import com.harshdeep.jasnify.domain.repository.VenueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VenueRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val savedVenueDao: SavedVenueDao
) : VenueRepository {

    private val externalScope = CoroutineScope(Dispatchers.IO)

    // --- Catalog Methods ---

    override fun getAllVenues(): Flow<List<Venue>> = callbackFlow {
        val subscription = firestore.collection("venues")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val venues = snapshot?.documents?.mapNotNull { it.toObject(Venue::class.java) } ?: emptyList()
                trySend(venues)
            }
        awaitClose { subscription.remove() }
    }

    override fun getVenueById(venueId: String): Flow<Venue?> = callbackFlow {
        val subscription = firestore.collection("venues").document(venueId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Venue::class.java))
            }
        awaitClose { subscription.remove() }
    }

    override fun getVenueReviews(venueId: String): Flow<List<VenueReview>> = callbackFlow {
        val subscription = firestore.collection("venues").document(venueId)
            .collection("reviews")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents?.mapNotNull { it.toObject(VenueReview::class.java) } ?: emptyList()
                trySend(reviews)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun seedMockVenues(venues: List<Venue>) {
        venues.forEach { venue ->
            try {
                // 1. Upload Venue Doc (strip reviews from the main doc as per schema)
                val venueToUpload = venue.copy(
                    reviewsData = venue.reviewsData?.copy(reviews = emptyList())
                )
                firestore.collection("venues").document(venue.id).set(venueToUpload).await()

                // 2. Upload Reviews to Sub-collection
                venue.reviewsData?.reviews?.forEach { review ->
                    val reviewId = review.id.ifBlank { java.util.UUID.randomUUID().toString() }
                    val reviewToUpload = review.copy(id = reviewId)
                    firestore.collection("venues").document(venue.id)
                        .collection("reviews").document(reviewId)
                        .set(reviewToUpload)
                        .await()
                }
            } catch (e: Exception) {
                android.util.Log.e("VenueRepo", "Error seeding venue ${venue.name}: ${e.message}")
            }
        }
    }

    // --- Saved Venues Methods ---

    override fun getSavedVenues(eventId: String): Flow<List<SavedVenue>> {
        if (eventId.isNotBlank()) {
            fetchSavedVenuesFromFirestore(eventId)
        }
        return savedVenueDao.getSavedVenues(eventId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun fetchSavedVenuesFromFirestore(eventId: String) {
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Venue")
            .collection("saved_venues")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                
                snapshot.documents.forEach { doc ->
                    val venue = doc.toObject(SavedVenue::class.java)
                    if (venue != null) {
                        externalScope.launch {
                            savedVenueDao.insertSavedVenue(venue.toEntity(isSynced = true))
                        }
                    }
                }
            }
    }

    override suspend fun saveVenue(savedVenue: SavedVenue, syncToCloud: Boolean) {
        // 1. Update Room Instantly
        savedVenueDao.insertSavedVenue(savedVenue.toEntity(isSynced = syncToCloud))

        // 2. Trigger Background Sync to Firestore ONLY if NOT viewer
        if (syncToCloud) {
            externalScope.launch {
                try {
                    // Ensure parent room doc exists
                    firestore.collection("events").document(savedVenue.eventId)
                        .collection("rooms").document("Venue")
                        .set(mapOf("updatedAt" to System.currentTimeMillis()), com.google.firebase.firestore.SetOptions.merge())
                        .await()

                    firestore.collection("events")
                        .document(savedVenue.eventId)
                        .collection("rooms")
                        .document("Venue")
                        .collection("saved_venues")
                        .document(savedVenue.venueName)
                        .set(savedVenue)
                        .await()
                } catch (e: Exception) {
                    android.util.Log.e("VenueRepo", "Error syncing saved venue: ${e.message}")
                }
            }
        }
    }

    override suspend fun removeSavedVenue(eventId: String, venueName: String, syncToCloud: Boolean) {
        // 1. Update Room Instantly
        savedVenueDao.deleteSavedVenue(venueName, eventId)

        // 2. Trigger Background Sync ONLY if NOT viewer
        if (syncToCloud) {
            externalScope.launch {
                try {
                    firestore.collection("events")
                        .document(eventId)
                        .collection("rooms")
                        .document("Venue")
                        .collection("saved_venues")
                        .document(venueName)
                        .delete()
                        .await()
                } catch (e: Exception) {
                    android.util.Log.e("VenueRepo", "Error removing saved venue from cloud: ${e.message}")
                }
            }
        }
    }

    private fun SavedVenueEntity.toDomain(): SavedVenue {
        return SavedVenue(venueId, venueName, eventId, destination, timestamp)
    }

    private fun SavedVenue.toEntity(isSynced: Boolean): SavedVenueEntity {
        return SavedVenueEntity(venueName, venueId, eventId, destination, timestamp, isSynced)
    }
}
