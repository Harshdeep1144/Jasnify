package com.harshdeep.jasnify.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harshdeep.jasnify.domain.model.ChecklistItem

class Converters {
    // Converts List<ChecklistItem> → String
    @TypeConverter
    fun fromChecklistItemList(value: List<ChecklistItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ChecklistItem>>() {}.type
        return gson.toJson(value, type)
    }

    // Converts String → List<ChecklistItem>
    @TypeConverter
    fun toChecklistItemList(value: String): List<ChecklistItem> {
        val gson = Gson()
        val type = object : TypeToken<List<ChecklistItem>>() {}.type
        return gson.fromJson(value, type)
    }
}
