package com.harshdeep.jasnify.data.models

import java.time.LocalDate

// Data class to hold all event creation data
data class EventData(
    val selectedEventTypeId: Int? = null,
    val eventName: String = "",
    val isMultiDay: Boolean? = null, // null: not selected, true: multi-day, false: single-day

    // CHANGED: Use a String for persistence in Firebase (e.g., "yyyy-MM-dd")
    val singleDayDateString: String? = null,

    // For multi-day/sub-events, the list will be stored here
    val subEvents: List<SubEventItem> = listOf(SubEventItem(id = "1", date = "", name = "", isExisting = false, isEditing = true)),
    val budget: String = ""
)
