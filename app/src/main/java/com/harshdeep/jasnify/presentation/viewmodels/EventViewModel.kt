package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.data.models.EventData
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.SubEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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
) : ViewModel() {

    // State for managing the event creation process
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
                _userEvents.value = events
                if (events.isNotEmpty()) {
                    _activeEvent.value = events.first()
                }
            }
    }

    /**
     * Saves the event data to Cloud Firestore.
     */
    fun saveEventData(eventData: EventData) {
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
            isMultiDay = eventData.isMultiDay ?: false,
            date = eventData.singleDayDate,
            budget = eventData.budget.toDoubleOrNull() ?: 0.0,
            subEvents = eventData.subEvents.map { 
                SubEvent(
                    id = it.id,
                    name = it.name,
                    date = it.date,
                    isCompleted = false
                )
            }
        )

        firestore.collection("events")
            .document(event.id)
            .set(event)
            .addOnSuccessListener {
                _eventState.value = EventCreationState.Success("'${event.name}' event created!")
            }
            .addOnFailureListener { e ->
                _eventState.value = EventCreationState.Error(e.message ?: "Failed to save event.")
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
