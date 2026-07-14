package com.harshdeep.jasnify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    // Expense queries
    @Query("SELECT * FROM expenses WHERE eventId = :eventId ORDER BY lastUpdatedDate DESC")
    fun getAllExpenses(eventId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: String)

    @Query("UPDATE expenses SET category = :newName WHERE category = :oldName AND eventId = :eventId")
    suspend fun renameCategory(oldName: String, newName: String, eventId: String)

    @Query("DELETE FROM expenses WHERE category = :category AND eventId = :eventId")
    suspend fun deleteExpensesByCategory(category: String, eventId: String)

    // Budget settings queries
    @Query("SELECT * FROM budget_settings WHERE eventId = :eventId")
    fun getBudgetSettings(eventId: String): Flow<BudgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBudgetSettings(budget: BudgetEntity)
}
