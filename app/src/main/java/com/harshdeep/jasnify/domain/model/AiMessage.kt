package com.harshdeep.jasnify.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class AiMessage(
    var id: String = "",
    var text: String = "",
    @get:PropertyName("isUser")
    @set:PropertyName("isUser")
    var isUser: Boolean = false,
    var timestamp: Long = 0,
    var venueIds: List<String> = emptyList(),
    var vendorIds: List<String> = emptyList(),
    var guestIds: List<String> = emptyList(),
    var expenseIds: List<String> = emptyList(),
    var checklistIds: List<String> = emptyList(),
    @get:PropertyName("showBudgetSummary")
    @set:PropertyName("showBudgetSummary")
    var showBudgetSummary: Boolean = false,
    @get:PropertyName("feedback")
    @set:PropertyName("feedback")
    var feedback: Int = 0
)
