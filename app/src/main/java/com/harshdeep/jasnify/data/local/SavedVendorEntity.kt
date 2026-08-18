package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "saved_vendors",
    primaryKeys = ["vendorName", "eventId", "category"],
    indices = [Index(value = ["eventId"])]
)
data class SavedVendorEntity(
    val vendorName: String,
    val vendorId: String,
    val eventId: String,
    val category: String,
    val destination: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
