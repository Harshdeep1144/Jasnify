package com.harshdeep.jasnify.data.local

import androidx.room.Entity

@Entity(tableName = "saved_vendors", primaryKeys = ["vendorName", "eventId", "category"])
data class SavedVendorEntity(
    val vendorName: String,
    val vendorId: String,
    val eventId: String,
    val category: String,
    val destination: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
