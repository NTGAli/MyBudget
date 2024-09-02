package com.ntg.core.mybudget.common.number

fun numToText(input: String?, level: Int = 0): String {
    if (input == null) {
        return ""
    }

    var num = toEnglishDigits(input)?.toLongOrNull() ?: return ""

    if (num < 0) {
        num *= -1
        return "منفی ${numToText(num.toString(), level)}"
    }
    if (num == 0L) {
        return if (level == 0) "صفر" else ""
    }

    val yekan = arrayOf("یک", "دو", "سه", "چهار", "پنج", "شش", "هفت", "هشت", "نه")
    val dahgan = arrayOf("بیست", "سی", "چهل", "پنجاه", "شصت", "هفتاد", "هشتاد", "نود")
    val sadgan = arrayOf("یکصد", "دویست", "سیصد", "چهارصد", "پانصد", "ششصد", "هفتصد", "هشتصد", "نهصد")
    val dah = arrayOf("ده", "یازده", "دوازده", "سیزده", "چهارده", "پانزده", "شانزده", "هفده", "هیجده", "نوزده")

    var result = ""
    if (level > 0) {
        result += " و "
    }

    result += when {
        num < 10 -> yekan[num.toInt() - 1]
        num < 20 -> dah[num.toInt() - 10]
        num < 100 -> dahgan[(num / 10).toInt() - 2] + numToText((num % 10).toString(), level + 1)
        num < 1000 -> sadgan[(num / 100).toInt() - 1] + numToText((num % 100).toString(), level + 1)
        num < 1000000 -> numToText((num / 1000).toString(), level) + " هزار" + numToText((num % 1000).toString(), level + 1)
        num < 1000000000 -> numToText((num / 1000000).toString(), level) + " میلیون" + numToText((num % 1000000).toString(), level + 1)
        num < 1000000000000L -> numToText((num / 1000000000).toString(), level) + " میلیارد" + numToText((num % 1000000000).toString(), level + 1)
        else -> numToText((num / 1000000000000L).toString(), level) + " تریلیارد" + numToText((num % 1000000000000L).toString(), level + 1)
    }

    return result
}

fun numToTextRials(num: String?): String {
    return if (num.isNullOrEmpty()) {
        ""
    } else {
        numToText(num, 0) + " ریال"
    }
}

fun numToTextRialsInTomans(num: String?): String {
    if (num.isNullOrEmpty()) {
        return ""
    }

    var cleanNum = toEnglishDigits(num) ?: return ""
    var amount = cleanNum.toIntOrNull() ?: return ""

    val originalAmount = amount
    if (amount >= 10 || amount <= -10) {
        amount /= 10
    } else {
        amount = 0
    }

    val haveRial = (originalAmount / 10.0).toString().split(".")[1]

    return (if (amount != 0) "${numToText(amount.toString(), 0)} تومان" else "") +
            (if (amount != 0 && haveRial.isNotEmpty()) " و " else "") +
            (if (haveRial.isNotEmpty()) "${numToText(haveRial, 0)} ریال" else "")
}

fun momentApprox(date: String?, baseDate: String? = null, suffixBefore: String = "پیش", suffixAfter: String = "بعد"): String {
    return numToTextMomentApprox(date, baseDate, suffixBefore, suffixAfter, false)
}

