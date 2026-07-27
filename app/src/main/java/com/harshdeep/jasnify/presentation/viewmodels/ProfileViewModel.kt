package com.harshdeep.jasnify.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    data class Success(val message: String) : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val cloudinaryManager: CloudinaryManager
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val updateState: StateFlow<ProfileUpdateState> = _updateState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _userProfile.value = userRepository.getUserProfile(uid)
        }
    }

    fun updateProfile(name: String, username: String, profileImageUri: Uri? = null, shouldRemovePhoto: Boolean = false) {
        val uid = auth.currentUser?.uid ?: return
        _updateState.value = ProfileUpdateState.Loading

        viewModelScope.launch {
            try {
                val currentProfile = userRepository.getUserProfile(uid)
                if (currentProfile == null) {
                    _updateState.value = ProfileUpdateState.Error("Profile not found")
                    return@launch
                }

                val now = System.currentTimeMillis()
                val oneMonthAgo = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -1)
                }.timeInMillis

                val isUsernameChanging = currentProfile.username != username
                
                if (isUsernameChanging && currentProfile.lastUsernameChangeTimestamp != null && currentProfile.lastUsernameChangeTimestamp > oneMonthAgo) {
                    _updateState.value = ProfileUpdateState.Error("Username can be changed only once a month.")
                    return@launch
                }

                var profileImageUrl = currentProfile.profilePictureUrl
                
                if (shouldRemovePhoto) {
                    cloudinaryManager.deleteProfilePicture(uid)
                    profileImageUrl = null
                } else if (profileImageUri != null) {
                    profileImageUrl = cloudinaryManager.uploadProfilePicture(profileImageUri, uid)
                }

                val updatedProfile = currentProfile.copy(
                    name = name,
                    username = username,
                    profilePictureUrl = profileImageUrl,
                    lastUsernameChangeTimestamp = if (isUsernameChanging) now else currentProfile.lastUsernameChangeTimestamp
                )

                userRepository.updateUserProfile(updatedProfile)
                _userProfile.value = updatedProfile
                _updateState.value = ProfileUpdateState.Success("Profile updated successfully!")
            } catch (e: Exception) {
                _updateState.value = ProfileUpdateState.Error(e.message ?: "Update failed")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = ProfileUpdateState.Idle
    }
}
