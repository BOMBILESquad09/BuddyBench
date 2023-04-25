package it.polito.mad.buddybench.activities.myreservations

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.HashMap
import java.util.Locale


@AndroidEntryPoint
class MyReservationsFragment(val context: HomeActivity): Fragment(R.layout.my_reservations) {
    lateinit var recyclerViewReservations: RecyclerView
    private val viewModel by viewModels<ReservationViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllByUser(context.profile.email).observe(viewLifecycleOwner) {
            refresh()
        }

        viewModel.selectedDate.observe(viewLifecycleOwner){
            refresh()
        }


        val calendarView = view.findViewById<CalendarView>(R.id.calendar)
        recyclerViewReservations = view.findViewById(R.id.reservations)
        recyclerViewReservations.layoutManager = LinearLayoutManager(context)
        val dayTitle = view.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))


        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }
            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.setOnClickCallback{
                    if (container.day.position == DayPosition.MonthDate) {
                        if (viewModel.selectedDate.value != container.day.date) {
                            val oldDate = viewModel.selectedDate.value
                            viewModel.updateSelectedDay(container.day.date)
                            if(oldDate != null)
                                calendarView.notifyDateChanged(oldDate)
                            calendarView.notifyDateChanged(data.date)
                        }
                        dayTitle.text = viewModel.selectedDate.value?.format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))
                        println(viewModel.selectedDate.value)
                        recyclerViewReservations.adapter = ReservationAdapter(viewModel.getSelectedReservations() ?: listOf())

                    }

                }
                container.textView.text = data.date.dayOfMonth.toString()
                container.setBackground(viewModel.selectedDate.value )
                container.setTextColor(viewModel.selectedDate.value, this@MyReservationsFragment.context)
                container.reservations = viewModel.reservations.value?.get(data.date)
                container.setSportsIcon(this@MyReservationsFragment.context)

            }
        }


        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(0)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(2)  // Adjust as needed
        val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first()) // Available from the library
        calendarView.scrollToMonth(currentMonth)
        val monthName = view.findViewById<TextView>(R.id.monthName)

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth){
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    (container.titlesContainer.children.first() as ViewGroup).children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek[index]
                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                        }
                }
            }
        }

        val previousButton = view.findViewById<ImageView>(R.id.previousButton)
        val nextButton = view.findViewById<ImageView>(R.id.nextButton)
        calendarView.monthScrollListener = { month ->
            monthName.text = month.yearMonth.displayText()
        }
        previousButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        nextButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }
    }

    private fun refresh(){
        val selectedReservations = viewModel.getSelectedReservations()
        context.findViewById<View>(R.id.emptyReservations).let {
            println("------------------------")
            println(selectedReservations.isNullOrEmpty())
            it.visibility = if (selectedReservations.isNullOrEmpty()){
                View.VISIBLE

            } else {
                View.GONE}
        }
        recyclerViewReservations.adapter = ReservationAdapter(
            selectedReservations ?: listOf())
    }



}