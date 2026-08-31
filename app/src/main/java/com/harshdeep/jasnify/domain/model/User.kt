package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable

enum class UserRole {
    OWNER, EDITOR, VIEWER
}

@Immutable
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val role: UserRole = UserRole.VIEWER,
    val profilePictureUrl: String? = null,
    val lastUsernameChangeTimestamp: Long? = null,
    val lastPasswordChangeTimestamp: Long? = null,
    val explicitLogoutTimestamp: Long? = null,
    val joinedEvents: List<UserEvent> = emptyList(),
    val currentEventId: String? = null,
    val fcmToken: String? = null,
    val versionCode: Int? = null
)

@Immutable
data class UserEvent(
    val eventId: String = "",
    val eventName: String = "",
    val adminId: String = "",
    val roomRoles: Map<String, UserRole> = emptyMap()
)