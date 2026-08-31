package com.harshdeep.jasnify.presentation.utils

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

object SessionState {
    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * Tracks if the AI button launch animation should be shown.
     * Initially true, turns false after showing, and resets to true after 1 minute.
     */
    var shouldShowAiButtonLaunchAnimation by mutableStateOf(true)
        private set

    fun markAiButtonAnimationShown() {
        if (!shouldShowAiButtonLaunchAnimation) return
        
        shouldShowAiButtonLaunchAnimation = false
        scope.launch {
            delay(60_000.milliseconds) // 1 minute
            shouldShowAiButtonLaunchAnimation = true
        }
    }

    /**
     * Tracks if the location access bottom sheet has been shown for Venues in the current session.
     */
    var hasShownVenueLocationAccess by mutableStateOf(false)

    /**
     * Tracks if the location access bottom sheet has been shown for Vendors in the current session.
     */
    var hasShownVendorLocationAccess by mutableStateOf(false)

    /**
     * Tracks if the import contacts banner has been shown in the current session.
     */
    var hasShownImportContactsBanner by mutableStateOf(false)

    /**
     * The current resolved location address for the session.
     */
    var currentLocation by mutableStateOf("City, State")
        private set

    fun updateLocation(context: Context, address: String) {
        currentLocation = address
        LocationHelper.saveLastLocation(context, address)
    }

    fun initializeLocation(context: Context) {
        if (currentLocation == "City, State") {
            currentLocation = LocationHelper.getLastLocation(context)
        }
    }
}
