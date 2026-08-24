package com.harshdeep.jasnify.presentation.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.harshdeep.jasnify.data.local.prefs.PreferenceManager
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
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
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val cloudinaryManager: CloudinaryManager,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val updateState: StateFlow<ProfileUpdateState> = _updateState.asStateFlow()

    val isNotificationsEnabled: StateFlow<Boolean> = preferenceManager.isNotificationsEnabledFlow

    init {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                userRepository.getUserProfileFlow(uid).collect {
                    _userProfile.value = it
                }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        preferenceManager.setNotificationsEnabled(enabled)
        preferenceManager.setHasUserManuallyToggledNotifications(true)
        
        if (enabled) {
            val isGranted = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
            
            if (isGranted) {
                FirebaseMessaging.getInstance().subscribeToTopic("all")
            }
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
        }
    }

    fun syncNotificationState(systemEnabled: Boolean) {
        val prefEnabled = preferenceManager.isNotificationsEnabled()
        // If system is disabled, we must reflect that
        if (!systemEnabled && prefEnabled) {
            toggleNotifications(false)
        }
    }

    fun fetchProfile() {
        // No longer strictly needed as init starts the flow, but keeping for compatibility if called manually
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _userProfile.value = userRepository.getUserProfile(uid)
        }
    }

    fun updateProfile(name: String, username: String, profileImageUri: Uri? = null, shouldRemovePhoto: Boolean = false) {
        val uid = auth.currentUser?.uid ?: return
        
        // 1. Validation logic
        if (username.isEmpty()) {
            _updateState.value = ProfileUpdateState.Error("Username cannot be empty")
            return
        }
        
        // Rule 4: Can't start with number
        if (username[0].isDigit()) {
            _updateState.value = ProfileUpdateState.Error("Username cannot start with a number")
            return
        }
        
        // Rule 1: Only small letters allowed (no uppercase)
        if (username.any { it.isUpperCase() }) {
            _updateState.value = ProfileUpdateState.Error("Only small letters allowed")
            return
        }

        // Rule 5: No spaces allowed
        if (username.contains(" ")) {
            _updateState.value = ProfileUpdateState.Error("Username cannot contain spaces")
            return
        }
        
        // Rule 2 & 3: Underscore and symbols not sequentially
        for (i in 0 until username.length - 1) {
            val curr = username[i]
            val next = username[i+1]
            val isCurrSymbol = !curr.isLetter() && !curr.isDigit() || curr == '_'
            val isNextSymbol = !next.isLetter() && !next.isDigit() || next == '_'
            
            if (isCurrSymbol && isNextSymbol) {
                _updateState.value = ProfileUpdateState.Error("Symbols/underscores cannot be sequential")
                return
            }
        }

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
                
                if (isUsernameChanging) {
                    if (currentProfile.lastUsernameChangeTimestamp != null && currentProfile.lastUsernameChangeTimestamp > oneMonthAgo) {
                        _updateState.value = ProfileUpdateState.Error("Username can be changed only once a month.")
                        return@launch
                    }
                    
                    if (userRepository.isUsernameTaken(username)) {
                        _updateState.value = ProfileUpdateState.Error("Username already exists!!")
                        return@launch
                    }
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
                _updateState.value = ProfileUpdateState.Success("Profile updated!")
            } catch (e: Exception) {
                _updateState.value = ProfileUpdateState.Error(e.message ?: "Update failed")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = ProfileUpdateState.Idle
    }

    fun submitFeedback(rating: Int, feedback: String) {
        val uid = auth.currentUser?.uid ?: return
        val userName = _userProfile.value?.name ?: "Anonymous"
        
        viewModelScope.launch {
            try {
                userRepository.submitFeedback(uid, userName, rating, feedback)
                _updateState.value = ProfileUpdateState.Success("Feedback submitted. Thank you!")
            } catch (e: Exception) {
                _updateState.value = ProfileUpdateState.Error("Failed to submit feedback")
            }
        }
    }
}
