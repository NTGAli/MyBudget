package com.ntg.core.mybudget.common

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun getCountryFromPhoneNumber(context: Context, phone_number: String?): String? {
    if (phone_number == null) return null
    val phone: String? = stripExceptNumbers(phone_number)
    var reader: BufferedReader? = null
    try {
        reader =
            BufferedReader(InputStreamReader(context.resources.assets.open("countries.txt")))
        val lines = reader.readLines().reversed() // Read lines and reverse their order
        for (line in lines) {
            val args = line.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (phone!! == args[0]) {
                return args[1] // Return the full country name instead of the country code
            }
        }
    } catch (e: Exception) {
        Log.d("error", e.toString())
    } finally {
        try {
            reader?.close()
        } catch (e2: Exception) {
            Log.d("error", e2.toString())
        }
    }
    return ""
}


fun getCountryFullNameFromPhoneNumber(context: Context, phone_number: String?): String? {
    if (phone_number == null) return null
    val phone: String? = stripExceptNumbers(phone_number)
    var reader: BufferedReader? = null
    try {
        reader =
            BufferedReader(InputStreamReader(context.resources.assets.open("countries.txt")))
        val lines = reader.readLines().reversed() // Read lines and reverse their order
        for (line in lines) {
            val args = line.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (phone == args[0]) {
                return args[2] // Return the full country name instead of the country code
            }
        }
    } catch (e: Exception) {
        Log.d("error", e.toString())
    } finally {
        try {
            reader?.close()
        } catch (e2: Exception) {
            Log.d("error", e2.toString())
        }
    }
    return ""
}

fun stripExceptNumbers(str: String?): String? {
    return stripExceptNumbers(str, false)
}

fun stripExceptNumbers(str: String?, includePlus: Boolean): String? {
    if (str == null) {
        return null
    }
    val res = StringBuilder(str)
    var phoneChars = "0123456789"
    if (includePlus) {
        phoneChars += "+"
    }
    for (i in res.length - 1 downTo 0) {
        if (!phoneChars.contains(res.substring(i, i + 1))) {
            res.deleteCharAt(i)
        }
    }
    return res.toString()
}


fun getCountryPattern(context: Context, code: String): String {
    var countryName = ""
    try {
        val inputStream = context.assets.open("countries.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val parts = line?.split(";")
            if (parts?.getOrNull(0) == code) {
                countryName = try {
                    parts[3]
                } catch (e: Exception) {
                    ""
                }
                break
            }
        }
        reader.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return countryName.replace("X", "0")
}


fun getCountryName(context: Context, code: String): String {
    var countryName = "---"
    try {
        val inputStream = context.assets.open("countries.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val parts = line?.split(";")
            if (parts?.getOrNull(0) == code) {
                countryName = parts[2]
                break
            }
        }
        reader.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return countryName
}
