package com.harshdeep.jasnify.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.data.local.prefs.PreferenceManager
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.CardData
import com.harshdeep.jasnify.domain.model.CardRoomData
import com.harshdeep.jasnify.domain.model.CardTheme
import com.harshdeep.jasnify.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardViewModel @Inject constructor(
    private val repository: CardRepository,
    private val cloudinaryManager: CloudinaryManager,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _eventId = MutableStateFlow<String?>(null)

    private val _recentColors = MutableStateFlow<List<String>>(
        preferenceManager.getRecentColors(PreferenceManager.KEY_RECENT_COLORS_CARD)
    )
    val recentColors: StateFlow<List<String>> = _recentColors.asStateFlow()

    fun addRecentColor(colorHex: String) {
        val current = _recentColors.value.toMutableList()
        current.remove(colorHex)
        current.add(0, colorHex)
        val limited = current.take(12)
        _recentColors.value = limited
        preferenceManager.saveRecentColors(PreferenceManager.KEY_RECENT_COLORS_CARD, limited)
    }

    val myCards: StateFlow<List<CardData>> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(emptyList<CardData>())
        else repository.getMyCards(id).map { list -> list.sortedByDescending { it.lastEdited } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedCards: StateFlow<List<CardData>> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(emptyList())
        else repository.getLikedCards(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cardRoomData: StateFlow<CardRoomData?> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(null)
        else repository.getCardRoomData(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setEventId(eventId: String) {
        _eventId.value = eventId
    }

    fun initializeRoom(defaultThemes: List<CardTheme>) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.initializeCardRoom(eventId, defaultThemes)
        }
    }

    fun saveMyCard(data: CardData) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.saveMyCard(eventId, data.copy(lastEdited = System.currentTimeMillis()))
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

    fun uploadThemeImage(uri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            try {
                val url = cloudinaryManager.uploadCardThemeImage(uri, eventId)
                val newTheme = CardTheme(
                    name = "",
                    url = url,
                    isDefault = false
                )
                repository.saveCardTheme(eventId, newTheme)
                onSuccess(url)
            } catch (e: Exception) {
                onError(e.message ?: "Upload failed")
            }
        }
    }

    fun updateThemeName(themeId: String, newName: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.updateCardThemeName(eventId, themeId, newName)
        }
    }
}
