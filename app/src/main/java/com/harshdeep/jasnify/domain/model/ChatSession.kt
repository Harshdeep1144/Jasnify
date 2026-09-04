package com.harshdeep.jasnify.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatSession(
    var id: String = "",
    var title: String = "New Chat",
    var timestamp: Long = System.currentTimeMillis()
)
