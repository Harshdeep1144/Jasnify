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

    val myCards: StateFlow<List<CardData>> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(emptyList())
        else repository.getMyCards(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedCards: StateFlow<List<CardData>> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(emptyList())
        else repository.getLikedCards(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setEventId(eventId: String) {
        _eventId.value = eventId
    }

    fun saveMyCard(data: CardData) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.saveMyCard(eventId, data)
        }
    }

    fun deleteMyCard(cardId: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.deleteMyCard(eventId, cardId)
        }
    }

    fun toggleLikedCard(data: CardData) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.toggleLikedCard(eventId, data)
        }
    }
}
