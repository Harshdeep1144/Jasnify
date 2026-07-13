package com.harshdeep.jasnify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_settings")
data class BudgetEntity(
    @PrimaryKey val id: Int = 0, // Singleton-like entity
    val totalBudget: Double,
    val currency: String = "INR"
)
