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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val processingCardLikes = mutableSetOf<String>()

    private val _adminDetails = MutableStateFlow<Map<String, Any>?>(null)
    val adminDetails: StateFlow<Map<String, Any>?> = _adminDetails.asStateFlow()
    val isCardAdmin: StateFlow<Boolean> = _adminDetails.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _recentColors = MutableStateFlow<List<String>>(
        preferenceManager.getRecentColors(PreferenceManager.KEY_RECENT_COLORS_CARD)
    )
    val recentColors: StateFlow<List<String>> = _recentColors.asStateFlow()

    val myCards: StateFlow<List<CardData>> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(emptyList<CardData>())
        else repository.getMyCards(id).map { list -> list.sortedByDescending { it.lastEdited } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedCards: StateFlow<List<CardData>> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(emptyList())
        else repository.getLikedCards(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedStyle = MutableStateFlow<String?>(null)
    val selectedStyle: StateFlow<String?> = _selectedStyle.asStateFlow()

    val jasnifyCards: StateFlow<List<CardData>> = repository.getJasnifyCards()
        .onEach { _isLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableStyles: StateFlow<List<String>> = jasnifyCards.map { cards ->
        cards.map { it.cardStyle }.distinct().filter { it.isNotBlank() }.sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val globalCardThemes: StateFlow<List<CardTheme>> = repository.getGlobalCardThemes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun setSelectedStyle(style: String?) {
        if (_selectedStyle.value == style) {
            _selectedStyle.value = null
        } else {
            _selectedStyle.value = style
        }
    }

    fun addRecentColor(colorHex: String) {
        val current = _recentColors.value.toMutableList()
        current.remove(colorHex)
        current.add(0, colorHex)
        val limited = current.take(12)
        _recentColors.value = limited
        preferenceManager.saveRecentColors(PreferenceManager.KEY_RECENT_COLORS_CARD, limited)
    }

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

    fun deleteJasnifyCard(cardId: String) {
        viewModelScope.launch {
            repository.deleteJasnifyCard(cardId)
        }
    }

    fun toggleLikedCard(data: CardData, isJasnifyCard: Boolean = false) {
        val eventId = _eventId.value ?: return
        val userId = auth.currentUser?.uid ?: return
        
        if (processingCardLikes.contains(data.id)) return
        processingCardLikes.add(data.id)

        viewModelScope.launch {
            try {
                val isAlreadyLiked = data.likedBy.contains(userId)
                
                // Prepare updated card object for personal collection
                val updatedLikedBy = if (isAlreadyLiked) data.likedBy - userId else data.likedBy + userId
                val updatedLikesCount = if (isAlreadyLiked) (data.likesCount - 1).coerceAtLeast(0) else data.likesCount + 1
                val updatedData = data.copy(likedBy = updatedLikedBy, likesCount = updatedLikesCount)

                // 1. Toggle in personal event room
                repository.toggleLikedCard(eventId, updatedData)
                
                // 2. Toggle in global collection if it's a Jasnify template
                if (isJasnifyCard) {
                    repository.toggleJasnifyCardLike(data.id, userId, !isAlreadyLiked)
                }
            } finally {
                delay(400)
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

    fun getCardRoomData(eventId: String): Flow<CardRoomData?> {
        return repository.getCardRoomData(eventId)
    }

    val cardRoomData: StateFlow<CardRoomData?> = _eventId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(null)
        else repository.getCardRoomData(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
