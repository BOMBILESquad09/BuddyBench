package it.polito.mad.buddybench.activities.court


import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.myreservations.displayText
import java.time.LocalDate
import java.time.Period


class WeeklyCalendarDayBinder(var selectedDate: LocalDate, val calendarCallback: (LocalDate, LocalDate) -> Unit) : WeekDayBinder<WeeklyCalendarDayBinder.WeekDayViewContainer> {
    override fun bind(container: WeekDayViewContainer, data: WeekDay) {

        container.bind(data)
    }

    override fun create(view: View): WeekDayViewContainer {

        return WeekDayViewContainer(view)
    }

    inner class WeekDayViewContainer(val v: View): ViewContainer(v){



        fun bind(data: WeekDay) {
            v.findViewById<TextView>(R.id.day_t).text = data.date.dayOfWeek.displayText()
            v.findViewById<TextView>(R.id.day_of_month_tv).let{
                val primaryColor =
                    ContextCompat.getColor(v.context, R.color.md_theme_light_primary)
                val whiteColor =
                    ContextCompat.getColor(v.context, R.color.md_theme_light_background)
                if (data.date == selectedDate) {
                    it.background.setTint(primaryColor)
                    it.setTextColor(whiteColor)
                } else {
                    it.background.setTint(whiteColor)
                    it.setTextColor(primaryColor)
                }
                it.text = data.date.dayOfMonth.toString()
            }
            v.findViewById<TextView>(R.id.month_tv).text = data.date.month.displayText()
            // ** Selected day

            v.setOnClickListener {
                if (LocalDate.now() > data.date) return@setOnClickListener
                calendarCallback(selectedDate, data.date)
                selectedDate = data.date
            }


        }

    }

}





