package com.example.movieindex.core.common.extensions

import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun String.toLocalDate(
    input: String,
    format: String,
    locale: Locale = Locale.getDefault(),
): LocalDate {
    /*"yyyy-MM-dd"*/
    val formatter = DateTimeFormatter.ofPattern(format, locale)
    return LocalDate.parse(input, formatter)

}

fun String.dateTimeFormatter(
    inputFormat: String,
    outputFormat: String,
): String {

    val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
    val localDateTime = LocalDateTime.parse(this, inputFormatter)
    val outputFormatter = DateTimeFormatter.ofPattern(outputFormat)

    return localDateTime.format(outputFormatter)
}

fun String.localeDateTimeFormatter(
    inputFormat: String,
    outputFormat: FormatStyle,
    targetLocale: Locale = Locale.getDefault(),
): String {

    val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
    val localDateTime = LocalDateTime.parse(this, inputFormatter)
    val outputFormatter =
        DateTimeFormatter.ofLocalizedDateTime(outputFormat).withLocale(targetLocale)

    return localDateTime.format(outputFormatter)

}

fun String.localeDateFormatter(
    inputFormat: String,
    outputFormat: FormatStyle,
    targetLocale: Locale = Locale.getDefault(),
): String {

    val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
    val localDate = LocalDate.parse(this, inputFormatter)
    val outputFormatter =
        DateTimeFormatter.ofLocalizedDate(outputFormat).withLocale(targetLocale)

    return localDate.format(outputFormatter)

}

fun LocalDateTime.toString(pattern: String, locale: Locale = Locale.getDefault()): String? {
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return this.format(formatter)
}

inline fun <reified T> Gson.fromJson(json: String): T = fromJson<T>(json, T::class.java)
inline fun <reified T> Gson.toJson(data: T): String = toJson(data)