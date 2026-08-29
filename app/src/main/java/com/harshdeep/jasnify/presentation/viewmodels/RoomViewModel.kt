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

    private val _hasAccess = MutableStateFlow<Boolean?>(null)
    val hasAccess: StateFlow<Boolean?> = _hasAccess.asStateFlow()

    fun loadRoomUsers(eventId: String, roomType: String) {
        if (eventId.isBlank()) return
        viewModelScope.launch {
            userRepository.getRoomUsers(eventId, roomType).collectLatest { users ->
                _roomUsers.value = users
            }
        }
    }

    fun verifyAccess(eventId: String, roomType: String, uid: String) {
        if (eventId.isBlank()) return
        viewModelScope.launch {
            // 1. Try to load from cache first for instant UI response
            val cached = userRepository.getCachedRoomAccess(eventId, roomType, uid)
            if (cached != null) {
                _hasAccess.value = cached
            }

            // 2. Perform network check to ensure access is still valid
            _hasAccess.value = userRepository.checkRoomAccess(eventId, roomType, uid)
        }
    }

    fun resetAccessState() {
        _hasAccess.value = null
    }

    fun setAccessState(hasAccess: Boolean?) {
        _hasAccess.value = hasAccess
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
        if (eventId.isBlank()) return
        viewModelScope.launch {
            try {
                userRepository.grantRoomAccess(eventId, roomType, email, role)
            } catch (e: Exception) {
                android.util.Log.e("RoomViewModel", "Error granting access to $email in $roomType", e)
            }
        }
    }

    fun updateRole(eventId: String, roomType: String, user: User, newRole: UserRole) {
        if (eventId.isBlank()) return
        viewModelScope.launch {
            try {
                userRepository.updateRoomRole(user.uid, eventId, roomType, newRole)
            } catch (e: Exception) {
                android.util.Log.e("RoomViewModel", "Error updating role for ${user.email} in $roomType", e)
            }
        }
    }

    fun removeAccess(eventId: String, roomType: String, uid: String) {
        if (eventId.isBlank()) return
        viewModelScope.launch {
            try {
                userRepository.removeRoomAccess(eventId, roomType, uid)
            } catch (e: Exception) {
                android.util.Log.e("RoomViewModel", "Error removing access for $uid in $roomType", e)
            }
        }
    }
}
