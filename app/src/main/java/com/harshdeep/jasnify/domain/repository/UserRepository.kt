package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUserProfile(user: User)
    suspend fun getUserProfile(uid: String): User?
    suspend fun searchUsers(query: String): List<User>
    suspend fun getUserByEmail(email: String): User?
    
    // Room Access Management
    suspend fun grantRoomAccess(eventId: String, roomType: String, email: String, role: UserRole)
    suspend fun removeRoomAccess(eventId: String, roomType: String, uid: String)
    fun getRoomUsers(eventId: String, roomType: String): Flow<List<User>>
    suspend fun checkPendingAccess(email: String): List<PendingAccess>
    suspend fun deletePendingAccess(email: String)
    suspend fun checkUserHasAccessToEvent(eventId: String, email: String, uid: String): Boolean
}

data class PendingAccess(
    val eventId: String,
    val roomType: String,
    val role: UserRole
)
