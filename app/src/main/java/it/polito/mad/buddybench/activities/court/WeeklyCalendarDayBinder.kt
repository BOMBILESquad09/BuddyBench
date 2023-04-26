package it.polito.mad.buddybench.activities.court

import it.polito.mad.buddybench.activities.myreservations.DayViewContainer


import android.view.View
import android.widget.TextView

import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.myreservations.displayText
import org.w3c.dom.Text


class WeeklyCalendarDayBinder : WeekDayBinder<WeekDayViewContainer> {
    override fun bind(container: WeekDayViewContainer, data: WeekDay) {
        container.bind(data)
    }

    override fun create(view: View): WeekDayViewContainer {
        return WeekDayViewContainer(view)
    }


}

class WeekDayViewContainer(val v: View): ViewContainer(v){



    fun bind(data: WeekDay) {
        v.findViewById<TextView>(R.id.day_t).text = data.date.dayOfWeek.displayText()
        v.findViewById<TextView>(R.id.day_of_month_tv).text = data.date.dayOfMonth.toString()
        v.findViewById<TextView>(R.id.month_tv).text = data.date.month.displayText()

    }

}



