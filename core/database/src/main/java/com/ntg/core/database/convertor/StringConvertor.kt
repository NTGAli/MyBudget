package com.ntg.core.database.convertor

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.ntg.core.model.Contact
import com.ntg.core.model.SourceType

class Converters {
    private val gson = Gson()


    @TypeConverter
    fun fromStringList(bind: List<String>?): String? {
        if (bind == null) {
            return null
        }
        val gson = Gson()
        return gson.toJson(bind) // Convert list to JSON
    }

    @TypeConverter
    fun toStringList(bindString: String?): List<String>? {
        if (bindString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<String>?>() {}.type
        return gson.fromJson(bindString, type) // Convert JSON back to list
    }

    @TypeConverter
    fun fromContactList(contactList: List<Contact>?): String? {
        return gson.toJson(contactList)
    }

    @TypeConverter
    fun toContactList(contactListString: String?): List<Contact>? {
        return if (contactListString.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Contact>>() {}.type
            gson.fromJson(contactListString, type)
        }
    }

    @TypeConverter
    fun fromSourceType(sourceType: SourceType?): String? {
        return gson.toJson(sourceType)
    }

    @TypeConverter
    fun toSourceType(data: String?): SourceType? {
        if (data == null) return null
        return try {
            // Detect the type of SourceType using Gson
            val jsonObject = gson.fromJson(data, Map::class.java)
            when {
                jsonObject.containsKey("number") -> gson.fromJson(data, SourceType.BankCard::class.java)
                jsonObject.containsKey("value") -> gson.fromJson(data, SourceType.Gold::class.java)
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }
}