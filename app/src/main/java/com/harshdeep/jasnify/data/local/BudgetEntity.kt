package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_settings")
data class BudgetEntity(
    @PrimaryKey val eventId: String = "",
    val totalBudget: Double? = null,
    val currency: String = "INR"
)
