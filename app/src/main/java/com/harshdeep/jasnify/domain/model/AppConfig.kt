package com.harshdeep.jasnify.domain.model

import com.google.firebase.firestore.PropertyName

data class TopSlider(
    val id: String = "",
    val title: String = "",
    var mediaUrl: String = "",
    var actionType: String = "NAVIGATE_ROOM",
    var targetRoute: String = "",
    val order: Int = 0,
    var contentColor: String? = null,
    var autoSlideDuration: Long? = null,
    @get:PropertyName("isActive") @set:PropertyName("isActive") var isActive: Boolean = true
)

data class HomeScreenConfig(
    @get:PropertyName("topSlider") @set:PropertyName("topSlider") var topSlider: List<TopSlider> = emptyList(),
    @get:PropertyName("lastUpdated") @set:PropertyName("lastUpdated") var lastUpdated: Any? = null
)

data class CardScreenConfig(
    var cardBg: List<CardTheme> = emptyList()
)