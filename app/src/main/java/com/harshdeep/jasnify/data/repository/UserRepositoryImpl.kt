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
        
        try {
            // CRITICAL: Explicitly create/update the room document so it's a "real" parent.
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .set(mapOf(
                    "id" to roomType,
                    "updatedAt" to System.currentTimeMillis()
                ), com.google.firebase.firestore.SetOptions.merge())
                .await()

            // REQUIREMENT: Always store in pending_access first, even if user exists.
            // This forces the user to use the "Have an Event ID" flow to join.
            val pendingData = mapOf(
                "eventId" to eventId,
                "roomType" to roomType,
                "role" to role.name,
                "email" to cleanEmail
            )
            
            android.util.Log.d("UserRepository", "Storing invitation in pending_access for $cleanEmail in $roomType")
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection("pending_access").document(cleanEmail)
                .set(pendingData).await()
                
            android.util.Log.d("UserRepository", "Successfully stored invitation for $cleanEmail")
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "CRITICAL: Failed to store invitation for $cleanEmail", e)
            throw e
        }
    }

    override suspend fun grantDirectRoomAccess(eventId: String, roomType: String, email: String, uid: String, role: UserRole) {
        val cleanEmail = email.lowercase().trim()
        val user = getUserProfile(uid) ?: return
        val collectionName = getUserCollectionName(roomType)
        
        try {
            // Ensure parent room doc exists
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .set(mapOf("updatedAt" to System.currentTimeMillis()), com.google.firebase.firestore.SetOptions.merge())
                .await()

            val accessData = mapOf(
                "uid" to uid,
                "email" to cleanEmail,
                "role" to role.name,
                "name" to user.name,
                "username" to user.username,
                "profilePictureUrl" to user.profilePictureUrl
            )
            
            android.util.Log.d("UserRepository", "Granting DIRECT access to $uid in $roomType")
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection(collectionName).document(uid)
                .set(accessData).await()
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error in grantDirectRoomAccess", e)
        }
    }

    override suspend fun grantAccessFromPending(eventId: String, email: String, uid: String) {
        val cleanEmail = email.lowercase().trim()
        val user = getUserProfile(uid) ?: return
        
        try {
            // 1. Find all pending invitations for this email in this specific event
            // Note: A user could be invited to multiple rooms in the same event
            val snapshot = firestore.collection("events").document(eventId)
                .collection("rooms")
                .get().await()

            for (roomDoc in snapshot.documents) {
                val roomType = roomDoc.id
                val pendingDoc = firestore.collection("events").document(eventId)
                    .collection("rooms").document(roomType)
                    .collection("pending_access").document(cleanEmail)
                    .get().await()

                if (pendingDoc.exists()) {
                    val roleStr = pendingDoc.getString("role") ?: UserRole.VIEWER.name
                    val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.VIEWER }
                    val collectionName = getUserCollectionName(roomType)

                    val accessData = mapOf(
                        "uid" to user.uid,
                        "email" to cleanEmail,
                        "role" to role.name,
                        "name" to user.name,
                        "username" to user.username,
                        "profilePictureUrl" to user.profilePictureUrl
                    )

                    // Write to real user collection
                    firestore.collection("events").document(eventId)
                        .collection("rooms").document(roomType)
                        .collection(collectionName).document(user.uid)
                        .set(accessData).await()

                    // Delete from pending
                    pendingDoc.reference.delete().await()
                    android.util.Log.d("UserRepository", "Granted real access to $cleanEmail in $roomType")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error granting access from pending", e)
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
        val cleanEmail = email.lowercase().trim()
        return try {
            // Use collectionGroup for nested pending_access lookups across all events/rooms
            android.util.Log.d("UserRepository", "Checking global pending access for: $cleanEmail")
            val snapshot = firestore.collectionGroup("pending_access")
                .whereEqualTo("email", cleanEmail)
                .get().await()
            
            android.util.Log.d("UserRepository", "Found ${snapshot.size()} pending invitations for $cleanEmail")
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
        val cleanEmail = email.lowercase().trim()
        val snapshot = firestore.collectionGroup("pending_access")
            .whereEqualTo("email", cleanEmail)
            .get().await()
        
        snapshot.documents.forEach { doc ->
            doc.reference.delete()
        }
    }

    override suspend fun checkUserHasAccessToEvent(eventId: String, email: String, uid: String): Boolean {
        val cleanEmail = email.lowercase().trim()
        android.util.Log.d("UserRepository", "--- START ACCESS CHECK ---")
        android.util.Log.d("UserRepository", "Input EventID: $eventId")
        
        try {
            // 1. Resolve the actual Event Document Name and Data
            // The input eventId could be a Doc Name, a field ID, or a short code.
            val docIdsToTry = listOf(eventId, eventId.lowercase(), eventId.uppercase()).distinct()
            var eventDoc: com.google.firebase.firestore.DocumentSnapshot? = null
            var actualDocId: String? = null

            // Try direct Doc Name match first
            for (id in docIdsToTry) {
                val doc = firestore.collection("events").document(id).get().await()
                if (doc.exists()) {
                    eventDoc = doc
                    actualDocId = id
                    break
                }
            }

            // Try field ID match if not found by Doc Name
            if (eventDoc == null) {
                val fieldQuery = firestore.collection("events")
                    .whereIn("id", docIdsToTry)
                    .limit(1).get().await()
                if (!fieldQuery.isEmpty) {
                    eventDoc = fieldQuery.documents.first()
                    actualDocId = eventDoc.id
                }
            }

            if (eventDoc == null || actualDocId == null) {
                android.util.Log.e("UserRepository", "COULD NOT FIND EVENT in database for ID: $eventId")
                return false
            }

            android.util.Log.d("UserRepository", "Resolved Event Doc ID: $actualDocId")

            // 2. Check if user is the Event Owner
            val ownerId = eventDoc.getString("ownerId")
            android.util.Log.d("UserRepository", "Event Owner ID: $ownerId")
            if (ownerId == uid) {
                android.util.Log.d("UserRepository", "Access GRANTED: User is Event Owner")
                return true
            }

            // 3. Check Pending Access (Invitations)
            // Use the resolved actualDocId for consistency
            val pending = checkPendingAccess(cleanEmail)
            android.util.Log.d("UserRepository", "Global pending invitations found: ${pending.size}")
            
            // Check for both the input ID and the resolved Doc ID in invitations
            val matchFound = pending.any { it.eventId == actualDocId || it.eventId == eventId }
            if (matchFound) {
                android.util.Log.d("UserRepository", "Access GRANTED: Found matching invitation")
                return true
            }

            // Target search fallback for specific event (useful if global index is building)
            val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue")
            for (room in rooms) {
                val pDoc = firestore.collection("events").document(actualDocId)
                    .collection("rooms").document(room)
                    .collection("pending_access").document(cleanEmail).get().await()
                
                if (pDoc.exists()) {
                    android.util.Log.d("UserRepository", "Access GRANTED: Found target invitation in $room")
                    return true
                }
            }

            // 4. Check Real Membership
            for (room in rooms) {
                val collectionName = getUserCollectionName(room)
                try {
                    val accessDoc = firestore.collection("events").document(actualDocId)
                        .collection("rooms").document(room)
                        .collection(collectionName).document(uid).get().await()
                    
                    if (accessDoc.exists()) {
                        android.util.Log.d("UserRepository", "Access GRANTED: Real member of $room")
                        return true
                    }
                } catch (e: Exception) {
                    android.util.Log.e("UserRepository", "Membership check failed for room $room (likely permission)")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "CRITICAL Error in checkUserHasAccessToEvent", e)
            return false
        }
        
        android.util.Log.w("UserRepository", "Access DENIED for $cleanEmail to event $eventId")
        android.util.Log.d("UserRepository", "--- END ACCESS CHECK ---")
        return false
    }
}
