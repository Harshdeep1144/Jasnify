package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "room_access",
    indices = [
        Index(value = ["eventId"]),
        Index(value = ["roomType"]),
        Index(value = ["uid"])
    ]
)
data class RoomAccessEntity(
    @PrimaryKey val compositeKey: String, // eventId + roomType + uid
    val eventId: String,
    val roomType: String,
    val uid: String,
    val hasAccess: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)
