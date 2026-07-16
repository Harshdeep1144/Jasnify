package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _roomUsers = MutableStateFlow<List<User>>(emptyList())
    val roomUsers: StateFlow<List<User>> = _roomUsers.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()

    fun loadRoomUsers(eventId: String, roomType: String) {
        viewModelScope.launch {
            userRepository.getRoomUsers(eventId, roomType).collectLatest { users ->
                _roomUsers.value = users
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                if (query.isNotEmpty()) {
                    _searchResults.value = userRepository.searchUsers(query)
                } else {
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun grantAccess(eventId: String, roomType: String, email: String, role: UserRole) {
        viewModelScope.launch {
            try {
                userRepository.grantRoomAccess(eventId, roomType, email, role)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun removeAccess(eventId: String, roomType: String, uid: String) {
        viewModelScope.launch {
            try {
                userRepository.removeRoomAccess(eventId, roomType, uid)
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
