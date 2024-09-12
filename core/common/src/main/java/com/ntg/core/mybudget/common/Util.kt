package com.ntg.core.mybudget.common

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ntg.core.common.R
import com.ntg.core.model.DataBank
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.MessageDigest
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipInputStream

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


fun isValidIranianPhoneNumber(phoneNumber: String): Boolean {
    val iranianPhoneNumberPattern = "^(09|00989|\\+989)\\d{9}$".toRegex()
    return phoneNumber.matches(iranianPhoneNumberPattern)
}

fun String.mask(mask: String): String {
    var maskedString = ""
    var valueIndex = 0
    var maskIndex = 0
    while (valueIndex < length && maskIndex < mask.length) {
        if (mask[maskIndex] == '#') {
            maskedString += get(valueIndex)
            valueIndex++
            maskIndex++
        } else {
            maskedString += mask[maskIndex]
            maskIndex++
        }
    }
    return maskedString
}

//fun detectCardType(cardNumber: String): String {
//    val cleanCardNumber = cardNumber.replace("\\D".toRegex(), "")
//    return Constants.SourceExpenseIcons.MELLI
//    return when {
//        cleanCardNumber.startsWith("4") && (cleanCardNumber.length == 13 || cleanCardNumber.length == 16 || cleanCardNumber.length == 19) -> R.drawable.visa
//        cleanCardNumber.startsWith("5") && cleanCardNumber.length == 16 -> R.drawable.master
//        cleanCardNumber.matches("^3[47][0-9]{13}\$".toRegex()) -> R.drawable.amex
//        cleanCardNumber.matches("^6(?:011|5[0-9]{2})[0-9]{12}\$".toRegex()) -> R.drawable.discover
//        cleanCardNumber.matches("^3(?:0[0-5]|[68][0-9])[0-9]{11}\$".toRegex()) -> R.drawable.diners
//        cleanCardNumber.matches("^35(2[89]|[3-8][0-9])[0-9]{12}\$".toRegex()) -> R.drawable.jcb
//        else -> -1
//    }
//}

fun generateUniqueFiveDigitId(): Int {
    val timestamp = System.currentTimeMillis()
    val input = "$timestamp".toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(input)
    val hash = digest.fold(0) { acc, byte -> (acc shl 8) + byte.toInt() }
    return hash and 0x7FFFFFFF % 90000 + 10000 // Ensures 5-digit ID
}


fun getCardDetailsFromAssets(context: Context, cardNumber: String): DataBank? {
    if (cardNumber.isEmpty()) return null
    val jsonString = context.assets.open("iran_banks.json").bufferedReader().use { it.readText() }

    val listType = object : TypeToken<List<DataBank>>() {}.type
    val bankCardList: List<DataBank> = Gson().fromJson(jsonString, listType)

    for (card in bankCardList) {
        if (cardNumber.startsWith(card.card_no.toString())) {
            return card
        }
    }
    return null
}

fun String.toUnixTimestamp(): Long {
    if (this.isEmpty()) return System.currentTimeMillis()
    val instant = ZonedDateTime.parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant()
    return instant.epochSecond
}