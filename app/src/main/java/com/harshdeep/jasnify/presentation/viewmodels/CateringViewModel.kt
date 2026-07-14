package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.data.local.CateringItemEntity
import com.harshdeep.jasnify.domain.repository.CateringRepository
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CateringViewModel @Inject constructor(
    private val repository: CateringRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val cateringItems: StateFlow<List<CateringItemEntity>> = repository.getAllCateringItems()
        .onEach { _isLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addItem(name: String, dietary: Dietary, type: String, cuisine: String) {
        viewModelScope.launch {
            val item = CateringItemEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                dietary = dietary,
                type = type,
                cuisine = cuisine
            )
            repository.addItem(item)
        }
    }

    fun updateItem(id: String, name: String, dietary: Dietary, type: String, cuisine: String) {
        viewModelScope.launch {
            val item = CateringItemEntity(
                id = id,
                name = name,
                dietary = dietary,
                type = type,
                cuisine = cuisine
            )
            repository.addItem(item)
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }

    fun seedDefaultMenu(eventType: String) {
        viewModelScope.launch {
            repository.seedDefaultItems(eventType)
        }
    }
}
