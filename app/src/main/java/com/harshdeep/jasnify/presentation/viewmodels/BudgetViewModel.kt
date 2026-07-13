package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> = repository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgetSettings: StateFlow<BudgetEntity?> = repository.getBudgetSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        emoji: String,
        phoneNumber: String?,
        note: String?
    ) {
        viewModelScope.launch {
            val expense = ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                category = category,
                amount = amount,
                emoji = emoji,
                lastUpdatedBy = "Me", // Replace with actual user name
                lastUpdatedDate = System.currentTimeMillis(),
                phoneNumber = phoneNumber,
                note = note
            )
            repository.addExpense(expense)
        }
    }

    fun updateExpense(
        id: String,
        title: String,
        category: String,
        amount: Double,
        emoji: String,
        phoneNumber: String?,
        note: String?
    ) {
        viewModelScope.launch {
            val expense = ExpenseEntity(
                id = id,
                title = title,
                category = category,
                amount = amount,
                emoji = emoji,
                lastUpdatedBy = "Me",
                lastUpdatedDate = System.currentTimeMillis(),
                phoneNumber = phoneNumber,
                note = note,
                isSynced = false
            )
            repository.addExpense(expense)
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
        }
    }

    fun updateBudget(totalBudget: Double) {
        viewModelScope.launch {
            repository.updateBudget(totalBudget)
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            repository.renameCategory(oldName, newName)
        }
    }

    fun deleteExpensesByCategory(category: String) {
        viewModelScope.launch {
            repository.deleteExpensesByCategory(category)
        }
    }
}
