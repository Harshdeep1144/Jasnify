package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_venues",
    primaryKeys = ["venueName", "eventId"],
    indices = [Index(value = ["eventId"])]
)
data class SavedVenueEntity(
    val venueName: String = "",
    val venueId: String = "",
    val eventId: String = "",
    val destination: String = "mysaved",
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
