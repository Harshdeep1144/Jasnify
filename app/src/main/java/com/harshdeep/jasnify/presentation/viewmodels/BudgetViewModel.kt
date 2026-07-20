package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.data.local.BudgetEntity
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _eventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val expenses: StateFlow<List<ExpenseEntity>> = _eventId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getAllExpenses(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val budgetSettings: StateFlow<BudgetEntity?> = _eventId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getBudgetSettings(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setEventId(id: String) {
        _eventId.value = id
    }

    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        emoji: String,
        userName: String,
        phoneNumber: String?,
        note: String?
    ) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            val expense = ExpenseEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                title = title,
                category = category,
                amount = amount,
                emoji = emoji,
                lastUpdatedBy = userName,
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
        userName: String,
        phoneNumber: String?,
        note: String?
    ) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            val expense = ExpenseEntity(
                id = id,
                eventId = eventId,
                title = title,
                category = category,
                amount = amount,
                emoji = emoji,
                lastUpdatedBy = userName,
                lastUpdatedDate = System.currentTimeMillis(),
                phoneNumber = phoneNumber,
                note = note,
                isSynced = false
            )
            repository.addExpense(expense)
        }
    }

    fun deleteExpense(expenseId: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.deleteExpense(expenseId, eventId)
        }
    }

    fun updateBudget(totalBudget: Double?) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.updateBudget(totalBudget, eventId)
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.renameCategory(oldName, newName, eventId)
        }
    }

    fun deleteExpensesByCategory(category: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.deleteExpensesByCategory(category, eventId)
        }
    }
}
