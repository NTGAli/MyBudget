package com.ntg.core.mybudget.common

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ntg.core.model.DataBank
import com.ntg.core.model.Transaction
import com.ntg.core.mybudget.common.persianDate.PersianDate
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Stack
import java.util.TimeZone
import java.util.zip.ZipInputStream

fun Float?.orZero() = this ?: 0f
fun Long?.orDefault() = this ?: 0L
fun Int?.orZero() = this ?: 0
fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true
fun String?.orDefault() = this ?: ""
fun String?.orDefault(default: String): String {
    return if (this.isNullOrEmpty()) default else this
}

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

private val counter = AtomicInteger(10000)

fun generateUniqueFiveDigitId(): Int {
//    val timestamp = System.currentTimeMillis()
//    val input = "$timestamp".toByteArray()
//    val digest = MessageDigest.getInstance("SHA-256").digest(input)
//    val hash = digest.fold(0) { acc, byte -> (acc shl 8) + byte.toInt() }
//    return hash and 0x7FFFFFFF % 90000 + 10000 // Ensures 5-digit ID
    val nextNumber = counter.getAndIncrement()
    if (nextNumber >= 100000) {
        throw IllegalStateException("Unique number limit exceeded")
    }
    return nextNumber
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

fun formatCurrency(amount: Long, mask: String, currency: String, pos: Int): String {
    val decimalFormat = DecimalFormat(mask)
    val formated = decimalFormat.format(amount)
    return when (pos) {
        1 -> "$currency $formated"
        2 -> "$formated $currency"
        else -> formated
    }
}


fun calculateExpression(expression: String): Long? {
    // Clean up the input expression by removing spaces
    val sanitizedExpression = expression.replace(" ", "")

    // Check for empty or invalid input
    if (sanitizedExpression.isEmpty()) {
        println("Error: Expression is empty.")
        return null
    }

    // Validate the expression for allowed characters and operators
    if (!sanitizedExpression.matches(Regex("^[0-9.+\\-*/×÷()]+$"))) {
        println("Error: Expression contains invalid characters.")
        return null
    }

    // Check if the expression ends with an operator
    if (sanitizedExpression.last() in listOf('+', '-', '*', '/', '×', '÷')) {
        println("Error: Expression ends with an operator.")
        return null
    }

    // Replace '×' and '÷' with '*' and '/' to make the expression compatible
    val expressionWithOperators = sanitizedExpression.replace('×', '*').replace('÷', '/')

    return try {
        evaluate(expressionWithOperators).toLong()
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }
}

fun evaluate(expression: String): Double {
    val numberStack = Stack<Double>()
    val operatorStack = Stack<Char>()

    var i = 0
    while (i < expression.length) {
        val ch = expression[i]

        when {
            // If current character is a digit or a dot, handle the number
            ch.isDigit() || ch == '.' -> {
                val num = StringBuilder()
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    num.append(expression[i])
                    i++
                }
                numberStack.push(num.toString().toDouble())
                continue
            }
            // Handle opening parenthesis
            ch == '(' -> {
                operatorStack.push(ch)
            }
            // Handle closing parenthesis
            ch == ')' -> {
                while (operatorStack.isNotEmpty() && operatorStack.peek() != '(') {
                    numberStack.push(performOperation(operatorStack.pop(), numberStack.pop(), numberStack.pop()))
                }
                if (operatorStack.isEmpty() || operatorStack.pop() != '(') {
                    throw IllegalArgumentException("Mismatched parentheses")
                }
            }
            // If it's an operator
            isOperator(ch) -> {
                // Handle unary minus
                if (ch == '-' && (i == 0 || expression[i - 1] in "+-*/(×÷")) {
                    // Treat unary minus as a negative number
                    val num = StringBuilder()
                    num.append('-')
                    i++
                    // Parse the number after the unary minus
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        num.append(expression[i])
                        i++
                    }
                    numberStack.push(num.toString().toDouble())
                    continue
                }

                while (operatorStack.isNotEmpty() && precedence(ch) <= precedence(operatorStack.peek())) {
                    val op = operatorStack.pop()
                    val b = numberStack.pop()
                    val a = numberStack.pop()
                    numberStack.push(performOperation(op, b, a))
                }
                operatorStack.push(ch)
            }
            else -> throw IllegalArgumentException("Invalid character encountered: $ch")
        }
        i++
    }

    // Process remaining operators in the stack
    while (operatorStack.isNotEmpty()) {
        val op = operatorStack.pop()
        if (op == '(' || op == ')') {
            throw IllegalArgumentException("Mismatched parentheses")
        }
        val b = numberStack.pop()
        val a = numberStack.pop()
        numberStack.push(performOperation(op, b, a))
    }

    return numberStack.pop()
}

