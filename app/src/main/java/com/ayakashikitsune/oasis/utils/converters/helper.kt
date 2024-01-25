package com.ayakashikitsune.oasis.utils.converters

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val digit_month = mapOf<Int, String>(
    1 to "january",
    2 to "february",
    3 to "march",
    4 to "april",
    5 to "may",
    6 to "june",
    7 to "july",
    8 to "august",
    9 to "september",
    10 to "october",
    11 to "november",
    12 to "december"
)
fun Long.toDate() : String{
    return SimpleDateFormat("yyyy-MM-dd").run {
        format(Date(this@toDate))
    }
}

fun String.toDate(): String{
    return SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).let {
            parser ->
        val dateParsed = parser.parse(this)
        SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(dateParsed)
    }
}