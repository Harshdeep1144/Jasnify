package com.harshdeep.jasnify.notifications

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.harshdeep.jasnify.notifications.model.NotificationConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val notificationId = remoteMessage.data["notification_id"]
            if (!notificationId.isNullOrEmpty()) {
                fetchAndShowNotification(notificationId)
            } else {
                val config = NotificationConfig(
                    title = remoteMessage.data["title"] ?: "",
                    body = remoteMessage.data["body"] ?: "",
                    imageUrl = remoteMessage.data["imageUrl"],
                    uiType = remoteMessage.data["uiType"] ?: "bigPicture",
                    deepLink = remoteMessage.data["deepLink"]
                )
                notificationHelper.showNotification(config)
            }
        }
    }

    private fun fetchAndShowNotification(notificationId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val document = firestore.collection("notifications")
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

        val uid = auth.currentUser?.uid
        if (!uid.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    firestore.collection("users").document(uid).update("fcmToken", token).await()
                } catch (e: Exception) {
                    Log.e("FCMService", "Error updating token in Firestore", e)
                }
            }
        }
    }
}