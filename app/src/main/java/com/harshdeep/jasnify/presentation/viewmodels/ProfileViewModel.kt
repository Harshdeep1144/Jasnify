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
