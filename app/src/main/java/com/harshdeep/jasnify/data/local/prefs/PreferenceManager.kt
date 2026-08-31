  package com.harshdeep.jasnify.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.harshdeep.jasnify.presentation.components.bottomdrawer.profile.NavBarStyleOption
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    private val _isNotificationsEnabled = MutableStateFlow(isNotificationsEnabled())
    val isNotificationsEnabledFlow: StateFlow<Boolean> = _isNotificationsEnabled.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == KEY_NOTIFICATIONS_ENABLED) {
            _isNotificationsEnabled.value = prefs.getBoolean(key, true)
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled) }
        _isNotificationsEnabled.value = enabled
    }

    fun isNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setHasFirstLoginPermissionAsked(asked: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_FIRST_LOGIN_PERMISSION_ASKED, asked) }
    }

    fun hasFirstLoginPermissionAsked(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LOGIN_PERMISSION_ASKED, false)
    }

    fun setHasCompletedOnboarding(completed: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_HAS_COMPLETED_ONBOARDING, completed) }
    }

    fun hasCompletedOnboarding(): Boolean {
        return sharedPreferences.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)
    }

    fun setHasUserManuallyToggledNotifications(toggled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_HAS_USER_MANUALLY_TOGGLED_NOTIFICATIONS, toggled) }
    }

    fun hasUserManuallyToggledNotifications(): Boolean {
        return sharedPreferences.getBoolean(KEY_HAS_USER_MANUALLY_TOGGLED_NOTIFICATIONS, false)
    }

    fun incrementSessionCount() {
        val count = sharedPreferences.getInt(KEY_SESSION_COUNT, 0)
        sharedPreferences.edit { putInt(KEY_SESSION_COUNT, count + 1) }
    }

    fun getSessionCount(): Int {
        return sharedPreferences.getInt(KEY_SESSION_COUNT, 0)
    }

    fun setLastPermissionRequestSession(session: Int) {
        sharedPreferences.edit { putInt(KEY_LAST_PERMISSION_REQUEST_SESSION, session) }
    }

    fun getLastPermissionRequestSession(): Int {
        return sharedPreferences.getInt(KEY_LAST_PERMISSION_REQUEST_SESSION, -1)
    }

    companion object {
        private const val KEY_NAV_BAR_STYLE = "nav_bar_style"
        private const val KEY_DOWNLOAD_QUALITY = "download_quality"
        private const val KEY_DOWNLOAD_REMEMBER_UNTIL = "download_remember_until"
        const val KEY_RECENT_COLORS_CHECKLIST = "recent_colors_checklist"
        const val KEY_RECENT_COLORS_CARD = "recent_colors_card"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_FIRST_LOGIN_PERMISSION_ASKED = "first_login_permission_asked"
        private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
        private const val KEY_HAS_USER_MANUALLY_TOGGLED_NOTIFICATIONS = "has_user_manually_toggled_notifications"
        private const val KEY_SESSION_COUNT = "session_count"
        private const val KEY_LAST_PERMISSION_REQUEST_SESSION = "last_permission_request_session"
    }
}
