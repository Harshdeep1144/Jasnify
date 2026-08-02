package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "",
    val ownerName: String = "",
    val name: String = "",
    val typeId: Int? = null,
    val multiDay: Boolean = false, // Renamed from isMultiDay to prevent Firestore mapping issues
    val date: Long? = null, // Using timestamp in milliseconds
    val budget: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val subEvents: List<SubEvent> = emptyList()
)

@Immutable
data class SubEvent(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val date: Long? = null,
    val completed: Boolean = false // Renamed from isCompleted to prevent Firestore mapping issues
)