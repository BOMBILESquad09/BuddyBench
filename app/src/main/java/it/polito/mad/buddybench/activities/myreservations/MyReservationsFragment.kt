package it.polito.mad.buddybench.activities.myreservations

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    lateinit var calendarView: CalendarView
    val viewModel by viewModels<ReservationViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllByUser(context.profile.email).observe(viewLifecycleOwner) {
            refresh()
        }

        viewModel.selectedDate.observe(viewLifecycleOwner){
            refresh()
        }


        calendarView = view.findViewById(R.id.calendar)
        recyclerViewReservations = view.findViewById(R.id.reservations)
        recyclerViewReservations.layoutManager = LinearLayoutManager(context)
        val dayTitle = view.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))

        calendarView.dayBinder = MyMonthDayBinder(this, calendarView, recyclerViewReservations)


        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(0)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(2)  // Adjust as needed
        val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first()) // Available from the library
        calendarView.scrollToMonth(currentMonth)
        val monthName = view.findViewById<TextView>(R.id.monthName)

        calendarView.monthHeaderBinder = MyMonthHeaderFooterBinder()

        val previousButton = view.findViewById<ImageView>(R.id.previousButton)
        val nextButton = view.findViewById<ImageView>(R.id.nextButton)
        calendarView.monthScrollListener = { month ->
            monthName.text = month.yearMonth.displayText()
        }
        previousButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
                if (it.yearMonth.previousMonth == startMonth){
                    previousButton.setColorFilter(ContextCompat.getColor(context,R.color.md_theme_dark_primary))
                } else {
                    previousButton.setColorFilter(Color.WHITE)
                }
                nextButton.setColorFilter(Color.WHITE)

            }

        }

        nextButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
                if (it.yearMonth.nextMonth == endMonth){
                    nextButton.setColorFilter(ContextCompat.getColor(context,R.color.md_theme_dark_primary))
                } else {
                    nextButton.setColorFilter(Color.WHITE)
                }
                previousButton.setColorFilter(Color.WHITE)
            }
        }


        viewModel.reservations.observe(viewLifecycleOwner){
            refreshCalendar()
        }
    }

    private fun refresh(){
        val selectedReservations = viewModel.getSelectedReservations()
        context.findViewById<View>(R.id.emptyReservations).let {
            println(selectedReservations.isNullOrEmpty())
            it.visibility = if (selectedReservations.isNullOrEmpty()){
                View.VISIBLE
            } else {
                View.GONE}
        }
        recyclerViewReservations.adapter = ReservationAdapter(
            selectedReservations ?: listOf())
    }


    private fun refreshCalendar(){
        val reservations = viewModel.reservations.value ?: return

        for(entries in reservations.entries){

            val firstDay = calendarView.findFirstVisibleMonth()?.weekDays?.get(0)?.get(0)!!
            val lastDay = calendarView.findFirstVisibleMonth()?.weekDays?.last()?.last()!!
            val calendarDay = if (firstDay.date <= entries.key && entries.key <= lastDay.date ){
                CalendarDay(entries.key, DayPosition.MonthDate)
            } else {
                null
            }
            if(calendarDay != null)
                calendarView.notifyDayChanged(calendarDay)

        }
    }

}