package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    private val _eventId = MutableStateFlow<String?>(null)

    val cardData: StateFlow<CardData?> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(null)
        else repository.getCardData(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setEventId(eventId: String) {
        _eventId.value = eventId
    }

    fun saveCardData(data: CardData) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.saveCardData(eventId, data)
        }
    }
}
