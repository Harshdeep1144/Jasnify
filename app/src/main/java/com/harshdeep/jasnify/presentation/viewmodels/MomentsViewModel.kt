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

    private val _rootFolders = MutableStateFlow<List<MomentFolder>>(emptyList())
    val rootFolders: StateFlow<List<MomentFolder>> = _rootFolders.asStateFlow()

    private val _allMoments = MutableStateFlow<List<Moment>>(emptyList())
    val allMoments: StateFlow<List<Moment>> = _allMoments.asStateFlow()

    private val _subFolders = MutableStateFlow<List<MomentFolder>>(emptyList())
    val subFolders: StateFlow<List<MomentFolder>> = _subFolders.asStateFlow()

    private val _folderMoments = MutableStateFlow<List<Moment>>(emptyList())
    val folderMoments: StateFlow<List<Moment>> = _folderMoments.asStateFlow()

    // Aliases for compatibility
    val folders: StateFlow<List<MomentFolder>> = _subFolders.asStateFlow()
    val moments: StateFlow<List<Moment>> = _allMoments.asStateFlow()

    private val _savedMoments = MutableStateFlow<List<Moment>>(emptyList())
    val savedMoments: StateFlow<List<Moment>> = _savedMoments.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.VIEWER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _currentUserId = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid ?: "")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uploadProgress = MutableStateFlow<Pair<Int, Int>?>(null)
    val uploadProgress: StateFlow<Pair<Int, Int>?> = _uploadProgress.asStateFlow()

    private var rootFolderJob: Job? = null
    private var rootMomentsJob: Job? = null
    private var subFolderJob: Job? = null
    private var folderMomentsJob: Job? = null
    private var roleCollectionJob: Job? = null
    private var savedMomentsJob: Job? = null

    fun loadRootContent(eventId: String) {
        if (eventId.isBlank()) return
        _currentUserId.value = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (rootFolderJob?.isActive != true) {
            rootFolderJob?.cancel()
            rootFolderJob = viewModelScope.launch {
                try {
                    momentsRepository.initializeRoom(eventId)
                } catch (e: Exception) {
                    android.util.Log.e("MomentsVM", "Error initializing room: ${e.message}")
                }
                momentsRepository.getFolders(eventId, "").collect {
                    _rootFolders.value = it
                }
            }
        }

        if (rootMomentsJob?.isActive != true) {
            rootMomentsJob?.cancel()
            rootMomentsJob = viewModelScope.launch {
                momentsRepository.getMoments(eventId, "all_moments_id").collect {
                    _allMoments.value = it
                }
            }
        }

        if (savedMomentsJob?.isActive != true) {
            savedMomentsJob?.cancel()
            savedMomentsJob = viewModelScope.launch {
                momentsRepository.getSavedMoments(eventId).collect {
                    _savedMoments.value = it
                }
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

    fun loadFolderContent(eventId: String, folderId: String) {
        if (eventId.isBlank()) return
        
        _folderMoments.value = emptyList()
        _subFolders.value = emptyList()

        if (folderId.isEmpty() || folderId == "all_moments_id") {
            _folderMoments.value = _allMoments.value
            return
        }

        subFolderJob?.cancel()
        subFolderJob = viewModelScope.launch {
            momentsRepository.getFolders(eventId, folderId).collect {
                _subFolders.value = it
            }
        }

        folderMomentsJob?.cancel()
        folderMomentsJob = viewModelScope.launch {
            momentsRepository.getMoments(eventId, folderId).collect {
                _folderMoments.value = it
            }
        }
    }

    fun loadFolders(eventId: String, parentId: String = "") {
        if (parentId.isEmpty()) {
            loadRootContent(eventId)
        } else {
            loadFolderContent(eventId, parentId)
        }
    }

    fun loadMoments(eventId: String, folderId: String = "") {
        if (folderId.isEmpty() || folderId == "all_moments_id") {
            loadRootContent(eventId)
        } else {
            loadFolderContent(eventId, folderId)
        }
    }

    fun clearMoments() {
        _folderMoments.value = emptyList()
    }

    fun createFolder(eventId: String, name: String, parentId: String = "") {
        viewModelScope.launch {
            momentsRepository.createFolder(eventId, name, parentId)
            if (parentId.isEmpty()) {
                loadRootContent(eventId)
            } else {
                loadFolderContent(eventId, parentId)
            }
        }
    }

    fun uploadMultipleMoments(eventId: String, folderId: String, urisWithMediaTypes: List<Pair<Uri, Boolean>>) {
        if (eventId.isBlank() || urisWithMediaTypes.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            val total = urisWithMediaTypes.size
            try {
                urisWithMediaTypes.forEachIndexed { index, (uri, isVideo) ->
                    _uploadProgress.value = (index + 1) to total
                    momentsRepository.uploadMoment(eventId, folderId, uri, isVideo)
                }
            } catch (e: Exception) {
                android.util.Log.e("MomentsVM", "Error uploading moments: ${e.message}")
            } finally {
                _uploadProgress.value = null
                _isLoading.value = false
            }
        }
    }

    fun uploadMoment(eventId: String, folderId: String, uri: Uri, isVideo: Boolean) {
        uploadMultipleMoments(eventId, folderId, listOf(uri to isVideo))
    }

    fun deleteMoment(eventId: String, folderId: String, momentId: String) {
        viewModelScope.launch {
            momentsRepository.deleteMoment(eventId, folderId, momentId)
        }
    }

    fun deleteFolder(eventId: String, folderId: String) {
        viewModelScope.launch {
            momentsRepository.deleteFolder(eventId, folderId)
            loadRootContent(eventId)
        }
    }

    fun loadSavedMoments(eventId: String) {
        if (eventId.isBlank()) return
        if (savedMomentsJob?.isActive != true) {
            savedMomentsJob?.cancel()
            savedMomentsJob = viewModelScope.launch {
                momentsRepository.getSavedMoments(eventId).collect {
                    _savedMoments.value = it
                }
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
