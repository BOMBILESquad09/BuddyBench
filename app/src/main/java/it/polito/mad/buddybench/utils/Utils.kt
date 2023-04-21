package it.polito.mad.buddybench.utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.MutableLiveData
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class Utils {
    companion object {
        fun capitalize(string: String): String {
            return string.lowercase(Locale.ENGLISH)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }
        }

        fun formatString(str: String): String {
            val words = str.split("_")
            val formattedWords = mutableListOf<String>()

            for (word in words) {
                val formattedWord = capitalize(word)
                formattedWords.add(formattedWord)
            }

            return formattedWords.joinToString(" ")
        }

        fun generateDateRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
            val numOfDays = ChronoUnit.DAYS.between(startDate, endDate)
            return (0..numOfDays).map { startDate.plusDays(it) }
        }

        /**
         * Given two LocalTime values (8:00, 11:00)
         * @return array of time ranges [8:00, 9:00, 10:00, 11:00]
         */
        fun getTimeSlots(start: LocalTime, end: LocalTime): List<LocalTime> {
            val diff = Duration.between(start, end).toHours() // calculate the difference between start and end time in hours
            return (0..diff).map { start.plusHours(it) } // generate a list of hourly time slots using a range operator and the map() function
        }
    }
}