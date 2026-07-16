package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.repository.PendingAccess
import com.harshdeep.jasnify.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun createUserProfile(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }

    override suspend fun getUserProfile(uid: String): User? {
        return try {
            firestore.collection("users").document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchUsers(query: String): List<User> {
        if (query.isBlank()) return emptyList()
        return try {
            // Simple search by username or email
            val usernameQuery = firestore.collection("users")
                .whereGreaterThanOrEqualTo("username", query.lowercase())
                .whereLessThanOrEqualTo("username", query.lowercase() + "\uf8ff")
                .get().await()
            
            val emailQuery = firestore.collection("users")
                .whereGreaterThanOrEqualTo("email", query.lowercase())
                .whereLessThanOrEqualTo("email", query.lowercase() + "\uf8ff")
                .get().await()

            (usernameQuery.toObjects(User::class.java) + emailQuery.toObjects(User::class.java)).distinctBy { it.uid }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        val cleanEmail = email.lowercase().trim()
        return try {
            firestore.collection("users")
                .whereEqualTo("email", cleanEmail)
                .get().await()
                .toObjects(User::class.java).firstOrNull()
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error getting user by email: $cleanEmail", e)
            null
        }
    }

    private fun getUserCollectionName(roomType: String): String {
        val name = when (roomType.lowercase()) {
            "budget" -> "budget_room_users"
            "catering" -> "catering_room_users"
            "checklist" -> "checklist_room_users"
            "vendors" -> "vendors_room_users"
            "venue" -> "venue_room_users"
            else -> "room_users"
        }
        return name
    }

    override suspend fun grantRoomAccess(eventId: String, roomType: String, email: String, role: UserRole) {
        if (eventId.isBlank()) {
            android.util.Log.e("UserRepository", "grantRoomAccess: eventId is blank!")
            return
        }
        val cleanEmail = email.lowercase().trim()
        val user = getUserByEmail(cleanEmail)
        val collectionName = getUserCollectionName(roomType)
        
        try {
            // CRITICAL: Explicitly create/update the room document so it's a "real" parent.
            // This prevents console display bugs where subcollections "hide" each other.
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .set(mapOf(
                    "id" to roomType,
                    "updatedAt" to System.currentTimeMillis()
                ), com.google.firebase.firestore.SetOptions.merge())
                .await()

            if (user != null) {
                val accessData = mapOf(
                    "uid" to user.uid,
                    "email" to cleanEmail,
                    "role" to role.name,
                    "name" to user.name,
                    "username" to user.username,
                    "profilePictureUrl" to user.profilePictureUrl
                )
                android.util.Log.d("UserRepository", "Writing to $collectionName for ${user.uid}")
                firestore.collection("events").document(eventId)
                    .collection("rooms").document(roomType)
                    .collection(collectionName).document(user.uid)
                    .set(accessData).await()
            } else {
                val pendingData = mapOf(
                    "eventId" to eventId,
                    "roomType" to roomType,
                    "role" to role.name,
                    "email" to cleanEmail
                )
                android.util.Log.d("UserRepository", "Writing to pending_access for $cleanEmail")
                firestore.collection("events").document(eventId)
                    .collection("rooms").document(roomType)
                    .collection("pending_access").document(cleanEmail)
                    .set(pendingData).await()
            }
            android.util.Log.d("UserRepository", "Successfully processed access for $cleanEmail in $roomType")
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "CRITICAL: Failed write for $cleanEmail in $roomType", e)
            throw e
        }
    }

    override suspend fun removeRoomAccess(eventId: String, roomType: String, uid: String) {
        val collectionName = getUserCollectionName(roomType)
        firestore.collection("events").document(eventId)
            .collection("rooms").document(roomType)
            .collection(collectionName).document(uid)
            .delete().await()
    }

    override fun getRoomUsers(eventId: String, roomType: String): Flow<List<User>> = callbackFlow {
        val collectionName = getUserCollectionName(roomType)
        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document(roomType)
            .collection(collectionName)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val users = snapshot.documents.mapNotNull { doc ->
                        try {
                            val uid = doc.getString("uid") ?: ""
                            val name = doc.getString("name") ?: ""
                            val email = doc.getString("email") ?: ""
                            val username = doc.getString("username") ?: ""
                            val roleStr = doc.getString("role") ?: UserRole.VIEWER.name
                            val role = try {
                                UserRole.valueOf(roleStr)
                            } catch (e: IllegalArgumentException) {
                                UserRole.VIEWER
                            }
                            val profilePictureUrl = doc.getString("profilePictureUrl")
                            User(uid, name, email, username, role, profilePictureUrl)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(users)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun checkPendingAccess(email: String): List<PendingAccess> {
        return try {
            // Use collectionGroup for nested pending_access lookups across all events/rooms
            android.util.Log.d("UserRepository", "Checking global pending access for: $email")
            val snapshot = firestore.collectionGroup("pending_access")
                .whereEqualTo("email", email)
                .get().await()
            
            android.util.Log.d("UserRepository", "Found ${snapshot.size()} pending invitations")
            snapshot.documents.map { doc ->
                PendingAccess(
                    eventId = doc.getString("eventId") ?: "",
                    roomType = doc.getString("roomType") ?: "",
                    role = UserRole.valueOf(doc.getString("role") ?: UserRole.VIEWER.name)
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error checking pending access: ${e.message}")
            emptyList()
        }
    }

    override suspend fun deletePendingAccess(email: String) {
        val snapshot = firestore.collectionGroup("pending_access")
            .whereEqualTo("email", email)
            .get().await()
        
        snapshot.documents.forEach { doc ->
            doc.reference.delete()
        }
    }

    override suspend fun checkUserHasAccessToEvent(eventId: String, email: String, uid: String): Boolean {
        try {
            // 1. Check if user is the Event Owner
            val eventDoc = firestore.collection("events").document(eventId).get().await()
            if (eventDoc.exists() && eventDoc.getString("ownerId") == uid) return true

            // 2. Check Pending Access (Invitations)
            val pending = checkPendingAccess(email)
            if (pending.any { it.eventId == eventId }) return true

            // 3. Check specific Room Access subcollections
            val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue")
            for (room in rooms) {
                val collectionName = getUserCollectionName(room)
                val accessDoc = firestore.collection("events").document(eventId)
                    .collection("rooms").document(room)
                    .collection(collectionName).document(uid).get().await()
                if (accessDoc.exists()) return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }
}
