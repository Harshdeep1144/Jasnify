package com.harshdeep.jasnify.domain.model

import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "",
    val name: String = "",
    val typeId: Int? = null,
    val isMultiDay: Boolean = false,
    val date: Long? = null, // Using timestamp in milliseconds
    val budget: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val subEvents: List<SubEvent> = emptyList()
)

data class SubEvent(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val date: Long? = null,
    val isCompleted: Boolean = false
)
