package com.harshdeep.jasnify.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.*
import com.harshdeep.jasnify.domain.repository.VenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class VenueViewModel @Inject constructor(
    private val repository: VenueRepository,
    private val cloudinaryManager: CloudinaryManager
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isReviewSubmitting = MutableStateFlow(false)
    val isReviewSubmitting: StateFlow<Boolean> = _isReviewSubmitting.asStateFlow()

    fun submitReview(
        venueId: String,
        rating: Double,
        text: String,
        imageUris: List<Uri>,
        removedImageUrls: List<String>,
        likedOptions: List<String>
    ) {
        viewModelScope.launch {
            _isReviewSubmitting.value = true
            try {
                // 0. Delete removed images from Cloudinary
                removedImageUrls.forEach { url ->
                    cloudinaryManager.deleteImageByUrl(url)
                }

                // 1. Upload images to Cloudinary (only those that are not already uploaded)
                val uploadedUrls = imageUris.map { uri ->
                    async {
                        if (uri.toString().contains("cloudinary.com")) {
                            uri.toString()
                        } else {
                            cloudinaryManager.uploadVenueReviewImage(uri, venueId)
                        }
                    }
                }.awaitAll()

                // 2. Create and Submit review
                val user = auth.currentUser
                val review = VenueReview(
                    userId = user?.uid ?: "",
                    userName = user?.displayName ?: "Anonymous",
                    userAvatarUrl = user?.photoUrl?.toString(),
                    rating = rating,
                    reviewText = text,
                    attachedImages = uploadedUrls,
                    likedOptions = likedOptions,
                    createdAt = System.currentTimeMillis(),
                    relativeTime = "Just now"
                )
                repository.addVenueReview(venueId, review)
                
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isReviewSubmitting.value = false
            }
        }
    }

    fun deleteReview(venueId: String, imageUrls: List<String>) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                imageUrls.forEach { url ->
                    cloudinaryManager.deleteImageByUrl(url)
                }
                repository.deleteVenueReview(venueId, userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    init {
        viewModelScope.launch {
            // Simulate check or real check
            _isLoading.value = false
        }
    }

    val allVenues: StateFlow<List<Venue>> = repository.getAllVenues()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVenueId = MutableStateFlow<String?>(null)
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val venueReviews: StateFlow<List<VenueReview>> = _selectedVenueId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getVenueReviews(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedVenueId(id: String?) {
        _selectedVenueId.value = id
    }

    private val _eventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val savedVenues: StateFlow<List<SavedVenue>> = _eventId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getSavedVenues(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setEventId(id: String) {
        if (_eventId.value != id) {
            _eventId.value = id
        }
    }

    fun toggleSaveVenue(venueName: String, venueId: String, isViewer: Boolean, destination: String? = null) {
        val eventId = _eventId.value ?: return
        val currentSaved = savedVenues.value.find { it.venueName == venueName }
        val syncToCloud = !isViewer
        
        viewModelScope.launch {
            if (destination == null && currentSaved != null) {
                repository.removeSavedVenue(eventId, venueName, syncToCloud)
            } else {
                val newSave = SavedVenue(
                    venueId = venueId,
                    venueName = venueName,
                    eventId = eventId,
                    destination = destination ?: "mysaved"
                )
                repository.saveVenue(newSave, syncToCloud)
            }
        }
    }

    fun removeSavedVenue(venueName: String, isViewer: Boolean) {
        val eventId = _eventId.value ?: return
        viewModelScope.launch {
            repository.removeSavedVenue(eventId, venueName, !isViewer)
        }
    }

    fun seedMockData(venues: List<Venue>) {
        viewModelScope.launch {
            repository.seedMockVenues(venues)
        }
    }
}
