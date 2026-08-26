package com.harshdeep.jasnify.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class TimelineEvent(
    val id: String = "",
    val date: String = "",
    val event: String = "",
    val venues: List<Venue> = emptyList()
)
