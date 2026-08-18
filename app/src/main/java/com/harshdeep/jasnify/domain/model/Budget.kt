package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ExpenseItem(
    val id: String,
    val title: String,
    val category: String,
    val amount: String,
    val emoji: String = "💸",
    val lastUpdatedBy: String? = null,
    val lastUpdatedDate: String? = null,
    val phoneNumber: String? = null,
    val note: String? = null
)

@Immutable
data class CategorySummaryData(
    val name: String,
    val amountFormatted: String,
    val amountRaw: Double,
    val emojis: List<String>,
    val totalCount: Int
)