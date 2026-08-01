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

    companion object {
        private const val KEY_NAV_BAR_STYLE = "nav_bar_style"
    }
}
