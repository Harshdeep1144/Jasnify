package com.harshdeep.jasnify.data.models

import com.harshdeep.jasnify.R

data class EventTypeData(
    val id: Int,
    val label: String,
    val iconResId: Int
)

val eventTypes = listOf(
    EventTypeData(1, "Wedding", R.drawable.ill_event_type_wedding),
    EventTypeData(2, "Birthday Party", R.drawable.ill_event_type_birthday),
    EventTypeData(3, "Engagement",R.drawable.ill_event_type_engagement ),
    EventTypeData(4, "Family Gathering", R.drawable.ill_event_type_familygathering),
    EventTypeData(5, "Reunion", R.drawable.ill_event_type_reunion),
    EventTypeData(6, "Exhibition", R.drawable.ill_event_type_exhibition),
    EventTypeData(7, "Seminar", R.drawable.ill_event_type_seminar),
    EventTypeData(8, "Farewell", R.drawable.ill_event_type_farewell),
    EventTypeData(9, "Anniversary", R.drawable.ill_event_type_anniversary),
    EventTypeData(10, "Meetup", R.drawable.ill_event_type_meetup),
    EventTypeData(12, "Cultural Event", R.drawable.ill_event_type_culturalevent),
    EventTypeData(13, "Concert", R.drawable.ill_event_type_concert),
    EventTypeData(14, "Others", R.drawable.ill_event_type_others),
)
