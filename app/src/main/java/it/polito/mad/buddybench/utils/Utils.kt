package it.polito.mad.buddybench.utils

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.icu.text.DecimalFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.core.graphics.drawable.DrawableCompat
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.myreservations.displayText
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale
import java.util.UUID


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
         * @return array of time ranges [8:00, 9:00, 10:00]
         */
        fun getTimeSlots(start: LocalTime, end: LocalTime): List<LocalTime> {
            val diff = Duration.between(start, end)
                .toHours() // calculate the difference between start and end time in hours
            val list = (0 until diff).map { start.plusHours(it) } as MutableList

            return list // generate a list of hourly time slots using a range operator and the map() function
        }

        fun getStringifyTimeTable(timeTable: HashMap<DayOfWeek, Pair<LocalTime, LocalTime>>): Pair<String,String> {
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


                    val s = "$prefix: ! ${entry.key.first} - ${entry.key.second}"
                    adiacentMap[x.first()] = s
                }
            }



            return Pair(

            adiacentMap.entries.toList().sortedBy {
                it.key
            }.joinToString("\n") {
                it.value.split(":").first()
            },

            adiacentMap.entries.toList().sortedBy {
                it.key
            }.joinToString("\n") {
                it.value.split("!").last()
            })



        }

        private fun getConsecutiveNumbers(srcList: List<DayOfWeek>): List<List<DayOfWeek>> {
            return srcList.fold(mutableListOf<MutableList<DayOfWeek>>()) { acc, i ->
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

        fun getDateRanges(): Pair<LocalDate, LocalDate> {
            val now = LocalDate.now()
            val fieldISO = WeekFields.of(Locale.FRANCE).dayOfWeek()
            val start = now.with(fieldISO,1)
            val end = now.plusWeeks(4).with(fieldISO, 7)
            return Pair(start, end)
        }


        fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING.ordinal
            return df.format(number).toDouble()
        }

        fun openProgressDialgo(context: Context): AlertDialog{
            val llPadding = 30
            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.VERTICAL
            ll.setPadding(llPadding, llPadding, llPadding, llPadding)
            ll.gravity = Gravity.CENTER
            var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            ll.layoutParams = llParam

            // Creating a ProgressBar inside the layout
            val progressBar = ProgressBar(context)
            progressBar.isIndeterminate = true
            progressBar.setPadding(0, 0, llPadding, 0)
            progressBar.layoutParams = llParam
            llParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER



            // Creating a TextView inside the layout
            val tvText = TextView(context)
            tvText.text = "Loading..."
            tvText.setTextColor(Color.parseColor("#000000"))
            tvText.textSize = 20f
            tvText.layoutParams = llParam
            ll.addView(progressBar)
            ll.addView(tvText)
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(false)
            builder.setView(ll)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return dialog
        }

        fun generateUUID(): String {
            val uuid = UUID.randomUUID()
            return uuid.toString()
        }
    }
}