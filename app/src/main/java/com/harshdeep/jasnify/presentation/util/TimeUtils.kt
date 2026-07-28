package com.harshdeep.jasnify.presentation.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {

    fun formatLastActive(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30

        return when {
            seconds < 60 -> "Active now"
            minutes < 60 -> "Active $minutes min${if (minutes > 1) "s" else ""} ago"
            hours < 24 -> "Active $hours hour${if (hours > 1) "s" else ""} ago"
            days < 30 -> "Active $days day${if (days > 1) "s" else ""} ago"
            months < 12 -> "Active $months month${if (months > 1) "s" else ""} ago"
            else -> {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                "Active on ${sdf.format(Date(timestamp))}"
            }
        }
    }

    fun formatChatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatPasswordLastChanged(timestamp: Long?): String {
        if (timestamp == null) return "Password never changed"
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = months / 12

        return when {
            seconds < 60 -> "Last changed just now"
            minutes < 60 -> "Last changed $minutes min${if (minutes > 1) "s" else ""} ago"
            hours < 24 -> "Last changed $hours hour${if (hours > 1) "s" else ""} ago"
            days < 30 -> "Last changed $days day${if (days > 1) "s" else ""} ago"
            months < 12 -> "Last changed $months month${if (months > 1) "s" else ""} ago"
            else -> "Last changed $years year${if (years > 1) "s" else ""} ago"
        }
    }
}
