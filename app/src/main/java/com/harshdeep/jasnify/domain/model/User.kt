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
    val lastUsernameChangeTimestamp: Long? = null
)
