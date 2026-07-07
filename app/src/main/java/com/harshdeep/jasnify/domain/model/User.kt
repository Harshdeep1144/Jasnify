package com.harshdeep.jasnify.domain.model

enum class UserRole {
    OWNER, EDITOR, VIEWER
}

data class User(
    val name: String,
    val username: String,
    val role: UserRole,
    val profilePictureUrl: String? = null
)
