package com.harshdeep.jasnify.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.harshdeep.jasnify.MainActivity
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.notifications.model.NotificationConfig
import java.net.HttpURLConnection
import java.net.URL

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(config: NotificationConfig) {
        val channelId = config.channelId.ifBlank { "jasnify_default_channel" }
        createNotificationChannel(channelId)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            config.deepLink?.let {
                data = it.toUri()
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Fetches #006363 directly from colors.xml
        val brandColor = ContextCompat.getColor(context, R.color.notification_default_color)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle(config.title)
            .setContentText(config.body)
            .setAutoCancel(true)
            .setPriority(getPriority(config.priority))
            .setContentIntent(pendingIntent)
            .setColor(brandColor)
            .setColorized(true)

        when (config.uiType) {
            "bigPicture", "promo" -> {
                config.imageUrl?.let { url ->
                    val bitmap = getBitmapFromUrl(url)
                    if (bitmap != null) {
                        builder.setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                                .setBigContentTitle(config.title)
                                .setSummaryText(config.body)
                        )
                        builder.setLargeIcon(bitmap)
                    } else {
                        builder.setStyle(NotificationCompat.BigTextStyle().bigText(config.body))
                    }
                } ?: run {
                    builder.setStyle(NotificationCompat.BigTextStyle().bigText(config.body))
                }
            }
            "bigText" -> {
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(config.body))
            }
            else -> {
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(config.body))
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun sendUserGreeting(name: String, customMessage: String? = null) {
        val greeting = "Hi, ${name.substringBefore(" ").trim()}"
        val body = customMessage ?: "Welcome back to Jasnify! We're glad to see you again."

        val config = NotificationConfig(
            title = greeting,
            body = body,
            channelId = "user_greetings",
            priority = 2
        )
        showNotification(config)
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Jasnify Notifications"
            val descriptionText = "General App Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
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
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()
            BitmapFactory.decodeStream(connection.inputStream)
        } catch (e: Exception) {
            null
        }
    }
}