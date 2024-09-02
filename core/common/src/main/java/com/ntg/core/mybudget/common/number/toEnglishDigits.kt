package com.ntg.core.mybudget.common.number

fun toEnglishDigits(num: String?): String? {
    if (num.isNullOrEmpty()) {
        return null
    }

    val faDigits = "۰۱۲۳۴۵۶۷۸۹"
    val arDigits = "٠١٢٣٤٥٦٧٨٩"
    var output = ""

    for (char in num) {
        val faIndex = faDigits.indexOf(char)
        if (faIndex >= 0) {
            output += faIndex.toString()
            continue
        }
        val arIndex = arDigits.indexOf(char)
        if (arIndex >= 0) {
            output += arIndex.toString()
            continue
        }
        output += char
    }

    return output.replace(",", "")
}