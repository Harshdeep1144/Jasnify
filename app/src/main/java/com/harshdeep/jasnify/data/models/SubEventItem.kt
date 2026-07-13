package com.harshdeep.jasnify.data.models

import java.util.UUID

/**
 * Professional representation of a sub-event item used in the UI.
 */
data class SubEventItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val date: Long? = null,
    val dateString: String = "",
    val isEditing: Boolean = false,
    val isExisting: Boolean = false,
    val isCompleted: Boolean = false
)
