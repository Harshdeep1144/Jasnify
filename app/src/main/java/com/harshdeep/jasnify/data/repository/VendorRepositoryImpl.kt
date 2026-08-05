package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.SavedVendorDao
import com.harshdeep.jasnify.data.local.SavedVendorEntity
import com.harshdeep.jasnify.domain.model.SavedVendor
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.repository.VendorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VendorRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val savedVendorDao: SavedVendorDao
) : VendorRepository {

    private val externalScope = CoroutineScope(Dispatchers.IO)

    override fun getAllVendors(): Flow<List<Vendor>> = callbackFlow {
        val subscription = firestore.collection("vendors")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val vendors = snapshot?.documents?.mapNotNull { it.toObject(Vendor::class.java) } ?: emptyList()
                trySend(vendors)
            }
        awaitClose { subscription.remove() }
    }

    override fun getVendorsByCategory(category: String): Flow<List<Vendor>> = callbackFlow {
        val subscription = firestore.collection("vendors")
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val vendors = snapshot?.documents?.mapNotNull { it.toObject(Vendor::class.java) } ?: emptyList()
                trySend(vendors)
            }
        awaitClose { subscription.remove() }
    }

    override fun getVendorReviews(vendorId: String): Flow<List<com.harshdeep.jasnify.domain.model.VendorReview>> = callbackFlow {
        val subscription = firestore.collection("vendors").document(vendorId)
            .collection("reviews")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents?.mapNotNull { it.toObject(com.harshdeep.jasnify.domain.model.VendorReview::class.java) } ?: emptyList()
                trySend(reviews)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addVendorReview(vendorId: String, review: com.harshdeep.jasnify.domain.model.VendorReview) {
        try {
            // Use userId as the document ID to ensure one review per user
            val docId = review.userId.ifBlank { review.id.ifBlank { java.util.UUID.randomUUID().toString() } }
            val reviewToUpload = review.copy(id = docId)
            firestore.collection("vendors").document(vendorId)
                .collection("reviews").document(docId)
                .set(reviewToUpload)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("VendorRepo", "Error adding review: ${e.message}")
            throw e
        }
    }

    override suspend fun deleteVendorReview(vendorId: String, userId: String) {
        try {
            firestore.collection("vendors").document(vendorId)
                .collection("reviews").document(userId)
                .delete()
                .await()
        } catch (e: Exception) {
            android.util.Log.e("VendorRepo", "Error deleting review: ${e.message}")
            throw e
        }
    }

    override fun getSavedVendors(eventId: String): Flow<List<SavedVendor>> {
        // Fetch all categories for this event? Might be complex with separate collections.
        // For now, let's assume we fetch from local DB which is synced category by category.
        return savedVendorDao.getSavedVendors(eventId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSavedVendorsByCategory(eventId: String, category: String): Flow<List<SavedVendor>> {
        if (eventId.isNotBlank()) {
            fetchSavedVendorsFromFirestore(eventId, category)
        }
        return savedVendorDao.getSavedVendorsByCategory(eventId, category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun fetchSavedVendorsFromFirestore(eventId: String, category: String) {
        firestore.collection("events")
            .document(eventId)
            .collection("rooms")
            .document("Vendors")
            .collection("saved_vendors")
            .document(category)
            .collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                
                snapshot.documents.forEach { doc ->
                    val vendor = doc.toObject(SavedVendor::class.java)
                    if (vendor != null) {
                        externalScope.launch {
                            savedVendorDao.insertSavedVendor(vendor.toEntity(isSynced = true))
                        }
                    }
                }
            }
    }

    override suspend fun saveVendor(savedVendor: SavedVendor, syncToCloud: Boolean) {
        savedVendorDao.insertSavedVendor(savedVendor.toEntity(isSynced = syncToCloud))

        if (syncToCloud) {
            externalScope.launch {
                try {
                    // Ensure room doc exists
                    firestore.collection("events").document(savedVendor.eventId)
                        .collection("rooms").document("Vendors")
                        .set(mapOf("updatedAt" to System.currentTimeMillis()), com.google.firebase.firestore.SetOptions.merge())
                        .await()

                    firestore.collection("events")
                        .document(savedVendor.eventId)
                        .collection("rooms")
                        .document("Vendors")
                        .collection("saved_vendors")
                        .document(savedVendor.category)
                        .collection("items")
                        .document(savedVendor.vendorName)
                        .set(savedVendor)
                        .await()
                } catch (e: Exception) {
                    android.util.Log.e("VendorRepo", "Error syncing saved vendor: ${e.message}")
                }
            }
        }
    }

    override suspend fun removeSavedVendor(eventId: String, vendorName: String, category: String, syncToCloud: Boolean) {
        savedVendorDao.deleteSavedVendor(vendorName, eventId, category)

        if (syncToCloud) {
            externalScope.launch {
                try {
                    firestore.collection("events")
                        .document(eventId)
                        .collection("rooms")
                        .document("Vendors")
                        .collection("saved_vendors")
                        .document(category)
                        .collection("items")
                        .document(vendorName)
                        .delete()
                        .await()
                } catch (e: Exception) {
                    android.util.Log.e("VendorRepo", "Error removing saved vendor: ${e.message}")
                }
            }
        }
    }

    override suspend fun seedMockVendors(vendors: List<Vendor>) {
        vendors.forEach { vendor ->
            try {
                // 1. Upload Vendor Doc (strip reviews from the main doc as per schema)
                val vendorToUpload = vendor.copy(
                    reviewsData = vendor.reviewsData?.copy(reviews = emptyList())
                )
                firestore.collection("vendors").document(vendor.id).set(vendorToUpload).await()

                // 2. Upload Reviews to Sub-collection
                vendor.reviewsData?.reviews?.forEach { review ->
                    val reviewId = review.id.ifBlank { java.util.UUID.randomUUID().toString() }
                    val reviewToUpload = review.copy(id = reviewId)
                    firestore.collection("vendors").document(vendor.id)
                        .collection("reviews").document(reviewId)
                        .set(reviewToUpload)
                        .await()
                }
            } catch (e: Exception) {
                android.util.Log.e("VendorRepo", "Error seeding vendor ${vendor.name}: ${e.message}")
            }
        }
    }

    override suspend fun isCatalogEmpty(): Boolean {
        return try {
            val snapshot = firestore.collection("vendors").limit(1).get().await()
            snapshot.isEmpty
        } catch (e: Exception) {
            true
        }
    }

    private fun SavedVendorEntity.toDomain(): SavedVendor {
        return SavedVendor(vendorId, vendorName, eventId, category, destination, timestamp)
    }

    private fun SavedVendor.toEntity(isSynced: Boolean): SavedVendorEntity {
        return SavedVendorEntity(vendorName, vendorId, eventId, category, destination, timestamp, isSynced)
    }
}
