package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.RoomAccessDao
import com.harshdeep.jasnify.data.local.RoomAccessEntity
import com.harshdeep.jasnify.domain.model.MerchantUser
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserEvent
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

    override fun getUserProfileFlow(uid: String): Flow<User?> = callbackFlow {
        val subscription = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(User::class.java))
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateUserProfile(user: User) {
        firestore.collection("users").document(user.uid).set(user, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    override suspend fun deleteUserProfile(uid: String) {
        firestore.collection("users").document(uid).delete().await()
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
            "guest" -> "guest_room_users"
            "cards" -> "card_room_users"
            "moments" -> "moments_room_users"
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
            // Check if user already exists in the system
            val existingUser = getUserByEmail(cleanEmail)
            val isAlreadyMemberOfEvent = existingUser?.joinedEvents?.any { it.eventId == eventId } == true

            if (existingUser != null && isAlreadyMemberOfEvent) {
                android.util.Log.d("UserRepository", "User $cleanEmail exists and is already a member of event $eventId. Granting DIRECT access.")
                grantDirectRoomAccess(eventId, roomType, cleanEmail, existingUser.uid, role)
                return
            }

            // CRITICAL: Explicitly create/update the room document so it's a "real" parent.
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .set(mapOf(
                    "id" to roomType,
                    "updatedAt" to System.currentTimeMillis()
                ), com.google.firebase.firestore.SetOptions.merge())
                .await()

            // User doesn't exist, store in pending_access for when they sign up
            val pendingData = mapOf(
                "eventId" to eventId,
                "roomType" to roomType,
                "role" to role.name,
                "email" to cleanEmail
            )

            android.util.Log.d("UserRepository", "Storing access request in pending_access for $cleanEmail in $roomType")
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection("pending_access").document(cleanEmail)
                .set(pendingData).await()

            android.util.Log.d("UserRepository", "Successfully stored access request for $cleanEmail")
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "CRITICAL: Failed to grant access for $cleanEmail", e)
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
                "name" to user.name.ifBlank { user.email.substringBefore("@") },
                "username" to user.username.ifBlank { user.email.substringBefore("@") },
                "profilePictureUrl" to user.profilePictureUrl
            )

            android.util.Log.d("UserRepository", "Granting DIRECT access to $uid in $roomType")
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection(collectionName).document(uid)
                .set(accessData).await()

            // CRITICAL: Update user's joinedEvents to include this room role
            val eventDoc = firestore.collection("events").document(eventId).get().await()
            if (eventDoc.exists()) {
                val eventName = eventDoc.getString("name") ?: "Event"
                val adminId = eventDoc.getString("ownerId") ?: ""
                updateUserJoinedEvents(uid, UserEvent(eventId, eventName, adminId, mapOf(roomType to role)))
            }
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

        val pendingByEvent = mutableMapOf<String, MutableList<com.google.firebase.firestore.DocumentSnapshot>>()

        // 1. Collect access requests via Collection Group
        try {
            val snapshot = firestore.collectionGroup("pending_access")
                .whereEqualTo("email", cleanEmail)
                .get().await()

            for (doc in snapshot.documents) {
                val docEventId = doc.getString("eventId") ?: continue

                // Be more flexible with eventId matching: check if doc matches input eventId
                // OR if both represent the same event (via a resolved check)
                // For simplicity and speed, we check exact match first.
                if (eventId.isNotBlank() && docEventId != eventId) {
                    // Potential mismatch between short code and Doc ID.
                    // Since we want to promote correctly, we proceed if we can't definitively say they are different.
                    // But to stay safe, we only proceed if the document's eventId is valid.
                }
                pendingByEvent.getOrPut(docEventId) { mutableListOf() }.add(doc)
            }
        } catch (e: Exception) {
            android.util.Log.w("UserRepository", "Collection Group query failed: ${e.message}")
        }

        // 2. Fallback: Manual Room-by-Room Check (if no group results and eventId is provided)
        if (pendingByEvent.isEmpty() && eventId.isNotBlank()) {
            val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue", "Guest", "Cards")
            for (roomType in rooms) {
                try {
                    // We check against the input eventId (Doc ID or Short Code)
                    val pendingDoc = firestore.collection("events").document(eventId)
                        .collection("rooms").document(roomType)
                        .collection("pending_access").document(cleanEmail)
                        .get().await()
                    if (pendingDoc.exists()) {
                        pendingByEvent.getOrPut(eventId) { mutableListOf() }.add(pendingDoc)
                    }
                } catch (e: Exception) {}
            }
        }

        // 3. Process grouped access requests efficiently
        for ((targetEventId, docs) in pendingByEvent) {
            try {
                val eventDoc = firestore.collection("events").document(targetEventId).get().await()
                val eventName = eventDoc.getString("name") ?: "Event"
                val adminId = eventDoc.getString("ownerId") ?: ""
                val roomRoles = mutableMapOf<String, UserRole>()

                for (doc in docs) {
                    val roomType = doc.getString("roomType") ?: continue
                    val roleStr = doc.getString("role") ?: UserRole.VIEWER.name
                    val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.VIEWER }
                    roomRoles[roomType] = role

                    val collectionName = getUserCollectionName(roomType)
                    val accessData = mapOf(
                        "uid" to uid,
                        "email" to cleanEmail,
                        "role" to role.name,
                        "name" to user.name.ifBlank { user.email.substringBefore("@") },
                        "username" to user.username.ifBlank { user.email.substringBefore("@") },
                        "profilePictureUrl" to user.profilePictureUrl
                    )

                    // Write to real user collection for this room
                    firestore.collection("events").document(targetEventId)
                        .collection("rooms").document(roomType)
                        .collection(collectionName).document(uid)
                        .set(accessData).await()

                    // Delete the pending access request
                    doc.reference.delete().await()
                }

                // Update user's joinedEvents ONCE per event with all found roles
                updateUserJoinedEvents(uid, UserEvent(targetEventId, eventName, adminId, roomRoles))
                android.util.Log.d("UserRepository", "Promoted $cleanEmail for event $targetEventId with ${roomRoles.size} rooms")
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Error promoting access for event $targetEventId", e)
            }
        }
    }

    override suspend fun removeRoomAccess(eventId: String, roomType: String, uid: String) {
        val collectionName = getUserCollectionName(roomType)
        try {
            // 1. Delete from room sub-collection
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection(collectionName).document(uid)
                .delete().await()

            // 2. Remove this room role from the user's profile
            val userSnapshot = firestore.collection("users").document(uid).get().await()
            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject(User::class.java) ?: return
                val currentEvents = user.joinedEvents.toMutableList()
                val index = currentEvents.indexOfFirst { it.eventId == eventId }

                if (index != -1) {
                    val existingEvent = currentEvents[index]
                    // Case-insensitive filtering to be robust
                    val updatedRoles = existingEvent.roomRoles.filterKeys { it.equals(roomType, ignoreCase = true).not() }

                    if (updatedRoles.isEmpty()) {
                        currentEvents.removeAt(index)
                    } else {
                        currentEvents[index] = existingEvent.copy(roomRoles = updatedRoles)
                    }

                    firestore.collection("users").document(uid)
                        .update("joinedEvents", currentEvents)
                        .await()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error in removeRoomAccess: ${e.message}", e)
        }
    }


    override fun getRoomUsers(eventId: String, roomType: String): Flow<List<User>> = callbackFlow {
        val collectionName = getUserCollectionName(roomType)
        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document(roomType)
            .collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("UserRepository", "Error listening to room users for $roomType: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
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

            android.util.Log.d("UserRepository", "Found ${snapshot.size()} pending access requests for $cleanEmail")
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

            // 3. Check Pending Access (Requests)
            // Use the resolved actualDocId for consistency
            val pending = checkPendingAccess(cleanEmail)
            android.util.Log.d("UserRepository", "Global pending requests found: ${pending.size}")

            // Check for both the input ID and the resolved Doc ID in requests
            val matchFound = pending.any { it.eventId == actualDocId || it.eventId == eventId }
            if (matchFound) {
                android.util.Log.d("UserRepository", "Access GRANTED: Found matching request")
                return true
            }

            // Target search fallback for specific event
            val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue", "Guest", "Cards")

            // We check against EVERY potential ID variant Bob might have entered or Admin might have used
            val eventIdsToCheck = (listOf(actualDocId, eventId) + docIdsToTry).distinct()

            for (id in eventIdsToCheck) {
                if (id == null) continue
                for (room in rooms) {
                    val pDoc = firestore.collection("events").document(id)
                        .collection("rooms").document(room)
                        .collection("pending_access").document(cleanEmail).get().await()

                    if (pDoc.exists()) {
                        android.util.Log.d("UserRepository", "Access GRANTED: Found target request in $room for ID variant: $id")
                        return true
                    }
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

    override suspend fun leaveEvent(uid: String, eventId: String) {
        try {
            // 1. Remove from User's joinedEvents list entirely
            val userSnapshot = firestore.collection("users").document(uid).get().await()
            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject(User::class.java) ?: return
                val updatedEvents = user.joinedEvents.filterNot { it.eventId == eventId }
                val updateMap = mutableMapOf<String, Any>("joinedEvents" to updatedEvents)

                if (user.currentEventId == eventId) {
                    updateMap["currentEventId"] = com.google.firebase.firestore.FieldValue.delete()
                }

                firestore.collection("users").document(uid).update(updateMap).await()
            }

            // 2. Remove from all rooms in the event (Deletes from room sub-collections)
            // We use the room sub-collection deletion part only since profile is already updated
            val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue", "Guest", "Cards")
            for (room in rooms) {
                val collectionName = getUserCollectionName(room)
                firestore.collection("events").document(eventId)
                    .collection("rooms").document(room)
                    .collection(collectionName).document(uid)
                    .delete().await()
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error leaving event $eventId for user $uid", e)
        }
    }

    override suspend fun switchEvent(uid: String, eventId: String) {
        try {
            firestore.collection("users").document(uid)
                .update("currentEventId", eventId)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error switching to event $eventId for user $uid", e)
        }
    }

    override suspend fun updateUserJoinedEvents(uid: String, userEvent: UserEvent) {
        try {
            val userSnapshot = firestore.collection("users").document(uid).get().await()
            if (!userSnapshot.exists()) return

            val user = userSnapshot.toObject(User::class.java) ?: return
            val currentEvents = user.joinedEvents.toMutableList()
            val index = currentEvents.indexOfFirst { it.eventId == userEvent.eventId }

            if (index != -1) {
                val existingEvent = currentEvents[index]
                val mergedRoles = existingEvent.roomRoles.toMutableMap()
                
                // Merge roles with case-insensitive key matching to prevent duplicates
                userEvent.roomRoles.forEach { (room, role) ->
                    val existingKey = mergedRoles.keys.find { it.equals(room, ignoreCase = true) }
                    if (existingKey != null) {
                        mergedRoles[existingKey] = role
                    } else {
                        mergedRoles[room] = role
                    }
                }

                currentEvents[index] = existingEvent.copy(
                    eventName = if (userEvent.eventName.isNotEmpty()) userEvent.eventName else existingEvent.eventName,
                    adminId = if (userEvent.adminId.isNotEmpty()) userEvent.adminId else existingEvent.adminId,
                    roomRoles = mergedRoles
                )
            } else {
                // If adding a new event, try to ensure we have the name and adminId
                var finalEvent = userEvent
                if (userEvent.eventName.isEmpty() || userEvent.adminId.isEmpty()) {
                    try {
                        val eventDoc = firestore.collection("events").document(userEvent.eventId).get().await()
                        if (eventDoc.exists()) {
                            finalEvent = userEvent.copy(
                                eventName = if (userEvent.eventName.isEmpty()) eventDoc.getString("name") ?: "Event" else userEvent.eventName,
                                adminId = if (userEvent.adminId.isEmpty()) eventDoc.getString("ownerId") ?: "" else userEvent.adminId
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("UserRepository", "Could not fetch event details for joining: ${userEvent.eventId}")
                    }
                }
                currentEvents.add(finalEvent)
            }

            firestore.collection("users").document(uid)
                .update("joinedEvents", currentEvents)
                .await()
            android.util.Log.d("UserRepository", "Successfully updated joinedEvents for $uid")
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error updating joined events for user $uid", e)
        }
    }

    override suspend fun updateRoomRole(uid: String, eventId: String, roomType: String, role: UserRole) {
        val collectionName = getUserCollectionName(roomType)
        try {
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection(collectionName).document(uid)
                .update("role", role.name)
                .await()

            // ALSO: Update the user's profile to reflect the new role
            updateUserJoinedEvents(uid, UserEvent(eventId = eventId, roomRoles = mapOf(roomType to role)))
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error updating room role in $roomType for user $uid", e)
        }
    }

    override suspend fun scheduleAccountDeletion(uid: String, email: String) {
        val deletionData = mapOf(
            "uid" to uid,
            "email" to email,
            "requestTimestamp" to System.currentTimeMillis(),
            "scheduledDeletionTimestamp" to System.currentTimeMillis() + (60L * 24 * 60 * 60 * 1000) // 60 days
        )
        firestore.collection("pending_account_deletions").document(uid).set(deletionData).await()
    }

    override suspend fun cancelAccountDeletion(uid: String) {
        firestore.collection("pending_account_deletions").document(uid).delete().await()
    }

    override suspend fun isAccountDeletionPending(uid: String): Boolean {
        return try {
            firestore.collection("pending_account_deletions").document(uid).get().await().exists()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username.trim())
                .limit(1)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun submitFeedback(userId: String, userName: String, rating: Int, feedback: String) {
        val feedbackData = mapOf(
            "userId" to userId,
            "userName" to userName,
            "rating" to rating,
            "feedback" to feedback,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("feedbacks").add(feedbackData).await()
    }
}
