package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.SubEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import com.harshdeep.jasnify.domain.repository.BudgetRepository
import com.harshdeep.jasnify.data.models.eventTypes
import com.harshdeep.jasnify.domain.repository.CateringRepository
import com.harshdeep.jasnify.domain.repository.UserRepository
import com.harshdeep.jasnify.domain.model.UserRole
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FieldPath
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
    private val cateringRepository: CateringRepository,
    private val budgetRepository: BudgetRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("jasnify_event_prefs", Context.MODE_PRIVATE)
    private fun saveActiveEventIdLocally(id: String) {
        prefs.edit().putString("active_event_id", id).apply()
    }
    fun getLocalActiveEventId(): String? = prefs.getString("active_event_id", null)
    fun clearLocalActiveEventId() {
        prefs.edit().remove("active_event_id").apply()
    }

    private val _eventState = MutableStateFlow<EventCreationState>(EventCreationState.Idle)
    val eventState: StateFlow<EventCreationState> = _eventState

    private val _hasEventsState = MutableStateFlow<Boolean?>(null)
    val hasEventsState: StateFlow<Boolean?> = _hasEventsState.asStateFlow()

    private val _activeEvent = MutableStateFlow<Event?>(null)
    val activeEvent: StateFlow<Event?> = _activeEvent.asStateFlow()

    private val _activeEventId = MutableStateFlow<String?>(null)
    val activeEventId: StateFlow<String?> = _activeEventId.asStateFlow()

    private val _userEvents = MutableStateFlow<List<Event>>(emptyList())
    val userEvents: StateFlow<List<Event>> = _userEvents.asStateFlow()

    private var isManuallyJoined = false

    init {
        // Initialize from local cache immediately
        val cachedId = getLocalActiveEventId()
        if (cachedId != null) {
            android.util.Log.d("EventViewModel", "Initializing with cached eventId: $cachedId")
            _activeEventId.value = cachedId
            fetchAndSetActiveEvent(cachedId)
        }
    }

    fun setActiveEvent(event: Event) {
        _activeEvent.value = event
        _activeEventId.value = event.id
        isManuallyJoined = true
        saveActiveEventIdLocally(event.id)
    }

    /**
     * Fetches a specific event by ID and sets it as the active event.
     * This is used when a user joins via an Event ID.
     */
    fun fetchAndSetActiveEvent(eventId: String) {
        _activeEventId.value = eventId
        saveActiveEventIdLocally(eventId)
        viewModelScope.launch {
            val event = getEventById(eventId)
            if (event != null) {
                _activeEvent.value = event
                isManuallyJoined = true
                android.util.Log.d("EventViewModel", "Active event loaded and prioritized: ${event.id}")
                
                // REQUIREMENT: Promote user if they have pending invitations for this event
                val user = auth.currentUser
                if (user != null) {
                    val userEmail = user.email ?: ""
                    android.util.Log.d("EventViewModel", "Triggering promotion for $userEmail in event ${event.id}")
                    userRepository.grantAccessFromPending(event.id, userEmail, user.uid)
                }
            }
        }
    }

    fun clearActiveEvent() {
        _activeEvent.value = null
        _activeEventId.value = null
        isManuallyJoined = false
        clearLocalActiveEventId()
    }

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

                val currentActive = _activeEvent.value
                val latest = sortedEvents.firstOrNull()

                if (latest != null) {
                    // Switch to latest if none active OR if a newer event was created
                    // BUT only if we haven't manually joined another event
                    if (!isManuallyJoined && (currentActive == null || latest.createdAt > currentActive.createdAt)) {
                        _activeEvent.value = latest
                        _activeEventId.value = latest.id
                    } else if (currentActive != null) {
                        // Otherwise just refresh the current active event data if it's in the list
                        val updatedVersion = sortedEvents.find { it.id == currentActive.id }
                        if (updatedVersion != null) {
                            _activeEvent.value = updatedVersion
                            _activeEventId.value = updatedVersion.id
                        }
                    }
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

        val budgetValue = eventData.budget.dropWhile { !it.isDigit() && it != '.' }.toDoubleOrNull()

        // Mapping to professional Event model
        val event = Event(
            ownerId = userId,
            name = eventData.eventName,
            typeId = eventData.selectedEventTypeId,
            multiDay = eventData.isMultiDay ?: false, // Updated to use renamed multiDay
            date = eventData.singleDayDate,
            budget = budgetValue,
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
                _activeEvent.value = event // Set as active immediately
                _activeEventId.value = event.id
                saveActiveEventIdLocally(event.id)
                viewModelScope.launch {
                    val eventTypeLabel = eventTypes.find { it.id == event.typeId }?.label ?: "Others"
                    try {
                        cateringRepository.seedDefaultItems(eventTypeLabel, event.id)
                    } catch (e: Exception) {
                        // Log seeding error but proceed
                    }

                    try {
                        // Also seed budget settings
                        budgetRepository.updateBudget(event.budget, event.id)
                    } catch (e: Exception) {
                        // Log budget error
                    }

                    try {
                        // Grant OWNER access to all rooms for the creator DIRECTLY
                        val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue")
                        val currentUserEmail = auth.currentUser?.email
                        val currentUserId = auth.currentUser?.uid
                        if (currentUserEmail != null && currentUserId != null) {
                            rooms.forEach { room ->
                                userRepository.grantDirectRoomAccess(event.id, room, currentUserEmail, currentUserId, UserRole.OWNER)
                            }
                        }
                        android.util.Log.d("EventViewModel", "Successfully granted owner access to all rooms for ${event.id}")
                    } catch (e: Exception) {
                        android.util.Log.e("EventViewModel", "Failed to grant room access: ${e.message}")
                    }

                    _eventState.value = EventCreationState.Success("'${event.name}' event created!")
                }
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
     * Checks Cloud Firestore for an event matching the provided ID.
     * Supports exact matches (case-insensitive) and prefix matches for shorter IDs.
     * PUBLIC READ REQUIRED in Firestore Rules.
     * IMPORTANT: Always returns the Event with its Firestore Document ID.
     */
    suspend fun getEventById(eventId: String): Event? {
        val inputId = eventId.trim()
        if (inputId.isEmpty()) return null

        val lowercaseId = inputId.lowercase()
        val uppercaseId = inputId.uppercase()

        android.util.Log.d("EventViewModel", "Searching for Event ID: $inputId (variants: $lowercaseId, $uppercaseId)")

        return try {
            // 1. Try exact matches on Document ID (Fastest)
            val docIds = listOf(inputId, lowercaseId, uppercaseId).distinct()
            for (id in docIds) {
                val doc = firestore.collection("events").document(id).get().await()
                if (doc.exists()) {
                    android.util.Log.d("EventViewModel", "Found event by Doc ID: $id")
                    // CRITICAL: Overwrite object id with Document ID to ensure path consistency
                    return doc.toObject(Event::class.java)?.copy(id = doc.id)
                }
            }

            // 2. Try exact matches on internal 'id' field
            val fieldQuery = firestore.collection("events")
                .whereIn("id", docIds)
                .limit(1).get().await()

            if (!fieldQuery.isEmpty) {
                val doc = fieldQuery.documents.first()
                val found = doc.toObject(Event::class.java)
                android.util.Log.d("EventViewModel", "Found event by internal 'id' match: ${found?.id} (Doc ID: ${doc.id})")
                // CRITICAL: Overwrite object id with Document ID
                return found?.copy(id = doc.id)
            }

            // 3. Fallback: Prefix searches (for 8-character codes or shortened UUIDs)
            val prefixVariants = listOf(lowercaseId, uppercaseId).distinct()
            for (prefix in prefixVariants) {
                // Check internal 'id' field prefix
                val pQuery = firestore.collection("events")
                    .whereGreaterThanOrEqualTo("id", prefix)
                    .whereLessThanOrEqualTo("id", prefix + "\uf8ff")
                    .limit(1).get().await()

                if (!pQuery.isEmpty()) {
                    val doc = pQuery.documents.first()
                    val found = doc.toObject(Event::class.java)
                    android.util.Log.d("EventViewModel", "Found event by internal 'id' prefix match: $prefix (Doc ID: ${doc.id})")
                    return found?.copy(id = doc.id)
                }

                // Check Document ID prefix
                val dpQuery = firestore.collection("events")
                    .whereGreaterThanOrEqualTo(FieldPath.documentId(), prefix)
                    .whereLessThanOrEqualTo(FieldPath.documentId(), prefix + "\uf8ff")
                    .limit(1).get().await()

                if (!dpQuery.isEmpty()) {
                    val doc = dpQuery.documents.first()
                    val found = doc.toObject(Event::class.java)
                    android.util.Log.d("EventViewModel", "Found event by Doc ID prefix match: $prefix (Doc ID: ${doc.id})")
                    return found?.copy(id = doc.id)
                }
            }

            android.util.Log.w("EventViewModel", "No event found for ID: $inputId")
            null
        } catch (e: Exception) {
            android.util.Log.e("EventViewModel", "CRITICAL: Event ID Search Failed for $inputId", e)
            null
        }
    }

    suspend fun checkIfUserParticipatesInAnyEvent(): Boolean {
        val user = auth.currentUser ?: return false
        val userId = user.uid
        val userEmail = user.email ?: ""

        return try {
            // 1. Check if user is an OWNER of any event
            val ownedQuery = firestore.collection("events")
                .whereEqualTo("ownerId", userId)
                .limit(1).get().await()

            if (!ownedQuery.isEmpty) {
                _hasEventsState.value = true
                return true
            }

            // 2. Check if user is a member of ANY room (Budget, Catering, etc.)
            // We search for the user's UID in all room-specific user collections
            val rooms = listOf("Budget", "Catering", "Checklist", "Vendors", "Venue")
            for (room in rooms) {
                val collectionName = when (room.lowercase()) {
                    "budget" -> "budget_room_users"
                    "catering" -> "catering_room_users"
                    "checklist" -> "checklist_room_users"
                    "vendors" -> "vendors_room_users"
                    "venue" -> "venue_room_users"
                    else -> "room_users"
                }

                // Using collectionGroup to find the user's membership across all events
                val memberQuery = firestore.collectionGroup(collectionName)
                    .whereEqualTo("uid", userId)
                    .limit(1).get().await()

                if (!memberQuery.isEmpty) {
                    _hasEventsState.value = true
                    return true
                }
            }

            _hasEventsState.value = false
            false
        } catch (e: Exception) {
            android.util.Log.e("EventViewModel", "Participation Check Failed", e)
            _hasEventsState.value = false
            false
        }
    }
}
