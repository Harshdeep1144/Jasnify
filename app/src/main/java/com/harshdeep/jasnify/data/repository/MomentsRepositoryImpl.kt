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

    override fun getFolders(eventId: String, parentId: String): Flow<List<MomentFolder>> = callbackFlow {
        if (eventId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("folders")
            .whereEqualTo("parentId", parentId)
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
        if (eventId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val query = if (folderId.isEmpty() || folderId == "all_moments_id") {
            firestore.collection("events").document(eventId)
                .collection("rooms").document("Moments")
                .collection("all_moments")
        } else {
            firestore.collection("events").document(eventId)
                .collection("rooms").document("Moments")
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

    override suspend fun createFolder(eventId: String, name: String, parentId: String): String {
        if (eventId.isBlank() || name.isBlank()) return ""
        // Ensure the owner has access to this room (for older events)
        ensureOwnerAccess(eventId)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val foldersRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("folders")
        
        val docRef = foldersRef.document()
        
        var fullPath = name
        if (parentId.isNotEmpty()) {
            val parentDoc = foldersRef.document(parentId).get().await()
            val parentPath = parentDoc.getString("fullPath") ?: ""
            if (parentPath.isNotEmpty()) {
                fullPath = "$parentPath/$name"
            }
        }
        
        val folder = MomentFolder(
            id = docRef.id, 
            name = name, 
            isNew = true,
            uploaderId = currentUser?.uid ?: "",
            parentId = parentId,
            fullPath = fullPath
        )
        docRef.set(folder).await()
        return docRef.id
    }

    private suspend fun ensureOwnerAccess(eventId: String) {
        if (eventId.isBlank()) return
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val roomDoc = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments").get().await()
        
        if (!roomDoc.exists()) {
            firestore.collection("events").document(eventId)
                .collection("rooms").document("Moments")
                .set(mapOf("updatedAt" to System.currentTimeMillis())).await()
        }

        // Check if owner is in the room users
        val membership = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("moments_room_users").document(currentUser.uid).get().await()
        
        if (!membership.exists()) {
            val eventDoc = firestore.collection("events").document(eventId).get().await()
            if (eventDoc.getString("ownerId") == currentUser.uid) {
                userRepository.grantDirectRoomAccess(eventId, "Moments", currentUser.email ?: "", currentUser.uid, UserRole.OWNER)
            }
        }
    }

    override suspend fun uploadMoment(eventId: String, folderId: String, uri: Uri, isVideo: Boolean) {
        if (eventId.isBlank()) return
        // 1. Resolve folder info for path and metadata
        val actualFolderId = if (folderId.isEmpty() || folderId == "all_moments_id") {
            // Find/Create "All Moments" folder at root
            val allMomentsSnapshot = firestore.collection("events").document(eventId)
                .collection("rooms").document("Moments")
                .collection("folders")
                .whereEqualTo("name", "All Moments")
                .whereEqualTo("parentId", "")
                .limit(1).get().await()
            
            if (allMomentsSnapshot.isEmpty) {
                createFolder(eventId, "All Moments", "")
            } else {
                allMomentsSnapshot.documents.first().id
            }
        } else folderId

        val folderRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("folders").document(actualFolderId)
        
        val folderDoc = folderRef.get().await()
        if (!folderDoc.exists()) throw Exception("Target folder does not exist")

        val folderPath = folderDoc.getString("fullPath") ?: folderDoc.getString("name") ?: "Moments"

        // 2. Upload to Cloudinary using the full virtual path
        val url = cloudinaryManager.uploadMoment(uri, eventId, folderPath, isVideo)

        // 3. Prepare Moment Data
        val currentUser = FirebaseAuth.getInstance().currentUser
        val momentId = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("all_moments").document().id

        val moment = Moment(
            id = momentId,
            imageUrl = url,
            timestamp = System.currentTimeMillis(),
            isVideo = isVideo,
            folderId = actualFolderId,
            uploaderId = currentUser?.uid ?: ""
        )

        // 4. Atomic Write (Moment in Folder + Moment in All + Folder Metadata)
        val batch = firestore.batch()
        
        // Save to specific folder sub-collection
        val folderMomentRef = folderRef.collection("moments").document(momentId)
        batch.set(folderMomentRef, moment)

        // Save to global room collection
        val allMomentRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("all_moments").document(momentId)
        batch.set(allMomentRef, moment)
        
        // Update folder metadata (Newest item becomes cover)
        batch.update(folderRef, "coverImageUrl", url)
        batch.update(folderRef, "itemCount", com.google.firebase.firestore.FieldValue.increment(1))
        batch.update(folderRef, "isNew", false)

        batch.commit().await()
        android.util.Log.d("MomentsRepo", "Successfully uploaded moment to $folderPath")
    }

    override suspend fun initializeRoom(eventId: String) {
        ensureOwnerAccess(eventId)
    }

    override fun getUserRole(eventId: String): Flow<UserRole> = callbackFlow {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            trySend(UserRole.VIEWER)
            close()
            return@callbackFlow
        }

        var isOwner = false
        try {
            val eventDoc = firestore.collection("events").document(eventId).get().await()
            if (eventDoc.getString("ownerId") == currentUser.uid) {
                isOwner = true
                trySend(UserRole.OWNER)
            }
        } catch (e: Exception) {
            // Ignore
        }

        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("moments_room_users").document(currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(if (isOwner) UserRole.OWNER else UserRole.VIEWER)
                    return@addSnapshotListener
                }
                val roleStr = snapshot?.getString("role")
                val role = when {
                    roleStr != null -> {
                        try {
                            UserRole.valueOf(roleStr)
                        } catch (e: Exception) {
                            if (isOwner) UserRole.OWNER else UserRole.VIEWER
                        }
                    }
                    isOwner -> UserRole.OWNER
                    else -> UserRole.VIEWER
                }
                trySend(role)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun deleteMoment(eventId: String, folderId: String, momentId: String) {
        if (eventId.isBlank() || momentId.isBlank()) return
        val momentDoc = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("all_moments").document(momentId).get().await()
        
        if (!momentDoc.exists()) return

        val moment = momentDoc.toObject(Moment::class.java) ?: return
        
        // 1. Delete from Cloudinary
        cloudinaryManager.deleteImageByUrl(moment.imageUrl)

        // 2. Delete from Firestore
        val batch = firestore.batch()
        
        // Remove from all_moments
        batch.delete(momentDoc.reference)
        
        // Remove from folder moments (Use folderId from moment if passed one is empty)
        val targetFolderId = if (folderId.isNotEmpty() && folderId != "all_moments_id") folderId else moment.folderId
        
        if (targetFolderId.isNotEmpty()) {
            val folderMomentRef = firestore.collection("events").document(eventId)
                .collection("rooms").document("Moments")
                .collection("folders").document(targetFolderId)
                .collection("moments").document(momentId)
            batch.delete(folderMomentRef)

            // Update folder count
            val folderRef = firestore.collection("events").document(eventId)
                .collection("rooms").document("Moments")
                .collection("folders").document(targetFolderId)
            batch.update(folderRef, "itemCount", com.google.firebase.firestore.FieldValue.increment(-1))
        }

        batch.commit().await()
    }

    override suspend fun deleteFolder(eventId: String, folderId: String) {
        if (eventId.isBlank() || folderId.isEmpty() || folderId == "all_moments_id") {
            android.util.Log.e("MomentsRepo", "Attempted to delete protected or root folder: $folderId")
            return
        }
        try {
            val rootFolderRef = firestore.collection("events").document(eventId)
                .collection("rooms").document("Moments")
                .collection("folders")
            
            // Collect all folders to delete (recursively)
            val foldersToDelete = mutableListOf<String>()
            val stack = mutableListOf(folderId)
            
            while (stack.isNotEmpty()) {
                val currentId = stack.removeAt(stack.size - 1)
                foldersToDelete.add(currentId)
                
                // Find subfolders
                val subfolders = rootFolderRef.whereEqualTo("parentId", currentId).get().await()
                subfolders.documents.forEach { doc ->
                    stack.add(doc.id)
                }
            }

            // Delete moments from all collected folders
            foldersToDelete.forEach { fid ->
                val momentsRef = rootFolderRef.document(fid).collection("moments")
                val momentDocs = momentsRef.get().await()
                val moments = momentDocs.toObjects(Moment::class.java)
                
                // Delete assets from Cloudinary
                moments.forEach { moment ->
                    try {
                        cloudinaryManager.deleteImageByUrl(moment.imageUrl)
                    } catch (_: Exception) {}
                }

                // Delete moments from Firestore
                val batch = firestore.batch()
                moments.forEach { moment ->
                    val allMomentRef = firestore.collection("events").document(eventId)
                        .collection("rooms").document("Moments")
                        .collection("all_moments").document(moment.id)
                    batch.delete(allMomentRef)
                    batch.delete(momentsRef.document(moment.id))
                }
                batch.commit().await()
            }
            
            // Delete the folders themselves
            val folderBatch = firestore.batch()
            foldersToDelete.forEach { fid ->
                folderBatch.delete(rootFolderRef.document(fid))
            }
            folderBatch.commit().await()
            
            android.util.Log.d("MomentsRepo", "Successfully deleted folder hierarchy for: $folderId")
        } catch (e: Exception) {
            android.util.Log.e("MomentsRepo", "Error during recursive folder deletion: ${e.message}", e)
            throw e
        }
    }

    override fun getSavedMoments(eventId: String): Flow<List<Moment>> = callbackFlow {
        if (eventId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("user_saved").document(currentUser.uid)
            .collection("moments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("MomentsRepo", "Error fetching saved moments: ${error.message}")
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val moments = it.toObjects(Moment::class.java)
                    trySend(moments)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun toggleSaveMoment(eventId: String, moment: Moment) {
        if (eventId.isBlank() || moment.id.isBlank()) return
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val docRef = firestore.collection("events").document(eventId)
            .collection("rooms").document("Moments")
            .collection("user_saved").document(currentUser.uid)
            .collection("moments").document(moment.id)

        val doc = docRef.get().await()
        if (doc.exists()) {
            docRef.delete().await()
        } else {
            docRef.set(moment).await()
        }
    }
}
