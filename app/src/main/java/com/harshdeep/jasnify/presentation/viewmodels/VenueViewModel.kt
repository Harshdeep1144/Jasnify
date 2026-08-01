package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshdeep.jasnify.domain.model.SavedVenue
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.model.VenueReview
import com.harshdeep.jasnify.domain.repository.VenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class VenueViewModel @Inject constructor(
    private val repository: VenueRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            // Simulate check or real check
            delay(5000.milliseconds) // Small delay for shimmer effect visibility
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
        _eventId.value = id
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
