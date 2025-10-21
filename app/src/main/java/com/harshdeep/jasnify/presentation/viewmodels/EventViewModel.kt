package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.harshdeep.jasnify.data.models.EventData
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
    private val database: FirebaseDatabase,
) : ViewModel() {

    // State for managing the event creation process
    private val _eventState = MutableStateFlow<EventCreationState>(EventCreationState.Idle)
    val eventState: StateFlow<EventCreationState> = _eventState

    private val _hasEventsState = MutableStateFlow<Boolean?>(null)
    val hasEventsState: StateFlow<Boolean?> = _hasEventsState.asStateFlow()

    fun resetEventState() {
        _eventState.value = EventCreationState.Idle
    }

    /**
     * Saves the event data to Firebase Realtime Database.
     */
    fun saveEventData(eventData: EventData) {
        val user = auth.currentUser
        if (user == null) {
            _eventState.value = EventCreationState.Error("User not logged in. Cannot save event.")
            return
        }

        _eventState.value = EventCreationState.Loading

        val userId = user.uid
        val eventsRef = database.getReference("users").child(userId).child("events")

        // Push a new, unique child node to store the event data
        eventsRef.push().setValue(eventData)
            .addOnSuccessListener {
                _eventState.value = EventCreationState.Success("Event '${eventData.eventName}' created and saved successfully!")
            }
            .addOnFailureListener { e ->
                _eventState.value = EventCreationState.Error(e.message ?: "Failed to save event data.")
            }
    }


    /**
     * Checks Firebase Realtime Database to determine if the current user has any events.
     */
    suspend fun checkIfUserHasEventsInDatabase(): Boolean {
        val user = auth.currentUser
        if (user == null) {
            return false
        }

        val userId = user.uid
        val eventsRef = database.getReference("users").child(userId).child("events")

        return try {
            val dataSnapshot = eventsRef.limitToFirst(1).get().await()
            val hasEvents = dataSnapshot.exists() && dataSnapshot.hasChildren()
            _hasEventsState.value = hasEvents
            hasEvents
        } catch (e: Exception) {
            _hasEventsState.value = false
            false
        }
    }
}
