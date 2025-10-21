package com.harshdeep.jasnify.presentation.screens.onboarding
enum class EventCreationStep(val stepNumber: Int, val title: String) {
    EVENT_TYPE(1, "Create a new event"),
    EVENT_NAME(2, "Create a new event"),
    EVENT_DAYS(3, "Create a new event"),
    EVENT_DATE(4, "Create a new event"), // Used for single-day event date selection
    EVENT_TIMELINE(4, "Create a new event"), // Used for multi-day event timeline
    EVENT_BUDGET(5, "Create a new event");

    companion object {
        val totalSteps = 5 // Total main steps
    }
}