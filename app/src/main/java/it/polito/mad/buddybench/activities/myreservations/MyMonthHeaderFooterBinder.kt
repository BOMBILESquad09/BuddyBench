package it.polito.mad.buddybench.activities.myreservations

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

class MyMonthHeaderFooterBinder: MonthHeaderFooterBinder<MonthViewContainer> {
    private val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)

    override fun create(view: View) = MonthViewContainer(view)
    override fun bind(container: MonthViewContainer, data: CalendarMonth){
        if (container.titlesContainer.tag == null) {
            container.titlesContainer.tag = data.yearMonth
            (container.titlesContainer.children.first() as ViewGroup).children.map { it as TextView }
                .forEachIndexed { index, textView ->
                    val dayOfWeek = daysOfWeek[index]
                    val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    textView.text = title
                }
        }
    }
}