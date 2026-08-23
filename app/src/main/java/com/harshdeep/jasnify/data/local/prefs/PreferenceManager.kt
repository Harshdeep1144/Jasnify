  package com.harshdeep.jasnify.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.harshdeep.jasnify.presentation.components.bottomdrawer.NavBarStyleOption
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

  @Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("jasnify_prefs", Context.MODE_PRIVATE)

    fun saveNavBarStyle(style: NavBarStyleOption) {
        sharedPreferences.edit { putString(KEY_NAV_BAR_STYLE, style.name) }
    }

    fun getNavBarStyle(): NavBarStyleOption {
        val styleName = sharedPreferences.getString(KEY_NAV_BAR_STYLE, NavBarStyleOption.PILL_SHAPED.name)
        return try {
            NavBarStyleOption.valueOf(styleName ?: NavBarStyleOption.PILL_SHAPED.name)
        } catch (e: Exception) {
            NavBarStyleOption.PILL_SHAPED
        }
    }

    fun saveDownloadPreference(quality: String, rememberUntil: Long) {
        sharedPreferences.edit {
            putString(KEY_DOWNLOAD_QUALITY, quality)
            putLong(KEY_DOWNLOAD_REMEMBER_UNTIL, rememberUntil)
        }
    }

    fun getDownloadPreference(): String? {
        val until = sharedPreferences.getLong(KEY_DOWNLOAD_REMEMBER_UNTIL, 0L)
        if (System.currentTimeMillis() > until) return null
        return sharedPreferences.getString(KEY_DOWNLOAD_QUALITY, null)
    }

    fun getSavedDownloadQuality(): String {
        return sharedPreferences.getString(KEY_DOWNLOAD_QUALITY, "Standard Quality") ?: "Standard Quality"
    }

    fun saveRecentColors(key: String, colors: List<String>) {
        sharedPreferences.edit { putString(key, colors.joinToString(",")) }
    }

    fun getRecentColors(key: String): List<String> {
        val colorsString = sharedPreferences.getString(key, "") ?: ""
        return if (colorsString.isEmpty()) emptyList() else colorsString.split(",")
    }

    companion object {
        private const val KEY_NAV_BAR_STYLE = "nav_bar_style"
        private const val KEY_DOWNLOAD_QUALITY = "download_quality"
        private const val KEY_DOWNLOAD_REMEMBER_UNTIL = "download_remember_until"
        const val KEY_RECENT_COLORS_CHECKLIST = "recent_colors_checklist"
        const val KEY_RECENT_COLORS_CARD = "recent_colors_card"
    }
}
