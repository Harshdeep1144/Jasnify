package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.repository.GuestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuestViewModel @Inject constructor(
    private val repository: GuestRepository
) : ViewModel() {

    private val _guests = MutableStateFlow<List<Guest>>(emptyList())
    val guests: StateFlow<List<Guest>> = _guests.asStateFlow()

    private val _eventId = MutableStateFlow<String?>(null)

    fun setEventId(eventId: String) {
        if (_eventId.value == eventId) return
        _eventId.value = eventId
        viewModelScope.launch {
            repository.getGuests(eventId).collectLatest {
                _guests.value = it
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
