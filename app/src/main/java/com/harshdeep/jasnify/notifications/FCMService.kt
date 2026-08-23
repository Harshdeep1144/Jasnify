package com.harshdeep.jasnify.notifications

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.harshdeep.jasnify.notifications.model.NotificationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FCMService : FirebaseMessagingService() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val notificationId = remoteMessage.data["notification_id"]
            if (notificationId != null) {
                fetchAndShowNotification(notificationId)
            } else {
                // Fallback: If no notification_id, use payload data directly
                val config = NotificationConfig(
                    title = remoteMessage.data["title"] ?: "",
                    body = remoteMessage.data["body"] ?: "",
                    imageUrl = remoteMessage.data["imageUrl"],
                    uiType = remoteMessage.data["uiType"] ?: "standard"
                )
                notificationHelper.showNotification(config)
            }
        }

        // Also handle standard notification messages (though data messages are preferred for customization)
        remoteMessage.notification?.let {
            val config = NotificationConfig(
                title = it.title ?: "",
                body = it.body ?: ""
            )
            notificationHelper.showNotification(config)
        }
    }

    private fun fetchAndShowNotification(notificationId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val document = db.collection("notifications")
                    .document(notificationId)
                    .get()
                    .await()

                val config = document.toObject(NotificationConfig::class.java)
                if (config != null) {
                    notificationHelper.showNotification(config)
                }
            } catch (e: Exception) {
                Log.e("FCMService", "Error fetching notification config", e)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "Refreshed token: $token")
        // You might want to upload this token to Firestore to target this specific device
    }
}
