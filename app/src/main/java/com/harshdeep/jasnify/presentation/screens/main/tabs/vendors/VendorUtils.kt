package com.harshdeep.jasnify.presentation.screens.main.tabs.vendors

import android.content.Context
import androidx.core.content.edit

const val PREFS_NAME = "vendor_search_prefs"
const val KEY_RECENT_SEARCHES = "recent_searches"

fun getRecentSearches(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
    return if (raw.isEmpty()) emptyList() else raw.split("|||")
}

fun saveRecentSearch(context: Context, name: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = getRecentSearches(context).toMutableList()
    current.remove(name)
    current.add(0, name)
    val limited = current.take(8)
    prefs.edit { putString(KEY_RECENT_SEARCHES, limited.joinToString("|||")) }
}

fun getCategoryRecentSearches(context: Context, category: String): List<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString("${KEY_RECENT_SEARCHES}_$category", null) ?: return emptyList()
    return if (raw.isEmpty()) emptyList() else raw.split("|||")
}

fun saveCategoryRecentSearch(context: Context, category: String, name: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = getCategoryRecentSearches(context, category).toMutableList()
    current.remove(name)
    current.add(0, name)
    val limited = current.take(8)
    prefs.edit { putString("${KEY_RECENT_SEARCHES}_$category", limited.joinToString("|||")) }
}

fun parsePrice(priceString: String): Int {
    return priceString
        .replace(Regex("[^0-9]"), "")
        .toIntOrNull() ?: 0
}
