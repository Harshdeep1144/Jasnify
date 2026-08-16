package com.harshdeep.jasnify.utils

import android.content.Context
import android.content.SharedPreferences

class SearchHistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val key = "recent_guests_searches"

    fun getRecentSearches(): List<String> {
        val searchString = prefs.getString(key, "") ?: ""
        return if (searchString.isEmpty()) emptyList() else searchString.split("|")
    }

    fun addSearch(query: String) {
        if (query.isBlank()) return
        val current = getRecentSearches().toMutableList()
        current.remove(query)
        current.add(0, query)
        val result = current.take(10).joinToString("|")
        prefs.edit().putString(key, result).apply()
    }

    fun removeSearch(query: String) {
        val current = getRecentSearches().toMutableList()
        current.remove(query)
        val result = current.joinToString("|")
        prefs.edit().putString(key, result).apply()
    }

    fun clearAll() {
        prefs.edit().remove(key).apply()
    }
}
