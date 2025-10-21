package com.harshdeep.jasnify.data.models

import com.harshdeep.jasnify.R

data class EventTypeData(
    val id: Int,
    val label: String,
    val iconResId: Int
)

val eventTypes = listOf(
    EventTypeData(1, "Wedding", R.drawable.img_3d_illustration_12),
    EventTypeData(2, "Birthday Party", R.drawable.img_3d_illustration_11),
    EventTypeData(3, "Engagement",R.drawable.img_3d_illustration_8 ),
    EventTypeData(4, "Family Gathering", R.drawable.img_3d_illustration_7),
    EventTypeData(5, "Reunion", R.drawable.img_3d_illustration_5),
    EventTypeData(6, "Exhibition", R.drawable.img_3d_illustration_9),
    EventTypeData(7, "Seminar", R.drawable.img_3d_illustration_4),
    EventTypeData(8, "Farewell", R.drawable.img_3d_illustration_2),
    EventTypeData(9, "Anniversary", R.drawable.img_3d_illustration_13),
    EventTypeData(10, "Meetup", R.drawable.img_3d_illustration_6),
    EventTypeData(12, "Cultural Event", R.drawable.img_3d_illustration_1),
    EventTypeData(13, "Concert", R.drawable.img_3d_illustration_5),
    EventTypeData(14, "Others", R.drawable.img_3d_illustration_5),
)
