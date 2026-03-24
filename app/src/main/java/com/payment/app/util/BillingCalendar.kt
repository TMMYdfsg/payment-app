package com.payment.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class BillingDateInfo(
    val requestedDate: LocalDate,
    val scheduledDate: LocalDate,
    val holidayName: String? = null
)

private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
private val monthLabelFormatter = DateTimeFormatter.ofPattern("yyyy年M月", Locale.JAPAN)
private val dateLabelFormatter = DateTimeFormatter.ofPattern("M/d(E)", Locale.JAPAN)

fun currentYearMonth(): YearMonth = YearMonth.now()

fun parseYearMonth(value: String?): YearMonth {
    if (value.isNullOrBlank()) return currentYearMonth()
    return runCatching { YearMonth.parse(value, monthFormatter) }.getOrElse { currentYearMonth() }
}

fun YearMonth.asStorageKey(): String = format(monthFormatter)

fun YearMonth.asDisplayLabel(): String = format(monthLabelFormatter)

fun LocalDate.asDisplayLabel(): String = format(dateLabelFormatter)

fun calculateBillingDate(yearMonth: YearMonth, dueDate: Int): BillingDateInfo {
    val safeDueDate = dueDate.coerceIn(1, yearMonth.lengthOfMonth())
    val requestedDate = yearMonth.atDay(safeDueDate)
    var scheduledDate = requestedDate
    var holidayName: String? = null

    while (scheduledDate.dayOfWeek == DayOfWeek.SATURDAY ||
        scheduledDate.dayOfWeek == DayOfWeek.SUNDAY ||
        getJapaneseHolidayName(scheduledDate) != null
    ) {
        if (holidayName == null) {
            holidayName = getJapaneseHolidayName(scheduledDate)
        }
        scheduledDate = scheduledDate.plusDays(1)
    }

    return BillingDateInfo(
        requestedDate = requestedDate,
        scheduledDate = scheduledDate,
        holidayName = holidayName
    )
}

fun getJapaneseHolidayName(date: LocalDate): String? {
    basicHolidayName(date)?.let { return it }
    if (isSubstituteHoliday(date)) return "振替休日"
    if (isCitizenHoliday(date)) return "国民の休日"
    return null
}

private fun isSubstituteHoliday(date: LocalDate): Boolean {
    if (date < LocalDate.of(1973, 4, 12)) return false
    if (basicHolidayName(date) != null) return false

    var cursor = date.minusDays(1)
    while (basicHolidayName(cursor) != null) {
        if (cursor.dayOfWeek == DayOfWeek.SUNDAY) {
            return true
        }
        cursor = cursor.minusDays(1)
    }
    return false
}

private fun isCitizenHoliday(date: LocalDate): Boolean {
    if (date < LocalDate.of(1985, 12, 27)) return false
    if (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) return false
    if (basicHolidayName(date) != null || isSubstituteHoliday(date)) return false
    return basicHolidayName(date.minusDays(1)) != null && basicHolidayName(date.plusDays(1)) != null
}

private fun basicHolidayName(date: LocalDate): String? {
    val year = date.year
    val month = date.monthValue
    val day = date.dayOfMonth

    return when {
        month == 1 && day == 1 -> "元日"
        month == 1 && date == nthMonday(year, 1, 2) -> "成人の日"
        month == 2 && day == 11 -> "建国記念の日"
        year >= 2020 && month == 2 && day == 23 -> "天皇誕生日"
        month == 3 && day == springEquinoxDay(year) -> "春分の日"
        month == 4 && day == 29 -> "昭和の日"
        month == 5 && day == 3 -> "憲法記念日"
        month == 5 && day == 4 -> "みどりの日"
        month == 5 && day == 5 -> "こどもの日"
        month == 7 && date == nthMonday(year, 7, 3) -> "海の日"
        month == 8 && day == 11 -> "山の日"
        month == 9 && date == nthMonday(year, 9, 3) -> "敬老の日"
        month == 9 && day == autumnEquinoxDay(year) -> "秋分の日"
        month == 10 && date == nthMonday(year, 10, 2) -> "スポーツの日"
        month == 11 && day == 3 -> "文化の日"
        month == 11 && day == 23 -> "勤労感謝の日"
        else -> null
    }
}

private fun nthMonday(year: Int, month: Int, nth: Int): LocalDate {
    var date = LocalDate.of(year, month, 1)
    while (date.dayOfWeek != DayOfWeek.MONDAY) {
        date = date.plusDays(1)
    }
    return date.plusWeeks((nth - 1).toLong())
}

private fun springEquinoxDay(year: Int): Int {
    return when {
        year in 1900..1979 -> (20.8357 + 0.242194 * (year - 1980) - (year - 1983) / 4).toInt()
        year in 1980..2099 -> (20.8431 + 0.242194 * (year - 1980) - (year - 1980) / 4).toInt()
        else -> 20
    }
}

private fun autumnEquinoxDay(year: Int): Int {
    return when {
        year in 1900..1979 -> (23.2588 + 0.242194 * (year - 1980) - (year - 1983) / 4).toInt()
        year in 1980..2099 -> (23.2488 + 0.242194 * (year - 1980) - (year - 1980) / 4).toInt()
        else -> 23
    }
}
