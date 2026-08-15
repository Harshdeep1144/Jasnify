package com.harshdeep.jasnify.utils

import java.util.concurrent.TimeUnit

object TimeUtils {
    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 60 -> "just now"
            minutes == 1L -> "1 min ago"
            minutes < 60 -> "$minutes mins ago"
            hours == 1L -> "1 hr ago"
            hours < 24 -> "$hours hrs ago"
            days == 1L -> "yesterday"
            days < 7 -> "$days d ago"
            weeks == 1L -> "1 wk ago"
            weeks < 4 -> "$weeks wks ago"
            months == 1L -> "1 mo ago"
            months < 12 -> "$months mos ago"
            years == 1L -> "1 yr ago"
            else -> "$years yrs ago"
        }
    }
}
