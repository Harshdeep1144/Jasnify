package com.harshdeep.jasnify.data.models

/**
 * Data class representing the event creation state in the UI.
 * This is refined for a professional production environment.
 */
data class EventData(
    val selectedEventTypeId: Int? = null,
    val eventName: String = "",
    val isMultiDay: Boolean? = null, // null: not selected, true: multi-day, false: single-day
    val singleDayDate: Long? = null,
    val singleDayDateString: String? = null,
    val subEvents: List<SubEventItem> = listOf(SubEventItem(isEditing = true)),
    val budget: String = ""
)
