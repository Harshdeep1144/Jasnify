package com.harshdeep.jasnify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.local.BudgetDao
import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.repository.BudgetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val firestore: FirebaseFirestore
) : BudgetRepository {

    private val externalScope = CoroutineScope(Dispatchers.IO)

    override fun getAllExpenses(): Flow<List<ExpenseEntity>> = budgetDao.getAllExpenses()

    override fun getBudgetSettings(): Flow<BudgetEntity?> = budgetDao.getBudgetSettings()

    override suspend fun addExpense(expense: ExpenseEntity) {
        // 1. Update Room Instantly
        budgetDao.insertExpense(expense)
        
        // 2. Trigger Background Sync to Firestore
        externalScope.launch {
            try {
                firestore.collection("budgets")
                    .document("default_room") // Using a placeholder for room ID
                    .collection("expenses")
                    .document(expense.id)
                    .set(expense)
                    .await()
                
                // Mark as synced in local DB
                budgetDao.insertExpense(expense.copy(isSynced = true))
            } catch (e: Exception) {
                // Log or handle error - Room already has the data so UI stays updated
            }
        }
    }

    override suspend fun deleteExpense(expenseId: String) {
        // 1. Update Room Instantly
        budgetDao.deleteExpenseById(expenseId)

        // 2. Trigger Background Sync
        externalScope.launch {
            try {
                firestore.collection("budgets")
                    .document("default_room")
                    .collection("expenses")
                    .document(expenseId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override suspend fun updateBudget(totalBudget: Double) {
        val budgetSettings = BudgetEntity(totalBudget = totalBudget)
        budgetDao.updateBudgetSettings(budgetSettings)

        externalScope.launch {
            try {
                firestore.collection("budgets")
                    .document("default_room")
                    .set(mapOf("totalBudget" to totalBudget))
                    .await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override suspend fun renameCategory(oldName: String, newName: String) {
        budgetDao.renameCategory(oldName, newName)
        // Cloud sync for category rename would require a batch update in Firestore
    }

    override suspend fun deleteExpensesByCategory(category: String) {
        budgetDao.deleteExpensesByCategory(category)
        // Cloud sync for delete by category would require a batch delete in Firestore
    }

    override suspend fun syncWithCloud() {
        // Implementation for pulling data from Firestore if needed
        // For now, focusing on the Push logic described in the diagram
    }
}
