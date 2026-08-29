package com.harshdeep.jasnify.presentation.screens.catering

import androidx.compose.runtime.Immutable
import com.harshdeep.jasnify.presentation.components.chip.Dietary

@Immutable
data class MenuItem(
    val id: String,
    val name: String,
    val dietary: Dietary,
    val type: String,
    val cuisine: String = "Indian"
)

enum class CateringMenuView {
    MENU,
    MANAGE_ROOM_ACCESS,
    VENDOR_CATEGORY_DETAIL,
    VENDOR_DETAIL,
    LOCATION_SELECTOR,
    TIMELINE_DETAIL,
    GROUP_CHAT,
    HELP_FEEDBACK
}
