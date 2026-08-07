package com.harshdeep.jasnify.domain.model

import java.util.UUID

data class Guest(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val type: String = "",
    val contactNo: String = "",
    val isInvited: Boolean = false,
    val imageUrl: String? = null,
    val invitedBy: String? = null,
    val invitedAt: String? = null,
    val lastUpdatedBy: String? = null,
    val lastUpdatedAt: String? = null
)

data class GuestType(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val guestCount: Int = 0
)
