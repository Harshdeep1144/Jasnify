package com.harshdeep.jasnify.domain.model

import java.util.UUID

data class Checklist(
    val id: String = UUID.randomUUID().toString(),
    val ownerId: String = "",
    val title: String = "",
    val dateTime: String = "",
    val items: List<ChecklistItem> = emptyList(),
    val bgColorHex: Long = 0xFFE1F5FE, // Use Long for hex color
    val pinned: Boolean = false,
    val archived: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val checked: Boolean = false
)
