package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String = "",
    val eventId: String = "",
    val title: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val emoji: String = "",
    val lastUpdatedBy: String? = null,
    val lastUpdatedDate: Long = 0L,
    val phoneNumber: String? = null,
    val note: String? = null,
    val isSynced: Boolean = false
)
