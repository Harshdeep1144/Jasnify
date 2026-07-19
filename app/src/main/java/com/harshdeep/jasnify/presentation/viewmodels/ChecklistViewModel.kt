package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.repository.ChecklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val repository: ChecklistRepository
) : ViewModel() {

    private val _eventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val checklists: StateFlow<List<Checklist>> = _eventId
        .flatMapLatest { id ->
            repository.getAllChecklists(id ?: "")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val archivedChecklists: StateFlow<List<Checklist>> = _eventId
        .flatMapLatest { id ->
            repository.getArchivedChecklists(id ?: "")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setEventId(id: String?) {
        android.util.Log.d("ChecklistVM", "setEventId called with: $id")
        if (_eventId.value != id) {
            android.util.Log.d("ChecklistVM", "Updating _eventId from ${_eventId.value} to $id")
            _eventId.value = id
        }
    }

    fun saveChecklist(checklist: Checklist) {
        val eventId = _eventId.value
        android.util.Log.d("ChecklistVM", "saveChecklist called. Current _eventId in VM: $eventId")
        
        val checklistWithEvent = if (checklist.eventId.isNullOrEmpty()) {
            android.util.Log.d("ChecklistVM", "Attaching eventId '$eventId' to checklist")
            checklist.copy(eventId = eventId)
        } else {
            android.util.Log.d("ChecklistVM", "Checklist already has eventId: ${checklist.eventId}")
            checklist
        }

        viewModelScope.launch {
            repository.saveChecklist(checklistWithEvent)
        }
    }

    fun saveChecklistLocally(checklist: Checklist) {
        viewModelScope.launch {
            repository.saveChecklistLocally(checklist)
        }
    }

    fun deleteChecklist(id: String) {
        viewModelScope.launch {
            repository.deleteChecklist(id)
        }
    }

    fun togglePin(checklist: Checklist, isViewer: Boolean) {
        viewModelScope.launch {
            val updated = checklist.copy(pinned = !checklist.pinned, lastUpdated = System.currentTimeMillis())
            if (isViewer) {
                repository.saveChecklistLocally(updated)
            } else {
                repository.saveChecklist(updated)
            }
        }
    }

    fun toggleArchive(checklist: Checklist, isViewer: Boolean) {
        viewModelScope.launch {
            val updated = checklist.copy(archived = !checklist.archived, pinned = false, lastUpdated = System.currentTimeMillis())
            if (isViewer) {
                repository.saveChecklistLocally(updated)
            } else {
                repository.saveChecklist(updated)
            }
        }
    }
}
