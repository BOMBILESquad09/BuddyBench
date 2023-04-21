package it.polito.mad.buddybench.utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
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
    }
}