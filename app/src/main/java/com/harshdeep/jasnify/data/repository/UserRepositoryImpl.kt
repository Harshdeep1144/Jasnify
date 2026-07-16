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
        return try {
            firestore.collection("users")
                .whereEqualTo("email", email)
                .get().await()
                .toObjects(User::class.java).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun grantRoomAccess(eventId: String, roomType: String, email: String, role: UserRole) {
        val user = getUserByEmail(email)
        if (user != null) {
            val accessData = mapOf(
                "uid" to user.uid,
                "email" to email,
                "role" to role.name,
                "name" to user.name,
                "username" to user.username,
                "profilePictureUrl" to user.profilePictureUrl
            )
            firestore.collection("events").document(eventId)
                .collection("rooms").document(roomType)
                .collection("access").document(user.uid)
                .set(accessData).await()
        } else {
            // User doesn't exist, store in pending access
            val pendingData = mapOf(
                "eventId" to eventId,
                "roomType" to roomType,
                "role" to role.name,
                "email" to email
            )
            firestore.collection("pending_access").add(pendingData).await()
        }
    }

    override suspend fun removeRoomAccess(eventId: String, roomType: String, uid: String) {
        firestore.collection("events").document(eventId)
            .collection("rooms").document(roomType)
            .collection("access").document(uid)
            .delete().await()
    }

    override fun getRoomUsers(eventId: String, roomType: String): Flow<List<User>> = callbackFlow {
        val subscription = firestore.collection("events").document(eventId)
            .collection("rooms").document(roomType)
            .collection("access")
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
            val snapshot = firestore.collection("pending_access")
                .whereEqualTo("email", email)
                .get().await()
            
            snapshot.documents.map { doc ->
                PendingAccess(
                    eventId = doc.getString("eventId") ?: "",
                    roomType = doc.getString("roomType") ?: "",
                    role = UserRole.valueOf(doc.getString("role") ?: UserRole.VIEWER.name)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun deletePendingAccess(email: String) {
        val snapshot = firestore.collection("pending_access")
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
                val accessDoc = firestore.collection("events").document(eventId)
                    .collection("rooms").document(room)
                    .collection("access").document(uid).get().await()
                if (accessDoc.exists()) return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }
}
