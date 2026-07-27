package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.MerchantUser
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUserProfile(user: User)
    suspend fun getUserProfile(uid: String): User?
    suspend fun updateUserProfile(user: User)
    suspend fun searchUsers(query: String): List<User>
    suspend fun getUserByEmail(email: String): User?

    // Merchant Profiles
    suspend fun getMerchantProfile(uid: String): MerchantUser?
    fun getMerchantProfileFlow(uid: String): Flow<MerchantUser?>
    suspend fun updateLastActive(uid: String, isMerchant: Boolean)
    
    // Room Access Management
    suspend fun grantRoomAccess(eventId: String, roomType: String, email: String, role: UserRole)
    suspend fun grantDirectRoomAccess(eventId: String, roomType: String, email: String, uid: String, role: UserRole)
    suspend fun grantAccessFromPending(eventId: String, email: String, uid: String, userProfile: User? = null)
    suspend fun removeRoomAccess(eventId: String, roomType: String, uid: String)
    fun getRoomUsers(eventId: String, roomType: String): Flow<List<User>>
    suspend fun checkPendingAccess(email: String): List<PendingAccess>
    suspend fun deletePendingAccess(email: String)
    suspend fun checkUserHasAccessToEvent(eventId: String, email: String, uid: String): Boolean
    suspend fun checkRoomAccess(eventId: String, roomType: String, uid: String): Boolean

    // Caching
    suspend fun getCachedRoomAccess(eventId: String, roomType: String, uid: String): Boolean?
    suspend fun cacheRoomAccess(eventId: String, roomType: String, uid: String, hasAccess: Boolean)
}

data class PendingAccess(
    val eventId: String,
    val roomType: String,
    val role: UserRole
)
