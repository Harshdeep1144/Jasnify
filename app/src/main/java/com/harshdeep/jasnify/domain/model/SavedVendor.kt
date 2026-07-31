package com.harshdeep.jasnify.domain.model

data class SavedVendor(
    val vendorId: String = "",
    val vendorName: String = "",
    val eventId: String = "",
    val category: String = "",
    val destination: String = "mysaved", // "mysaved" or subEvent.id
    val timestamp: Long = System.currentTimeMillis()
)
