package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.SavedVenueDao
import com.harshdeep.jasnify.data.local.SavedVenueEntity
import com.harshdeep.jasnify.domain.model.SavedVenue
import com.harshdeep.jasnify.domain.repository.VenueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VenueRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val savedVenueDao: SavedVenueDao
) : VenueRepository {

    private val externalScope = CoroutineScope(Dispatchers.IO)

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
