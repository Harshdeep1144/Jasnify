package com.harshdeep.jasnify.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.repository.GuestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuestViewModel @Inject constructor(
    private val repository: GuestRepository,
    private val cloudinaryManager: CloudinaryManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _eventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val guests: StateFlow<List<Guest>> = _eventId
        .filterNotNull()
        .flatMapLatest { eventId ->
            _isLoading.value = true
            repository.getGuests(eventId)
        }
        .onEach { _isLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setEventId(eventId: String) {
        if (_eventId.value != eventId) {
            _eventId.value = eventId
        }
    }

    fun uploadGuestPhoto(uri: Uri, guestId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            try {
                val url = cloudinaryManager.uploadGuestProfilePicture(uri, eventId, guestId)
                onSuccess(url)
            } catch (e: Exception) {
                onError(e.message ?: "Failed to upload photo")
            }
        }
    }

    fun deleteGuestPhoto(url: String) {
        viewModelScope.launch {
            try {
                cloudinaryManager.deleteImageByUrl(url)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun addGuest(guest: Guest) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.addGuest(eventId, guest)
        }
    }

    fun updateGuest(guest: Guest) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.updateGuest(eventId, guest)
        }
    }

    fun deleteGuest(guestId: String) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.deleteGuest(eventId, guestId)
        }
    }

    fun deleteMultipleGuests(guestIds: List<String>) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.deleteMultipleGuests(eventId, guestIds)
        }
    }
}
