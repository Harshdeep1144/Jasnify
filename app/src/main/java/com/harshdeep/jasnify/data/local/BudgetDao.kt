package com.harshdeep.jasnify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    // Expense queries
    @Query("SELECT * FROM expenses ORDER BY lastUpdatedDate DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: String)

    @Query("UPDATE expenses SET category = :newName WHERE category = :oldName")
    suspend fun renameCategory(oldName: String, newName: String)

    @Query("DELETE FROM expenses WHERE category = :category")
    suspend fun deleteExpensesByCategory(category: String)

    // Budget settings queries
    @Query("SELECT * FROM budget_settings WHERE id = 0")
    fun getBudgetSettings(): Flow<BudgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBudgetSettings(budget: BudgetEntity)
}
