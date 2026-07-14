package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.SubEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.domain.repository.CateringRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

import java.util.UUID

data class SubEventItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val date: Long? = null,
    val dateString: String = "",
    val isEditing: Boolean = false,
    val isExisting: Boolean = false,
    val isCompleted: Boolean = false
)

data class EventCreateUiState(
    val selectedEventTypeId: Int? = null,
    val eventName: String = "",
    val isMultiDay: Boolean? = null,
    val singleDayDate: Long? = null,
    val singleDayDateString: String? = null,
    val subEvents: List<SubEventItem> = listOf(SubEventItem(isEditing = true)),
    val budget: String = ""
)

sealed class EventCreationState {
    object Idle : EventCreationState()
    object Loading : EventCreationState()
    data class Success(val message: String) : EventCreationState()
    data class Error(val message: String) : EventCreationState()
}

@HiltViewModel
class EventViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val cateringRepository: CateringRepository
) : ViewModel() {

    private val _eventState = MutableStateFlow<EventCreationState>(EventCreationState.Idle)
    val eventState: StateFlow<EventCreationState> = _eventState

    private val _hasEventsState = MutableStateFlow<Boolean?>(null)
    val hasEventsState: StateFlow<Boolean?> = _hasEventsState.asStateFlow()

    private val _activeEvent = MutableStateFlow<Event?>(null)
    val activeEvent: StateFlow<Event?> = _activeEvent.asStateFlow()

    private val _userEvents = MutableStateFlow<List<Event>>(emptyList())
    val userEvents: StateFlow<List<Event>> = _userEvents.asStateFlow()

    fun resetEventState() {
        _eventState.value = EventCreationState.Idle
    }

    /**
     * Fetches all events for the current user and sets the first one as active.
     */
    fun fetchUserEvents() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        firestore.collection("events")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val events = snapshot?.toObjects(Event::class.java) ?: emptyList()
                val sortedEvents = events.sortedByDescending { it.createdAt }
                _userEvents.value = sortedEvents
                if (sortedEvents.isNotEmpty()) {
                    _activeEvent.value = sortedEvents.first()
                }
            }
    }

    /**
     * Saves the event data to Cloud Firestore.
     */
    fun saveEventData(eventData: EventCreateUiState) {
        val user = auth.currentUser
        if (user == null) {
            _eventState.value = EventCreationState.Error("Login to save event.")
            return
        }

        _eventState.value = EventCreationState.Loading

        val userId = user.uid

        // Mapping to professional Event model
        val event = Event(
            ownerId = userId,
            name = eventData.eventName,
            typeId = eventData.selectedEventTypeId,
            multiDay = eventData.isMultiDay ?: false, // Updated to use renamed multiDay
            date = eventData.singleDayDate,
            budget = eventData.budget.toDoubleOrNull() ?: 0.0,
            subEvents = eventData.subEvents.map {
                SubEvent(
                    id = it.id,
                    name = it.name,
                    date = it.date,
                    completed = false // Updated to use renamed completed
                )
            }
        )

        firestore.collection("events")
            .document(event.id)
            .set(event)
            .addOnSuccessListener {
                viewModelScope.launch {
                    val eventTypeLabel = eventTypes.find { it.id == event.typeId }?.label ?: "Others"
                    cateringRepository.seedDefaultItems(eventTypeLabel, event.id)
                }
                _eventState.value = EventCreationState.Success("'${event.name}' event created!")
            }
            .addOnFailureListener { e ->
                _eventState.value = EventCreationState.Error(e.message ?: "Failed to save event.")
            }
    }

    /**
     * Updates an existing event in Firestore.
     */
    fun updateEvent(event: Event) {
        firestore.collection("events")
            .document(event.id)
            .set(event)
            .addOnSuccessListener {
                // Success - the snapshot listener will pick up the changes
            }
            .addOnFailureListener {
                // Handle failure if needed
            }
    }

    /**
     * Checks Cloud Firestore to determine if the current user has any events.
     */
    suspend fun checkIfUserHasEventsInDatabase(): Boolean {
        val user = auth.currentUser
        if (user == null) {
            return false
        }

        val userId = user.uid

        return try {
            val querySnapshot = firestore.collection("events")
                .whereEqualTo("ownerId", userId)
                .limit(1)
                .get()
                .await()
            val hasEvents = !querySnapshot.isEmpty
            _hasEventsState.value = hasEvents
            hasEvents
        } catch (e: Exception) {
            _hasEventsState.value = false
            false
        }
    }
}