fun numToTextMomentApprox(date: String?, baseDate: String? = null, suffixBefore: String = "پیش", suffixAfter: String = "بعد", donumToText: Boolean = true): String {
    if (date.isNullOrEmpty()) {
        return ""
    }

    val base = baseDate?.let { java.util.Date(it) } ?: java.util.Date()
    val currentDate = java.util.Date(date)

    var suffix = suffixBefore
    var diff = base.time - currentDate.time

    if (diff < 0) {
        suffix = suffixAfter
        diff = kotlin.math.abs(diff)
    }

    val diffYears = diff / 31557600000
    if (diffYears > 0) {
        return (if (donumToText) numToText(diffYears.toString()) else diffYears.toString()) + " سال " + suffix
    }

    val diffMonths = diff / 2629800000
    if (diffMonths > 0) {
        return (if (donumToText) numToText(diffMonths.toString()) else diffMonths.toString()) + " ماه " + suffix
    }

    val diffWeeks = diff / 604800000
    if (diffWeeks > 0) {
        return (if (donumToText) numToText(diffWeeks.toString()) else diffWeeks.toString()) + " هفته " + suffix
    }

    val diffDays = diff / 86400000
    if (diffDays > 0) {
        return (if (donumToText) numToText(diffDays.toString()) else diffDays.toString()) + " روز " + suffix
    }

    val diffHours = diff / 3600000
    if (diffHours > 0) {
        return (if (donumToText) numToText(diffHours.toString()) else diffHours.toString()) + " ساعت " + suffix
    }

    val diffMinutes = diff / 60000
    if (diffMinutes > 0) {
        return (if (donumToText) numToText(diffMinutes.toString()) else diffMinutes.toString()) + " دقیقه " + suffix
    }

    val diffSeconds = diff / 1000

    return if (diffSeconds > 0) {
        "چند لحظه " + suffix
    } else {
        "بلافاصله"
    }
}

fun momentPrecise(date: String?, baseDate: String? = null, suffixBefore: String = "پیش", suffixAfter: String = "بعد"): String {
    return numToTextMomentPrecise(date, baseDate, suffixBefore, suffixAfter, false)
}

fun numToTextMomentPrecise(date: String?, baseDate: String? = null, suffixBefore: String = "پیش", suffixAfter: String = "بعد", donumToText: Boolean = true): String {
    if (date.isNullOrEmpty()) {
        return ""
    }

    val base = baseDate?.let { java.util.Date(it) } ?: java.util.Date()
    val currentDate = java.util.Date(date)

    var suffix = suffixBefore
    var diff = base.time - currentDate.time

    if (diff < 0) {
        suffix = suffixAfter
        diff = kotlin.math.abs(diff)
    }

    var result = ""
    val diffYears = diff / 31557600000
    if (diffYears > 0) {
        diff -= diffYears * 31557600000
    }
    val diffMonths = diff / 2629800000
    if (diffMonths > 0) {
        diff -= diffMonths * 2629800000
    }
    val diffWeeks = diff / 604800000
    if (diffWeeks > 0) {
        diff -= diffWeeks * 604800000
    }
    val diffDays = diff / 86400000
    if (diffDays > 0) {
        diff -= diffDays * 86400000
    }
    val diffHours = diff / 3600000
    if (diffHours > 0) {
        diff -= diffHours * 3600000
    }
    val diffMinutes = diff / 60000
    if (diffMinutes > 0) {
        diff -= diffMinutes * 60000
    }
    val diffSeconds = diff / 1000

    if (diffYears > 0) {
        result = (if (donumToText) numToText(diffYears.toString()) else diffYears.toString()) + " سال "
    }

    if (diffMonths > 0) {
        if (result.isNotEmpty()) result += "و "
        result += (if (donumToText) numToText(diffMonths.toString()) else diffMinutes.toString()) + " ماه "
    }

    if (diffWeeks > 0) {
        if (result.isNotEmpty()) result += "و "
        result += (if (donumToText) numToText(diffWeeks.toString()) else diffWeeks.toString()) + " هفته "
    }

    if (diffDays > 0) {
        if (result.isNotEmpty()) result += "و "
        result += (if (donumToText) numToText(diffDays.toString()) else diffDays.toString()) + " روز "
    }

    if (diffHours > 0) {
        if (result.isNotEmpty()) result += "و "
        result += (if (donumToText) numToText(diffHours.toString()) else diffHours.toString()) + " ساعت "
    }

    if (diffMinutes > 0) {
        if (result.isNotEmpty()) result += "و "
        result += (if (donumToText) numToText(diffMinutes.toString()) else diffMinutes.toString()) + " دقیقه "
    }

    if (diffSeconds > 0) {
        if (result.isNotEmpty()) result += "و "
        result += (if (donumToText) numToText(diffSeconds.toString()) else diffSeconds.toString()) + " ثانیه "
    }

    return if (result.isEmpty()) {
        "بلافاصله"
    } else {
        result + suffix
    }
}