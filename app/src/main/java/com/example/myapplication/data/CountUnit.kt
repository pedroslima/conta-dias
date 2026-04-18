package com.example.myapplication.data

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class CountUnit(
    val labelPt: String,
    val singularPt: String,
    val pluralPt: String,
) {
    SECONDS("segundos", "segundo", "segundos"),
    MINUTES("minutos", "minuto", "minutos"),
    HOURS("horas", "hora", "horas"),
    DAYS("dias", "dia", "dias"),
    WEEKS("semanas", "semana", "semanas"),
    MONTHS("meses", "mês", "meses"),
    YEARS("anos", "ano", "anos");

    fun next(): CountUnit = entries[(ordinal + 1) % entries.size]
    fun prev(): CountUnit = entries[(ordinal - 1 + entries.size) % entries.size]
}

fun diffIn(fromMillis: Long, toMillis: Long, unit: CountUnit): Long {
    val ms = kotlin.math.abs(toMillis - fromMillis)
    return when (unit) {
        CountUnit.SECONDS -> ms / 1_000L
        CountUnit.MINUTES -> ms / 60_000L
        CountUnit.HOURS -> ms / 3_600_000L
        CountUnit.DAYS -> ms / 86_400_000L
        CountUnit.WEEKS -> ms / (86_400_000L * 7)
        CountUnit.MONTHS -> {
            val c1 = Calendar.getInstance().apply { timeInMillis = minOf(fromMillis, toMillis) }
            val c2 = Calendar.getInstance().apply { timeInMillis = maxOf(fromMillis, toMillis) }
            var m = (c2[Calendar.YEAR] - c1[Calendar.YEAR]) * 12 + (c2[Calendar.MONTH] - c1[Calendar.MONTH])
            if (c2[Calendar.DAY_OF_MONTH] < c1[Calendar.DAY_OF_MONTH]) m--
            maxOf(0, m).toLong()
        }
        CountUnit.YEARS -> {
            val c1 = Calendar.getInstance().apply { timeInMillis = minOf(fromMillis, toMillis) }
            val c2 = Calendar.getInstance().apply { timeInMillis = maxOf(fromMillis, toMillis) }
            var y = c2[Calendar.YEAR] - c1[Calendar.YEAR]
            val m1 = c1[Calendar.MONTH] * 31 + c1[Calendar.DAY_OF_MONTH]
            val m2 = c2[Calendar.MONTH] * 31 + c2[Calendar.DAY_OF_MONTH]
            if (m2 < m1) y--
            maxOf(0, y).toLong()
        }
    }
}

fun bestUnit(fromMillis: Long, toMillis: Long): CountUnit {
    if (diffIn(fromMillis, toMillis, CountUnit.YEARS) >= 2) return CountUnit.YEARS
    if (diffIn(fromMillis, toMillis, CountUnit.MONTHS) >= 2) return CountUnit.MONTHS
    if (diffIn(fromMillis, toMillis, CountUnit.WEEKS) >= 3) return CountUnit.WEEKS
    if (diffIn(fromMillis, toMillis, CountUnit.DAYS) >= 2) return CountUnit.DAYS
    if (diffIn(fromMillis, toMillis, CountUnit.HOURS) >= 2) return CountUnit.HOURS
    if (diffIn(fromMillis, toMillis, CountUnit.MINUTES) >= 2) return CountUnit.MINUTES
    return CountUnit.SECONDS
}

fun isFuture(dateMillis: Long): Boolean = dateMillis > System.currentTimeMillis()

fun directionWord(dateMillis: Long, n: Long): String = when {
    isFuture(dateMillis) && n == 1L -> "Falta"
    isFuture(dateMillis) -> "Faltam"
    else -> "Faz"
}

fun unitLabel(unit: CountUnit, n: Long): String =
    if (n == 1L) unit.singularPt else unit.pluralPt

fun fmtNumber(n: Long): String =
    NumberFormat.getNumberInstance(Locale("pt", "BR")).format(n)

fun fmtDateLong(millis: Long): String =
    SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")).format(Date(millis))

fun fmtDateShort(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(millis))

fun fmtTime(millis: Long): String =
    SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(Date(millis))

fun fmtDateWithTime(millis: Long): String {
    val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy · HH:mm", Locale("pt", "BR"))
    return sdf.format(Date(millis)).replaceFirstChar { it.uppercase() }
}
