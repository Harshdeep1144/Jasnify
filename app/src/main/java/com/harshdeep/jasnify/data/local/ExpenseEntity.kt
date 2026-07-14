package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val title: String,
    val category: String,
    val amount: Double,
    val emoji: String,
    val lastUpdatedBy: String?,
    val lastUpdatedDate: Long, // Use timestamp
    val phoneNumber: String?,
    val note: String?,
    val isSynced: Boolean = false
)
