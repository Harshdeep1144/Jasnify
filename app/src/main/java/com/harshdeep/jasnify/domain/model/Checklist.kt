package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class Checklist(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "",
    val eventId: String? = null, // Add eventId
    val title: String = "",
    val dateTime: String = "",
    val items: List<ChecklistItem> = emptyList(),
    val bgColorHex: Long = 0xFFE1F5FE, // Use Long for hex color
    val pinned: Boolean = false,
    val archived: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

@Immutable
data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val checked: Boolean = false
)
