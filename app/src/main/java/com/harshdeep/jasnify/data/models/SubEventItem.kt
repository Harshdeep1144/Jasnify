package com.harshdeep.jasnify.data.models

import java.util.UUID

data class SubEventItem(
    val id: String = UUID.randomUUID().toString(),
    var date: String = "",
    var name: String = "",
    var isEditing: Boolean = false,
    val isExisting: Boolean = false
)
