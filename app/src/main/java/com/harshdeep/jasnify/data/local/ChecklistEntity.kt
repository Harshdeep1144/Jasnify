package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.harshdeep.jasnify.domain.model.ChecklistItem

@Entity(
    tableName = "checklists",
    indices = [
        Index(value = ["eventId"]),
        Index(value = ["archived"]),
        Index(value = ["pinned"])
    ]
)
data class ChecklistEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val eventId: String? = null, // Add eventId
    val title: String,
    val dateTime: String,
    val items: List<ChecklistItem>,
    val bgColorHex: Long,
    val pinned: Boolean,
    val archived: Boolean,
    val lastUpdated: Long,
    val createdAt: Long
)
