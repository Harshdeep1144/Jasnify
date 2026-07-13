package com.harshdeep.jasnify.presentation.screens.home.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.repository.ChecklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val repository: ChecklistRepository
) : ViewModel() {

    val checklists: StateFlow<List<Checklist>> = repository.getAllChecklists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val archivedChecklists: StateFlow<List<Checklist>> = repository.getArchivedChecklists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveChecklist(checklist: Checklist) {
        viewModelScope.launch {
            repository.saveChecklist(checklist)
        }
    }

    fun deleteChecklist(id: String) {
        viewModelScope.launch {
            repository.deleteChecklist(id)
        }
    }

    fun togglePin(checklist: Checklist) {
        saveChecklist(checklist.copy(pinned = !checklist.pinned, lastUpdated = System.currentTimeMillis()))
    }

    fun toggleArchive(checklist: Checklist) {
        saveChecklist(checklist.copy(archived = !checklist.archived, pinned = false, lastUpdated = System.currentTimeMillis()))
    }
}
