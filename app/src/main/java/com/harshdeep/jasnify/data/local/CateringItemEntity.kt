package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.harshdeep.jasnify.presentation.components.chip.Dietary

@Entity(tableName = "catering_items")
data class CateringItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val dietary: Dietary,
    val type: String,
    val cuisine: String,
    val isSynced: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
