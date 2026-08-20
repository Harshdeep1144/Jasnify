package com.harshdeep.jasnify.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.Moment
import com.harshdeep.jasnify.domain.model.MomentFolder
import com.harshdeep.jasnify.domain.repository.MomentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MomentsViewModel @Inject constructor(
    private val momentsRepository: MomentsRepository
) : ViewModel() {

    private val _folders = MutableStateFlow<List<MomentFolder>>(emptyList())
    val folders: StateFlow<List<MomentFolder>> = _folders.asStateFlow()

    private val _moments = MutableStateFlow<List<Moment>>(emptyList())
    val moments: StateFlow<List<Moment>> = _moments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var folderCollectionJob: Job? = null
    private var momentCollectionJob: Job? = null
    private var currentEventId: String? = null

    fun loadFolders(eventId: String) {
        if (currentEventId == eventId && folderCollectionJob?.isActive == true) return
        currentEventId = eventId
        
        folderCollectionJob?.cancel()
        folderCollectionJob = viewModelScope.launch {
            try {
                momentsRepository.initializeRoom(eventId)
            } catch (e: Exception) {
                android.util.Log.e("MomentsVM", "Error initializing room: ${e.message}")
            }
            momentsRepository.getFolders(eventId).collect {
                _folders.value = it
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

    fun createFolder(eventId: String, name: String) {
        viewModelScope.launch {
            momentsRepository.createFolder(eventId, name)
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
}
