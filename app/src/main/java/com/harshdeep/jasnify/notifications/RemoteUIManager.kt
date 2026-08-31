package com.harshdeep.jasnify.notifications

import com.harshdeep.jasnify.notifications.model.NotificationConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteUIManager @Inject constructor() {
    private val _uiEvents = MutableSharedFlow<NotificationConfig>(extraBufferCapacity = 1)
    val uiEvents = _uiEvents.asSharedFlow()

    fun triggerBottomSheet(config: NotificationConfig) {
        _uiEvents.tryEmit(config)
    }
}
