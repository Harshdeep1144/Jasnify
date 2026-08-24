package com.harshdeep.jasnify.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.harshdeep.jasnify.MainActivity
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.notifications.model.NotificationConfig
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(config: NotificationConfig) {
        val channelId = config.channelId
        createNotificationChannel(channelId)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            config.deepLink?.let {
                data = it.toUri()
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle(config.title)
            .setContentText(config.body)
            .setAutoCancel(true)
            .setPriority(getPriority(config.priority))
            .setContentIntent(pendingIntent)
            .setColor("#557373".toColorInt())

        // Override accent color if provided in config
        config.backgroundColor?.let {
            try {
                builder.color = it.toColorInt()
            } catch (e: Exception) { }
        }

        // Handle UI Types for System Notification
        when (config.uiType) {
            "bigText" -> {
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(config.body))
            }
            "bigPicture", "promo" -> {
                config.imageUrl?.let { url ->
                    val bitmap = getBitmapFromUrl(url)
                    if (bitmap != null) {
                        builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(config.body))
                    }
                }
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "App Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPriority(priority: Int): Int {
        return when (priority) {
            0 -> NotificationCompat.PRIORITY_LOW
            2 -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }
}
