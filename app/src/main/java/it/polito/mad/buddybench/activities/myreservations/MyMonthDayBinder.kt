package it.polito.mad.buddybench.activities.myreservations

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyMonthDayBinder(val context: MyReservationsFragment, private val calendarView: CalendarView,
                       private val recyclerView: RecyclerView, private val launcher: ActivityResultLauncher<Intent>): MonthDayBinder<DayViewContainer> {
    // Called only when a new container is needed.

    private val dayTitle: TextView =  context.context.findViewById(R.id.dayTitle)

    override fun create(view: View): DayViewContainer {
        return DayViewContainer(view)
    }


    // Called every time we need to reuse a container.
    override fun bind(container: DayViewContainer, data: CalendarDay) {
        container.day = data

        val viewModel = context.viewModel
        container.setOnClickCallback {
            if (container.day.position == DayPosition.MonthDate) {
                if (context.viewModel.selectedDate.value != container.day.date) {
                    viewModel.updateSelectedDay(container.day.date)
                    if (viewModel.oldDate != null)
                        calendarView.notifyDateChanged(viewModel.oldDate!!)
                    calendarView.notifyDateChanged(data.date)
                }

            }

        }
        if(viewModel.selectedDate.value == data.date){
            dayTitle.text =
                viewModel.selectedDate.value?.format(DateTimeFormatter.ofPattern("EEEE, d MMMM y", Locale.ENGLISH))
        }

        container.textView.text = data.date.dayOfMonth.toString()
        container.reservations = viewModel.reservations.value?.get(data.date)
        container.setTextColor(viewModel.selectedDate.value, context.context)
        container.setBackground(viewModel.selectedDate.value)
        container.setSportsIcon(context.context)


    }
}