package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_access")
data class RoomAccessEntity(
    @PrimaryKey val compositeKey: String, // eventId + roomType + uid
    val eventId: String,
    val roomType: String,
    val uid: String,
    val hasAccess: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)
