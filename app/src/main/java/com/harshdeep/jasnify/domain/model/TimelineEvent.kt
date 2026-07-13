package com.harshdeep.jasnify.domain.model

data class TimelineEvent(
    val id: String = "",
    val date: String = "",
    val event: String = "",
    val venues: List<Venue> = emptyList()
)
