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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardViewModel @Inject constructor(
    private val repository: CardRepository,
    private val cloudinaryManager: CloudinaryManager,
    private val preferenceManager: PreferenceManager,
    private val auth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _eventId = MutableStateFlow<String?>(null)

    private val processingCardLikes = mutableSetOf<String>()

    private val _adminDetails = MutableStateFlow<Map<String, Any>?>(null)
    val isCardAdmin: StateFlow<Boolean> = _adminDetails.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        val uid = auth.currentUser?.uid
        if (!uid.isNullOrEmpty()) {
            viewModelScope.launch {
                repository.checkIsCardsAdmin(uid).collect {
                    _adminDetails.value = it
                }
            }
        }
    }

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

    val jasnifyCards: StateFlow<List<CardData>> = repository.getJasnifyCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val globalCardThemes: StateFlow<List<CardTheme>> = repository.getGlobalCardThemes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun toggleLikedCard(data: CardData, isJasnifyCard: Boolean = false) {
        val eventId = _eventId.value ?: return
        val userId = auth.currentUser?.uid ?: return
        
        if (processingCardLikes.contains(data.id)) return
        processingCardLikes.add(data.id)

        viewModelScope.launch {
            try {
                // 1. Toggle local "liked_cards" collection (your existing logic)
                repository.toggleLikedCard(eventId, data)
                
                if (isJasnifyCard) {
                    val cardInList = jasnifyCards.value.find { it.id == data.id }
                    val isAlreadyLiked = cardInList?.likedBy?.contains(userId) == true

                    if (isAlreadyLiked) {
                        // It's already liked, so this click means the user wants to UNLIKE
                        repository.toggleJasnifyCardLike(data.id, userId, false)
                    } else {
                        // It's not liked, so this click means the user wants to LIKE
                        repository.toggleJasnifyCardLike(data.id, userId, true)
                    }
                }
            } finally {
                delay(500) // Cooldown to allow Firestore listener to update
                processingCardLikes.remove(data.id)
            }
        }
    }

    fun incrementCardShare(card: CardData, isJasnifyCard: Boolean) {
        viewModelScope.launch {
            repository.incrementCardShare(card.id, isJasnifyCard)
        }
    }

    fun saveAsJasnifyCard(data: CardData) {
        viewModelScope.launch {
            val admin = _adminDetails.value
            val cardToSave = data.copy(
                adminName = admin?.get("name") as? String,
                adminUsername = admin?.get("username") as? String
            )
            repository.saveJasnifyCard(cardToSave)
        }
    }

    fun publishCardsToJasnify(cards: List<CardData>, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val admin = _adminDetails.value
            val cardsWithAdmin = cards.map {
                it.copy(
                    adminName = admin?.get("name") as? String,
                    adminUsername = admin?.get("username") as? String
                )
            }
            repository.saveJasnifyCards(cardsWithAdmin)
            onComplete()
        }
    }

    fun uploadThemeImage(uri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            try {
                val url = cloudinaryManager.uploadCardThemeImage(uri, eventId)
                val admin = _adminDetails.value
                val newTheme = CardTheme(
                    name = "",
                    url = url,
                    isDefault = false,
                    adminName = admin?.get("name") as? String,
                    adminUsername = admin?.get("username") as? String
                )
                
                // Save to room's local themes so it appears in the editor selector
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