package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllExpenses(eventId: String): Flow<List<ExpenseEntity>>
    fun getBudgetSettings(eventId: String): Flow<BudgetEntity?>
    suspend fun addExpense(expense: ExpenseEntity)
    suspend fun deleteExpense(expenseId: String, eventId: String)
    suspend fun updateBudget(totalBudget: Double, eventId: String)
    suspend fun renameCategory(oldName: String, newName: String, eventId: String)
    suspend fun deleteExpensesByCategory(category: String, eventId: String)
    suspend fun syncWithCloud()
}
