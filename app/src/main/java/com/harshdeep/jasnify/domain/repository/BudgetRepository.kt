package com.harshdeep.jasnify.domain.repository

import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    fun getBudgetSettings(): Flow<BudgetEntity?>
    suspend fun addExpense(expense: ExpenseEntity)
    suspend fun deleteExpense(expenseId: String)
    suspend fun updateBudget(totalBudget: Double)
    suspend fun renameCategory(oldName: String, newName: String)
    suspend fun deleteExpensesByCategory(category: String)
    suspend fun syncWithCloud()
}
