package com.ntg.core.database.convertor

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromStringList(bind: List<String?>?): String? {
        if (bind == null) {
            return null
        }
        val gson = Gson()
        return gson.toJson(bind) // Convert list to JSON
    }

    @TypeConverter
    fun toStringList(bindString: String?): List<String?>? {
        if (bindString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(bindString, type) // Convert JSON back to list
    }
}