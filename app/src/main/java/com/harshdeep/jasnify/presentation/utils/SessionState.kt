package com.harshdeep.jasnify.presentation.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SessionState {
    /**
     * Tracks if the location access bottom sheet has been shown for Venues in the current session.
     */
    var hasShownVenueLocationAccess by mutableStateOf(false)

    /**
     * Tracks if the location access bottom sheet has been shown for Vendors in the current session.
     */
    var hasShownVendorLocationAccess by mutableStateOf(false)
}
