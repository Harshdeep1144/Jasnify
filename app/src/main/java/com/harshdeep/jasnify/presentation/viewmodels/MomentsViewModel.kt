package com.harshdeep.jasnify.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.data.local.prefs.PreferenceManager
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.repository.MomentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MomentsViewModel @Inject constructor(
    private val momentsRepository: MomentsRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _folders = MutableStateFlow<List<MomentFolder>>(emptyList())
    val folders: StateFlow<List<MomentFolder>> = _folders.asStateFlow()

    private val _moments = MutableStateFlow<List<Moment>>(emptyList())
    val moments: StateFlow<List<Moment>> = _moments.asStateFlow()

    private val _savedMoments = MutableStateFlow<List<Moment>>(emptyList())
    val savedMoments: StateFlow<List<Moment>> = _savedMoments.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.VIEWER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _currentUserId = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid ?: "")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var folderCollectionJob: Job? = null
    private var momentCollectionJob: Job? = null
    private var roleCollectionJob: Job? = null
    private var currentEventId: String? = null

    private var currentParentId: String = ""

    fun loadFolders(eventId: String, parentId: String = "") {
        _currentUserId.value = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        
        // If eventId and parentId haven't changed and job is active, skip
        if (currentEventId == eventId && currentParentId == parentId && folderCollectionJob?.isActive == true) return
        
        currentEventId = eventId
        currentParentId = parentId
        
        folderCollectionJob?.cancel()
        folderCollectionJob = viewModelScope.launch {
            try {
                momentsRepository.initializeRoom(eventId)
            } catch (e: Exception) {
                android.util.Log.e("MomentsVM", "Error initializing room: ${e.message}")
            }
            momentsRepository.getFolders(eventId, parentId).collect {
                _folders.value = it
            }
        }

        if (roleCollectionJob?.isActive != true) {
            roleCollectionJob?.cancel()
            roleCollectionJob = viewModelScope.launch {
                momentsRepository.getUserRole(eventId).collectLatest {
                    _userRole.value = it
                }
            }
        }
    }

    fun loadMoments(eventId: String, folderId: String = "") {
        momentCollectionJob?.cancel()
        momentCollectionJob = viewModelScope.launch {
            momentsRepository.getMoments(eventId, folderId).collect {
                _moments.value = it
            }
        }
    }

    fun createFolder(eventId: String, name: String, parentId: String = "") {
        viewModelScope.launch {
            momentsRepository.createFolder(eventId, name, parentId)
        }
    }

    fun uploadMoment(eventId: String, folderId: String, uri: Uri, isVideo: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                momentsRepository.uploadMoment(eventId, folderId, uri, isVideo)
            } catch (e: Exception) {
                android.util.Log.e("MomentsVM", "Error uploading moment: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMoment(eventId: String, folderId: String, momentId: String) {
        viewModelScope.launch {
            momentsRepository.deleteMoment(eventId, folderId, momentId)
        }
    }

    fun deleteFolder(eventId: String, folderId: String) {
        viewModelScope.launch {
            momentsRepository.deleteFolder(eventId, folderId)
        }
    }

    fun loadSavedMoments(eventId: String) {
        viewModelScope.launch {
            momentsRepository.getSavedMoments(eventId).collect {
                _savedMoments.value = it
            }
        }
    }

    fun toggleSaveMoment(eventId: String, moment: Moment) {
        viewModelScope.launch {
            momentsRepository.toggleSaveMoment(eventId, moment)
        }
    }

    fun saveDownloadPreference(quality: String, remember: Boolean) {
        if (remember) {
            val rememberUntil = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
            preferenceManager.saveDownloadPreference(quality, rememberUntil)
        }
    }

    fun getDownloadPreference(): String? {
        return preferenceManager.getDownloadPreference()
    }

    fun getSavedDownloadQuality(): String {
        return preferenceManager.getSavedDownloadQuality()
    }
}
