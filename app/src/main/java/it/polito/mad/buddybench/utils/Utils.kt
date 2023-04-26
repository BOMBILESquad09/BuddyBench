package it.polito.mad.buddybench.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.MutableLiveData
import it.polito.mad.buddybench.dto.CourtTimeDTO
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap
import it.polito.mad.buddybench.activities.myreservations.displayText
import it.polito.mad.buddybench.dto.CourtTimeTableDTO

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
            val diff = Duration.between(start, end)
                .toHours() // calculate the difference between start and end time in hours
            val list = (0 until diff).map { start.plusHours(it) } as MutableList

            return list // generate a list of hourly time slots using a range operator and the map() function
        }

        fun getStringifyTimeTable(timeTable: HashMap<DayOfWeek, Pair<LocalTime, LocalTime>>): String {
            val hm: HashMap<Pair<LocalTime, LocalTime>, MutableList<DayOfWeek>> = HashMap()
            for (day in timeTable) {
                hm.get(Pair(day.value.first, day.value.second)).let {
                    if (it != null) {
                        it.add(day.key)
                    } else {
                        hm[Pair(day.value.first, day.value.second)] = mutableListOf(day.key)
                    }
                }
            }

            val adiacentMap: HashMap<DayOfWeek, String> = HashMap()

            for (entry in hm.entries) {
                entry.value.sort()
                for (x in getConsecutiveNumbers(entry.value)) {
                    val prefix = if (x.first() == x.last()) {
                        "${capitalize(x.first().displayText())}"
                    } else {
                        "${capitalize((x.first()).displayText())} - ${capitalize((x.last()).displayText())}"
                    }


                    val s = "$prefix: ${entry.key.first} - ${entry.key.second}"
                    adiacentMap[x.first()] = s
                }
            }

            return adiacentMap.entries.toList().sortedBy {
                it.key
            }.joinToString("\n") {
                it.value
            }.let {

                return it
            }
        }

        private fun getConsecutiveNumbers(srcList: List<DayOfWeek>): List<List<DayOfWeek>> {
            return srcList.fold(mutableListOf<MutableList<DayOfWeek>>()) { acc, i ->
                println(i)
                if (acc.isEmpty() || acc.last().last() != i - 1) {
                    acc.add(mutableListOf(i))
                } else acc.last().add(i)
                acc
            }
        }


        fun setColoredDrawable(drawable: Drawable, iv: ImageView, color: Int = Color.WHITE) {
            val wrappedDrawable = DrawableCompat.wrap(drawable)
            wrappedDrawable.mutate().setTint(color)
            iv.setImageDrawable(wrappedDrawable)
        }
    }
}