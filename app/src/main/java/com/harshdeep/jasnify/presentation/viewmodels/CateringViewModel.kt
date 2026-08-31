package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.data.local.CateringItemEntity
import com.harshdeep.jasnify.domain.repository.CateringRepository
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CateringViewModel @Inject constructor(
    private val repository: CateringRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _isSeeding = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = combine(_isLoading, _isSeeding) { loading, seeding ->
        loading || seeding
    }.stateIn(viewModelScope, SharingStarted.Lazily, true)

    private val _eventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val cateringItems: StateFlow<List<CateringItemEntity>> = _eventId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null) // Emit null to indicate "no event context yet"
            } else {
                _isLoading.value = true // Reset loading when eventId changes
                repository.getCateringItems(id).map { it as List<CateringItemEntity>? }
            }
        }
        .onEach { items ->
            if (items != null && items.isNotEmpty()) {
                _isLoading.value = false 
            }
        }
        .map { it ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setEventId(id: String) {
        if (_eventId.value != id) {
            _eventId.value = id
        }
    }

    fun addItem(name: String, dietary: Dietary, type: String, cuisine: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            val item = CateringItemEntity(
                id = UUID.randomUUID().toString(),
                eventId = eventId,
                name = name,
                dietary = dietary,
                type = type,
                cuisine = cuisine
            )
            repository.addItem(item)
        }
    }

    fun updateItem(id: String, name: String, dietary: Dietary, type: String, cuisine: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            val item = CateringItemEntity(
                id = id,
                eventId = eventId,
                name = name,
                dietary = dietary,
                type = type,
                cuisine = cuisine
            )
            repository.addItem(item)
        }
    }

    fun deleteItem(id: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.deleteItem(id, eventId)
        }
    }

    fun seedDefaultMenu(eventType: String, eventId: String) {
        setEventId(eventId)
        viewModelScope.launch {
            _isSeeding.value = true
            try {
                repository.seedDefaultItems(eventType, eventId)
            } finally {
                _isSeeding.value = false
                _isLoading.value = false 
            }
        }
    }

    suspend fun fetchDishImages(query: String): List<String> {
        return repository.searchDishImages(query).getOrElse { emptyList() }
    }
}
