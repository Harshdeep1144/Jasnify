package com.harshdeep.jasnify.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.repository.MomentsRepository
import com.harshdeep.jasnify.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MomentsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinaryManager: CloudinaryManager,
    private val userRepository: UserRepository
) : MomentsRepository {

    override fun getFolders(eventId: String): Flow<List<MomentFolder>> = callbackFlow {
        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("folders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("MomentsRepo", "Error fetching folders: ${error.message}")
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val folders = it.toObjects(MomentFolder::class.java)
                    trySend(folders)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getMoments(eventId: String, folderId: String): Flow<List<Moment>> = callbackFlow {
        val query = if (folderId.isEmpty() || folderId == "all_moments_id") {
            firestore.collection("events").document(eventId)
                .collection("rooms").document("moments")
                .collection("all_moments")
        } else {
            firestore.collection("events").document(eventId)
                .collection("rooms").document("moments")
                .collection("folders").document(folderId)
                .collection("moments")
        }

        val subscription = query.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("MomentsRepo", "Error fetching moments: ${error.message}")
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val moments = it.toObjects(Moment::class.java)
                    trySend(moments)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getAllMoments(eventId: String): Flow<List<Moment>> = getMoments(eventId, "")

    override suspend fun createFolder(eventId: String, name: String): String {
        // Ensure the owner has access to this room (for older events)
        ensureOwnerAccess(eventId)

        val docRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("folders").document()
        
        val folder = MomentFolder(id = docRef.id, name = name, isNew = true)
        docRef.set(folder).await()
        return docRef.id
    }

    private suspend fun ensureOwnerAccess(eventId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val roomDoc = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments").get().await()
        
        if (!roomDoc.exists()) {
            firestore.collection("events").document(eventId)
                .collection("rooms").document("moments")
                .set(mapOf("updatedAt" to System.currentTimeMillis())).await()
        }

        // Check if owner is in the room users
        val membership = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("moments_room_users").document(currentUser.uid).get().await()
        
        if (!membership.exists()) {
            val eventDoc = firestore.collection("events").document(eventId).get().await()
            if (eventDoc.getString("ownerId") == currentUser.uid) {
                userRepository.grantDirectRoomAccess(eventId, "moments", currentUser.email ?: "", currentUser.uid, UserRole.OWNER)
            }
        }
    }

    override suspend fun uploadMoment(eventId: String, folderId: String, uri: Uri, isVideo: Boolean) {
        // 1. Get folder info for Cloudinary path
        val actualFolderId = if (folderId == "all_moments_id") {
            // Find "All Moments" folder or create one if uploading to "All"
            val allMomentsSnapshot = firestore.collection("events").document(eventId)
                .collection("rooms").document("moments")
                .collection("folders").whereEqualTo("name", "All Moments").limit(1).get().await()
            
            if (allMomentsSnapshot.isEmpty) createFolder(eventId, "All Moments") else allMomentsSnapshot.documents.first().id
        } else folderId

        val folderDoc = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("folders").document(actualFolderId).get().await()
        
        val folderName = folderDoc.getString("name") ?: "All Moments"

        // 2. Upload to Cloudinary
        val url = cloudinaryManager.uploadMoment(uri, eventId, folderName, isVideo)

        // 3. Save to Firestore
        val momentId = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("all_moments").document().id

        val moment = Moment(
            id = momentId,
            imageUrl = url,
            timestamp = System.currentTimeMillis(),
            isVideo = isVideo,
            folderId = actualFolderId
        )

        val batch = firestore.batch()
        
        // Save to folder specific collection
        val folderMomentRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("folders").document(actualFolderId)
            .collection("moments").document(momentId)
        batch.set(folderMomentRef, moment)

        // Save to all moments collection for easy access
        val allMomentRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("all_moments").document(momentId)
        batch.set(allMomentRef, moment)
        
        // Update folder cover and count
        batch.update(folderDoc.reference, "coverImageUrl", url)
        batch.update(folderDoc.reference, "itemCount", com.google.firebase.firestore.FieldValue.increment(1))
        batch.update(folderDoc.reference, "isNew", false)

        batch.commit().await()
    }

    override suspend fun initializeRoom(eventId: String) {
        ensureOwnerAccess(eventId)
    }

    override suspend fun deleteMoment(eventId: String, folderId: String, momentId: String) {
        val momentDoc = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("all_moments").document(momentId).get().await()
        
        if (!momentDoc.exists()) return

        val moment = momentDoc.toObject(Moment::class.java) ?: return
        
        // 1. Delete from Cloudinary
        cloudinaryManager.deleteImageByUrl(moment.imageUrl)

        // 2. Delete from Firestore
        val batch = firestore.batch()
        
        // Remove from all_moments
        batch.delete(momentDoc.reference)
        
        // Remove from folder moments
        val folderMomentRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("folders").document(folderId)
            .collection("moments").document(momentId)
        batch.delete(folderMomentRef)

        // Update folder count
        val folderRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("moments")
            .collection("folders").document(folderId)
        batch.update(folderRef, "itemCount", com.google.firebase.firestore.FieldValue.increment(-1))

        batch.commit().await()
    }

    override suspend fun deleteFolder(eventId: String, folderId: String) {
        try {
            val folderRef = firestore.collection("events").document(eventId)
                .collection("rooms").document("moments")
                .collection("folders").document(folderId)
            
            val folderDoc = folderRef.get().await()
            if (!folderDoc.exists()) return
            
            val folderName = folderDoc.getString("name") ?: "Unknown"
            
            // 1. Get all moments in this folder
            val momentsRef = folderRef.collection("moments")
            val momentDocs = momentsRef.get().await()
            val moments = momentDocs.toObjects(Moment::class.java)
            
            // 2. Delete each asset from Cloudinary INDIVIDUALLY
            // This is required because the Admin API (bulk delete) is not supported in Android SDK
            moments.forEach { moment ->
                try {
                    cloudinaryManager.deleteImageByUrl(moment.imageUrl)
                } catch (e: Exception) {
                    android.util.Log.e("MomentsRepo", "Failed to delete Cloudinary asset: ${moment.imageUrl}")
                }
            }

            // 3. Delete Firestore documents in a batch for UI consistency
            val batch = firestore.batch()
            
            // Remove from global all_moments
            moments.forEach { moment ->
                val allMomentRef = firestore.collection("events").document(eventId)
                    .collection("rooms").document("moments")
                    .collection("all_moments").document(moment.id)
                batch.delete(allMomentRef)
                
                // Remove from folder sub-collection
                batch.delete(momentsRef.document(moment.id))
            }
            
            // Remove the folder document itself
            batch.delete(folderRef)
            
            batch.commit().await()
            
            android.util.Log.d("MomentsRepo", "Successfully deleted folder $folderName and all its ${moments.size} moments")
        } catch (e: Exception) {
            android.util.Log.e("MomentsRepo", "Error during folder deletion: ${e.message}", e)
            throw e
        }
    }
}
