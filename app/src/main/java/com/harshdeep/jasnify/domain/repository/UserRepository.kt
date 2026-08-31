package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.domain.model.MerchantUser
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUserProfile(user: User)
    suspend fun getUserProfile(uid: String): User?
    fun getUserProfileFlow(uid: String): Flow<User?>
    suspend fun updateUserProfile(user: User)
    suspend fun updateFcmToken(uid: String, token: String)
    suspend fun updateAppVersion(uid: String, versionCode: Int)
    suspend fun deleteUserProfile(uid: String)
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
    fun getRoomPictureUrlFlow(eventId: String, roomType: String): Flow<String?>
    suspend fun updateRoomPictureUrl(eventId: String, roomType: String, url: String)
    suspend fun checkPendingAccess(email: String): List<PendingAccess>
    suspend fun deletePendingAccess(email: String)
    suspend fun checkUserHasAccessToEvent(eventId: String, email: String, uid: String): Boolean
    suspend fun checkRoomAccess(eventId: String, roomType: String, uid: String): Boolean
    suspend fun leaveEvent(uid: String, eventId: String)
    suspend fun switchEvent(uid: String, eventId: String)
    suspend fun updateUserJoinedEvents(uid: String, userEvent: com.harshdeep.jasnify.domain.model.UserEvent)
    suspend fun updateRoomRole(uid: String, eventId: String, roomType: String, role: UserRole)

    // Caching
    suspend fun getCachedRoomAccess(eventId: String, roomType: String, uid: String): Boolean?
    suspend fun cacheRoomAccess(eventId: String, roomType: String, uid: String, hasAccess: Boolean)

    // Account Deletion
    suspend fun scheduleAccountDeletion(uid: String, email: String)
    suspend fun cancelAccountDeletion(uid: String)
    suspend fun isAccountDeletionPending(uid: String): Boolean
    suspend fun isUsernameTaken(username: String): Boolean

    // Feedback
    suspend fun submitFeedback(userId: String, userName: String, rating: Int, feedback: String)
}

data class PendingAccess(
    val eventId: String,
    val roomType: String,
    val role: UserRole
)
