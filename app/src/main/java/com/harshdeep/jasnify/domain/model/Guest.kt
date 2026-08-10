package com.harshdeep.jasnify.domain.model

import java.util.UUID
import com.google.firebase.firestore.PropertyName

data class Guest(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val type: String = "",
    val contactNo: String = "",
    @get:PropertyName("invited")
    @set:PropertyName("invited")
    var invited: Boolean = false,
    val imageUrl: String? = null,
    val invitedBy: String? = null,
    val invitedAt: String? = null,
    val lastUpdatedBy: String? = null,
    val lastUpdatedAt: String? = null,
    val addedBy: String? = null,
    val addedAt: String? = null
) {
    // Required no-argument constructor for Firestore
    constructor() : this(id = "")
}

data class GuestType(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val guestCount: Int = 0
)
