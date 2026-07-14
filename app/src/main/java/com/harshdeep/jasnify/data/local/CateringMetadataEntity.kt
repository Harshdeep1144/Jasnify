package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catering_metadata")
data class CateringMetadataEntity(
    @PrimaryKey val eventId: String,
    val isSeeded: Boolean = false
)
