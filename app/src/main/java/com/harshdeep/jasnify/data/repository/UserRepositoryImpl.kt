package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.RoomAccessDao
import com.harshdeep.jasnify.data.local.RoomAccessEntity
import com.harshdeep.jasnify.domain.model.MerchantUser
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
    private val firestore: FirebaseFirestore,
    private val roomAccessDao: RoomAccessDao
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

    override suspend fun getMerchantProfile(uid: String): MerchantUser? {
        return try {
            firestore.collection("merchants").document(uid).get().await().toObject(MerchantUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override fun getMerchantProfileFlow(uid: String): Flow<MerchantUser?> = callbackFlow {
        val subscription = firestore.collection("merchants").document(uid)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(MerchantUser::class.java))
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateLastActive(uid: String, isMerchant: Boolean) {
        val collection = if (isMerchant) "merchants" else "users"
        try {
            firestore.collection(collection).document(uid)
                .update("lastActive", System.currentTimeMillis())
                .await()
        } catch (e: Exception) {
            // Document might not exist or field missing, ignore for now
        }
    }

    override suspend fun updateUserProfile(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
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

    override suspend fun grantAccessFromPending(eventId: String, email: String, uid: String, userProfile: User?) {
        val cleanEmail = email.lowercase().trim()
        android.util.Log.d("UserRepository", "grantAccessFromPending: email=$cleanEmail, uid=$uid, eventId=$eventId")
        
        // Use provided profile or fetch it
        val user = userProfile ?: getUserProfile(uid)

        if (user == null) {
            android.util.Log.w("UserRepository", "grantAccessFromPending: User profile not found for $uid. Cannot promote.")
            return
        }

        // 1. Primary Attempt: Efficient Collection Group Query (Requires Index)
        try {
            android.util.Log.d("UserRepository", "Attempting collectionGroup search for pending_access with email: $cleanEmail")
            val snapshot = firestore.collectionGroup("pending_access")
                .whereEqualTo("email", cleanEmail)
                .get().await()

            if (!snapshot.isEmpty) {
                android.util.Log.d("UserRepository", "Index-based promotion found ${snapshot.size()} docs")
                for (doc in snapshot.documents) {
                    val docEventId = doc.getString("eventId") ?: ""
                    android.util.Log.d("UserRepository", "Checking doc with docEventId: $docEventId against input eventId: $eventId")
                    
                    // If eventId is provided, we only process invitations for that specific event.
                    if (eventId.isNotBlank() && docEventId != eventId) {
                        android.util.Log.d("UserRepository", "Skipping doc: ID mismatch")
                        continue
                    }
                    
                    promoteInvitation(doc, user, cleanEmail)
                }
                return // Success via primary method
            } else {
                android.util.Log.d("UserRepository", "No documents found in collectionGroup search for $cleanEmail")
            }
        } catch (e: Exception) {
            android.util.Log.w("UserRepository", "Collection Group query failed: ${e.message}")
        }

        // 2. Fallback: Manual Room-by-Room Check (Works without Index)
        if (eventId.isNotBlank()) {
            android.util.Log.d("UserRepository", "Running fallback room-by-room promotion for event $eventId")
            val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue")
            for (roomType in rooms) {
                try {
                    val pendingDoc = firestore.collection("events").document(eventId)
                        .collection("rooms").document(roomType)
                        .collection("pending_access").document(cleanEmail)
                        .get().await()

                    if (pendingDoc.exists()) {
                        android.util.Log.d("UserRepository", "Found pending invitation in $roomType. Promoting...")
                        promoteInvitation(pendingDoc, user, cleanEmail)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("UserRepository", "Error checking room $roomType for fallback promotion", e)
                }
            }
        }
    }

    private suspend fun promoteInvitation(doc: com.google.firebase.firestore.DocumentSnapshot, user: User, cleanEmail: String) {
        val docEventId = doc.getString("eventId") ?: return
        val roomType = doc.getString("roomType") ?: return
        val roleStr = doc.getString("role") ?: UserRole.VIEWER.name
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

        try {
            // 1. Write to real user collection for this room
            firestore.collection("events").document(docEventId)
                .collection("rooms").document(roomType)
                .collection(collectionName).document(user.uid)
                .set(accessData).await()

            // 2. Delete the pending invitation
            doc.reference.delete().await()
            android.util.Log.d("UserRepository", "Promoted $cleanEmail to real user in $roomType of event $docEventId")
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to promote invitation for $roomType", e)
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
                try {
                    val doc = firestore.collection("events").document(id).get().await()
                    if (doc.exists()) {
                        eventDoc = doc
                        actualDocId = id
                        break
                    }
                } catch (e: Exception) {
                    android.util.Log.w("UserRepository", "Permission denied checking Doc ID: $id")
                }
            }

            // Try field ID match if not found by Doc Name
            if (eventDoc == null) {
                try {
                    val fieldQuery = firestore.collection("events")
                        .whereIn("id", docIdsToTry)
                        .limit(1).get().await()
                    if (!fieldQuery.isEmpty) {
                        eventDoc = fieldQuery.documents.first()
                        actualDocId = eventDoc.id
                    }
                } catch (e: Exception) {
                    android.util.Log.w("UserRepository", "Permission denied checking 'id' field query")
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

    override suspend fun checkRoomAccess(eventId: String, roomType: String, uid: String): Boolean {
        try {
            // 1. Owner check
            val event = firestore.collection("events").document(eventId).get().await()
            if (event.getString("ownerId") == uid) {
                cacheRoomAccess(eventId, roomType, uid, true)
                return true
            }

            // 2. Room membership check
            val colName = getUserCollectionName(roomType)
            val membership = firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection(colName).document(uid).get().await()

            val hasAccess = membership.exists()
            cacheRoomAccess(eventId, roomType, uid, hasAccess)
            return hasAccess
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error in checkRoomAccess for $roomType", e)
            return false
        }
    }

    override suspend fun getCachedRoomAccess(eventId: String, roomType: String, uid: String): Boolean? {
        return roomAccessDao.getAccess(eventId, roomType, uid)?.hasAccess
    }

    override suspend fun cacheRoomAccess(eventId: String, roomType: String, uid: String, hasAccess: Boolean) {
        val compositeKey = "$eventId-$roomType-$uid"
        roomAccessDao.insertAccess(RoomAccessEntity(compositeKey, eventId, roomType, uid, hasAccess))
    }
}
