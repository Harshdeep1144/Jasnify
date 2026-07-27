package com.harshdeep.jasnify.domain.model

enum class UserRole {
    OWNER, EDITOR, VIEWER
}

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val role: UserRole = UserRole.VIEWER,
    val profilePictureUrl: String? = null,
    val lastUsernameChangeTimestamp: Long? = null,
    val joinedEvents: List<UserEvent> = emptyList(),
    val currentEventId: String? = null
)

data class UserEvent(
    val eventId: String = "",
    val eventName: String = "",
    val adminId: String = "",
    val roomRoles: Map<String, UserRole> = emptyMap()
)