// Helper function to determine if a character is an operator
fun isOperator(op: Char): Boolean {
    return op in listOf('+', '-', '*', '/', '×', '÷')
}

// Helper function to return precedence of operators
fun precedence(op: Char): Int {
    return when (op) {
        '+', '-' -> 1
        '*', '/' -> 2
        else -> -1
    }
}

// Helper function to perform the operations
fun performOperation(op: Char, b: Double, a: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> {
            if (b == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            a / b
        }
        else -> throw UnsupportedOperationException("Operator $op is not supported")
    }
}

fun formatInput(input: String): String {
    // Regex to match any operator (+, -, ×, ÷)
    val regex = "([+×÷-])".toRegex()

    // Split the input into alternating numbers and operations
    val parts = input.split(regex).filter { it.isNotEmpty() }
    val operations = regex.findAll(input).map { it.value }.toList()

    // Use StringBuilder to append formatted numbers and operations
    val formattedParts = StringBuilder()

    for (i in parts.indices) {
        // Format the number (handle decimals)
        formattedParts.append(formatNumberWithCommasAndDecimal(parts[i].trim()))

        // Append the operator if there's one for this index
        if (i < operations.size) {
            formattedParts.append(operations[i].trim())
        }
    }

    return formattedParts.toString()
}

// Function to format both integers and decimal numbers with commas
private fun formatNumberWithCommasAndDecimal(number: String): String {
    if (number.isEmpty()) return number

    return try {
        // Split by decimal point to handle both parts separately
        val parts = number.split(".")
        val integerPart = parts[0].replace(",", "").toLongOrNull()
        val formattedIntegerPart = integerPart?.let {
            NumberFormat.getNumberInstance(Locale.US).format(it)
        } ?: parts[0]

        // If there's a decimal part, append it without further formatting
        if (parts.size > 1) {
            val decimalPart = parts[1]
            "$formattedIntegerPart.$decimalPart"
        } else {
            formattedIntegerPart
        }
    } catch (e: NumberFormatException) {
        number
    }
}

fun logd(message: String) {
    Log.d("debugLog", message)

}

fun formatTimestamp(timestamp: Long, format: String = "yyyy/MM"): String {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    val date = Date(timestamp)
    return formatter.format(date)
}

fun getCurrentJalaliDate(): Triple<Int, Int, Int> {
    val persianDate = PersianDate()
    val year = persianDate.shYear
    val month = persianDate.shMonth
    val day = persianDate.shDay
    return Triple(year, month, day)
}


fun Long.toPersianDate(setClock: Boolean = false): String {
    val persianDate = PersianDate(Date(this))
    val month = persianDate.monthName
    val dayOfMonth = persianDate.shDay
    val year = persianDate.shYear

    return if (setClock) "$dayOfMonth $month $year - ${persianDate.hour}:${persianDate.minute}"
    else "$dayOfMonth $month $year"
}

fun Long.toPersianMonthlyDate(): String {
    val persianDate = PersianDate(Date(this))
    val month = persianDate.monthName
    val dayOfMonth = persianDate.shDay
    val year = persianDate.shYear

    return "$dayOfMonth $month"
}

fun jalaliToTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
    val persianDate = PersianDate()
    persianDate.initJalaliDate(year, month, day, hour, minute,0)
    return persianDate.time
}

fun convertDateTime(inputDateTime: String): String {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(inputDateTime)

    val persianDate = PersianDate(date)

    val year = persianDate.shYear
    val month = persianDate.shMonth
    val day = persianDate.shDay
    val hour = persianDate.hour
    val minute = persianDate.minute

    return "$year-$month-$day ساعت $hour:$minute"
}

fun formatTimestampToTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("h:mm a", Locale("fa", "IR"))
    return dateFormat.format(Date(timestamp))
}

fun convertToFirstDay(timestamp: Long, granularity: Int, zoneId: ZoneId = ZoneId.systemDefault()): Long {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)

    val adjustedDateTime = when (granularity) {
        Constants.FilterTime.DAY -> dateTime.toLocalDate().atStartOfDay() // First hour of the day
        Constants.FilterTime.MONTH -> dateTime.withDayOfMonth(1).toLocalDate().atStartOfDay() // First day of the month
        Constants.FilterTime.YEAR -> dateTime.withDayOfYear(1).toLocalDate().atStartOfDay() // First day of the year
        else -> throw IllegalArgumentException("Unsupported granularity: $granularity. Use 'day', 'month', or 'year'.")
    }

    return adjustedDateTime.atZone(zoneId).toInstant().toEpochMilli()
}
