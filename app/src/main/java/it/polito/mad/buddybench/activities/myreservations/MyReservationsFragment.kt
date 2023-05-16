package it.polito.mad.buddybench.activities.myreservations

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.material3.contentColorFor
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
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
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
    private lateinit var recyclerViewReservations: RecyclerView
    private lateinit var calendarView: CalendarView
    val viewModel = context.reservationViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var progressLayout: LinearLayout
    private lateinit var currentMonth: YearMonth
    private lateinit var endMonth: YearMonth
    private lateinit var startMonth: YearMonth
    private lateinit var previousButton: ImageView
    private lateinit var nextButton: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendar)
        recyclerViewReservations = view.findViewById(R.id.reservations)
        recyclerViewReservations.layoutManager = LinearLayoutManager(context)
        val dayTitle = view.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))

        calendarView.dayBinder = MyMonthDayBinder(this, calendarView, recyclerViewReservations, context.launcherReservation)

        progressLayout = view.findViewById(R.id.progess_layout)
        progressBar = progressLayout.findViewById(R.id.progress_circular)

        context.reservationViewModel.loading.observe(viewLifecycleOwner) {
            if(it) {
                recyclerViewReservations.visibility = View.GONE
                progressLayout.visibility = View.VISIBLE
            } else {
                progressLayout.visibility = View.GONE
                recyclerViewReservations.visibility = View.VISIBLE
            }
        }

        currentMonth = YearMonth.now()

        startMonth = currentMonth.minusMonths(0)  // Adjust as needed
        endMonth = currentMonth.plusMonths(1)  // Adjust as needed
        val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first()) // Available from the library
        calendarView.scrollToMonth(currentMonth)
        val monthName = view.findViewById<TextView>(R.id.monthName)

        calendarView.monthHeaderBinder = MyMonthHeaderFooterBinder()

        previousButton = view.findViewById<ImageView>(R.id.previousButton)
        nextButton = view.findViewById<ImageView>(R.id.nextButton)
        calendarView.monthScrollListener = { month ->
            monthName.text = month.yearMonth.displayText()
            refreshNextCalendarButton(month.yearMonth)
            refreshPreviousCalendarButton(month.yearMonth)
        }




        refreshNextCalendarButton(currentMonth)
        refreshPreviousCalendarButton(currentMonth)

        previousButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
                refreshPreviousCalendarButton(it.yearMonth.previousMonth)
                refreshCalendar()

            }

        }

        nextButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
                refreshNextCalendarButton(it.yearMonth.nextMonth)
                refreshCalendar()
            }
        }
        viewModel.getAllByUser().observe(viewLifecycleOwner){
            if(it == null)return@observe
            println("----sizeee-----")
            println("----------")

            refreshCalendar()
            refresh()
        }
    }

    private fun refresh(){
        val selectedReservations = viewModel.getSelectedReservations()?.sortedBy { it.endTime }
        context.findViewById<View>(R.id.emptyReservations).let {
            it.visibility = if (selectedReservations.isNullOrEmpty()){
                View.VISIBLE
            } else {
                View.GONE}
        }
        if (viewModel.oldDate != null){
            val firstDay = calendarView.findFirstVisibleMonth()?.yearMonth!!.atDay(1)
            val lastDay = calendarView.findFirstVisibleMonth()?.yearMonth!!.atEndOfMonth()
            if (firstDay <= viewModel.oldDate && viewModel.oldDate!! <= lastDay)
                calendarView.notifyDayChanged(CalendarDay(viewModel.oldDate!!, DayPosition.MonthDate))
            else if (firstDay > viewModel.oldDate){
                calendarView.notifyDayChanged(CalendarDay(viewModel.oldDate!!, DayPosition.InDate))
            }
            else {
                calendarView.notifyDayChanged(CalendarDay(viewModel.oldDate!!, DayPosition.OutDate))
            }
        }
        if (viewModel.selectedDate.value != null){
            calendarView.notifyDayChanged(CalendarDay(viewModel.selectedDate.value!!, DayPosition.MonthDate))
        }

        recyclerViewReservations.adapter = ReservationAdapter(selectedReservations ?: listOf(), context.launcherReservation)
        context.reservationViewModel.loading.postValue(false)

    }


    private fun refreshCalendar(){
        val reservations = viewModel.reservations.value ?: return

        for(entries in reservations.entries){

            val firstDay = calendarView.findFirstVisibleMonth()?.yearMonth!!.atDay(1)
            val lastDay = calendarView.findFirstVisibleMonth()?.yearMonth!!.atEndOfMonth()

            val calendarDay = if (firstDay <= entries.key && entries.key <= lastDay ){
                CalendarDay(entries.key, DayPosition.MonthDate)
            } else if(entries.key < firstDay) {

                CalendarDay(entries.key, DayPosition.InDate)
            } else{

                CalendarDay(entries.key, DayPosition.OutDate)
            }
            calendarView.notifyDayChanged(calendarDay)
        }
        calendarView.notifyDayChanged(CalendarDay(viewModel.selectedDate.value ?: LocalDate.now(), DayPosition.MonthDate))

    }

    override fun onStart() {
        super.onStart()

        viewModel.selectedDate.observe(viewLifecycleOwner){

            if (it != null && viewModel.refresh){
                calendarView.scrollToDate(it)
                calendarView.scrollToMonth(YearMonth.from(it))
                refreshPreviousCalendarButton(YearMonth.from(it))
                refreshNextCalendarButton(YearMonth.from(it))
                refresh()
                viewModel.refresh = false
            } else {
                viewModel.refresh = false
                refresh()
            }
        }


    }

    fun refreshNextCalendarButton(date: YearMonth){
        if (date == endMonth){
            nextButton.setColorFilter(ContextCompat.getColor(context,R.color.md_theme_dark_primary))
            nextButton.isEnabled = false
        } else {
            nextButton.isEnabled = true

            nextButton.setColorFilter(Color.WHITE)
        }
        if(date != startMonth) {
            previousButton.setColorFilter(Color.WHITE)
            previousButton.isEnabled = true
        }
    }

    fun refreshPreviousCalendarButton(date: YearMonth){
        if (date == startMonth){
            previousButton.setColorFilter(ContextCompat.getColor(context,R.color.md_theme_dark_primary))
            previousButton.isEnabled = false
        } else {
            previousButton.isEnabled = true

            previousButton.setColorFilter(Color.WHITE)
        }
        if(date != endMonth) {
            nextButton.setColorFilter(Color.WHITE)
            nextButton.isEnabled = true
        } else {

        }
    }


}