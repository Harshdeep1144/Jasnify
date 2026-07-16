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

    override fun getAllExpenses(eventId: String): Flow<List<ExpenseEntity>> = budgetDao.getAllExpenses(eventId)

    override fun getBudgetSettings(eventId: String): Flow<BudgetEntity?> = budgetDao.getBudgetSettings(eventId)

    override suspend fun addExpense(expense: ExpenseEntity) {
        // 1. Update Room Instantly
        budgetDao.insertExpense(expense)
        
        // 2. Trigger Background Sync to Firestore
        externalScope.launch {
            try {
                // Ensure parent document exists to prevent console display issues
                firestore.collection("events").document(expense.eventId)
                    .collection("rooms").document("Budget")
                    .set(mapOf("updatedAt" to System.currentTimeMillis()), com.google.firebase.firestore.SetOptions.merge())
                    .await()

                firestore.collection("events")
                    .document(expense.eventId)
                    .collection("rooms")
                    .document("Budget")
                    .collection("expenses")
                    .document(expense.id)
                    .set(expense)
                    .await()
                
                // Mark as synced in local DB
                budgetDao.insertExpense(expense.copy(isSynced = true))
            } catch (e: Exception) {
                android.util.Log.e("BudgetRepo", "Error syncing expense: ${e.message}")
            }
        }
    }

    override suspend fun deleteExpense(expenseId: String, eventId: String) {
        // 1. Update Room Instantly
        budgetDao.deleteExpenseById(expenseId)

        // 2. Trigger Background Sync
        externalScope.launch {
            try {
                firestore.collection("events")
                    .document(eventId)
                    .collection("rooms")
                    .document("Budget")
                    .collection("expenses")
                    .document(expenseId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override suspend fun updateBudget(totalBudget: Double, eventId: String) {
        android.util.Log.d("BudgetRepo", "Updating budget for event $eventId to $totalBudget")
        val budgetSettings = BudgetEntity(eventId = eventId, totalBudget = totalBudget)
        budgetDao.updateBudgetSettings(budgetSettings)

        externalScope.launch {
            try {
                // Update specific budget document in the room
                firestore.collection("events")
                    .document(eventId)
                    .collection("rooms")
                    .document("Budget")
                    .set(mapOf("totalBudget" to totalBudget), com.google.firebase.firestore.SetOptions.merge())
                    .await()
                android.util.Log.d("BudgetRepo", "Successfully updated events/$eventId/rooms/Budget")

                // Sync with the event document's budget field
                firestore.collection("events")
                    .document(eventId)
                    .update("budget", totalBudget)
                    .await()
                android.util.Log.d("BudgetRepo", "Successfully updated events/$eventId")

            } catch (e: Exception) {
                android.util.Log.e("BudgetRepo", "Error updating budget in Firestore: ${e.message}", e)
            }
        }
    }

    override suspend fun renameCategory(oldName: String, newName: String, eventId: String) {
        budgetDao.renameCategory(oldName, newName, eventId)
        // Cloud sync for category rename would require a batch update in Firestore
    }

    override suspend fun deleteExpensesByCategory(category: String, eventId: String) {
        budgetDao.deleteExpensesByCategory(category, eventId)
        // Cloud sync for delete by category would require a batch delete in Firestore
    }

    override suspend fun syncWithCloud() {
        // Implementation for pulling data from Firestore if needed
    }
}
