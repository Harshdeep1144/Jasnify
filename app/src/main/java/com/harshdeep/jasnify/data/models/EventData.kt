package com.harshdeep.jasnify.data.models

// Data class to hold all event creation data
data class EventData(
    val selectedEventTypeId: Int? = null,
    val eventName: String = "",
    val isMultiDay: Boolean? = null, // null: not selected, true: multi-day, false: single-day
    val singleDayDateString: String? = null,
    val subEvents: List<SubEventItem> = listOf(SubEventItem(id = "1", date = "", name = "", isExisting = false, isEditing = true)),
    val budget: String = ""
)
