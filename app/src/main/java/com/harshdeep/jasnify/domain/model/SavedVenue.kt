package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class SavedVenue(
    val venueId: String = "",
    val venueName: String = "",
    val eventId: String = "",
    val destination: String = "mysaved", // "mysaved" or subEvent.id
    val timestamp: Long = System.currentTimeMillis()
)
