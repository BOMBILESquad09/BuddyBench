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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import it.polito.mad.buddybench.activities.invitations.InvitationsDiffsUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.entities.Invitation
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
    private lateinit var swipeRefresh : SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh = view.findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.updateSelectedDay(viewModel.selectedDate.value ?: LocalDate.now())
            viewModel.getAllByUser()
            swipeRefresh.isRefreshing = false
        }

        calendarView = view.findViewById(R.id.calendar)
        currentMonth = YearMonth.now()

        startMonth = currentMonth.minusMonths(0)  // Adjust as needed
        endMonth = currentMonth.plusMonths(1)  // Adjust as needed
        val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first()) // Available from the library
        calendarView.scrollToMonth(currentMonth)
        recyclerViewReservations = view.findViewById(R.id.reservations)
        recyclerViewReservations.layoutManager = LinearLayoutManager(context)
        recyclerViewReservations.adapter = ReservationAdapter(  listOf(), context.launcherReservation)
        calendarView.dayBinder = MyMonthDayBinder(this, calendarView, recyclerViewReservations, context.launcherReservation)
        calendarView.monthHeaderBinder = MyMonthHeaderFooterBinder()
        recyclerViewReservations.isNestedScrollingEnabled = false




        val dayTitle = view.findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM y", Locale.ENGLISH))


        progressLayout = view.findViewById(R.id.progess_layout)
        progressBar = progressLayout.findViewById(R.id.progress_circular)



        context.reservationViewModel.loading.observe(viewLifecycleOwner) {
            if(it) {
                context.findViewById<View>(R.id.emptyReservations).visibility = View.GONE
                recyclerViewReservations.visibility = View.GONE
                progressLayout.visibility = View.VISIBLE
            } else {
                if(viewModel.getSelectedReservations().isNullOrEmpty()){

                    context.findViewById<View>(R.id.emptyReservations).visibility = View.VISIBLE
                    recyclerViewReservations.visibility = View.GONE

                } else {
                    recyclerViewReservations.visibility = View.VISIBLE
                    context.findViewById<View>(R.id.emptyReservations).visibility = View.GONE
                }
                progressLayout.visibility = View.GONE
            }
        }


        val monthName = view.findViewById<TextView>(R.id.monthName)


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
            refreshCalendar()
            refresh()
        }
    }

    private fun refresh(){
        val selectedReservations = viewModel.getSelectedReservations()?.sortedBy { it.endTime } ?: listOf()
        if (viewModel.oldDate != null){
            val firstDay = calendarView.findFirstVisibleMonth()?.yearMonth?.atDay(1) ?: YearMonth.now().atDay(1)
            val lastDay = calendarView.findFirstVisibleMonth()?.yearMonth?.atEndOfMonth() ?: YearMonth.now().atEndOfMonth()
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

        if(selectedReservations.isEmpty()){
            context.findViewById<View>(R.id.emptyReservations).visibility = View.VISIBLE
            recyclerViewReservations.visibility = View.GONE
            (recyclerViewReservations.adapter as ReservationAdapter).reservations = selectedReservations
            recyclerViewReservations.adapter!!.notifyDataSetChanged()

        } else {
            if(!viewModel.loading.value!!){
                recyclerViewReservations.visibility = View.VISIBLE
                context.findViewById<View>(R.id.emptyReservations).visibility = View.GONE
            }
            val diffUtils = InvitationsDiffsUtils((recyclerViewReservations.adapter as ReservationAdapter).reservations, selectedReservations)
            val diff = DiffUtil.calculateDiff(diffUtils)
            (recyclerViewReservations.adapter as ReservationAdapter).reservations = selectedReservations
            diff.dispatchUpdatesTo(recyclerViewReservations.adapter!!)
        }





    }


    private fun refreshCalendar(){
        val reservations = viewModel.reservations.value ?: return

        for(entries in reservations.entries){
            val firstDay = calendarView.findFirstVisibleMonth()?.yearMonth?.atDay(1) ?: YearMonth.now().atDay(1)
            val lastDay = calendarView.findFirstVisibleMonth()?.yearMonth?.atEndOfMonth() ?: YearMonth.now().atEndOfMonth()

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
            if (it != null){

                calendarView.scrollToDate(it)
                calendarView.scrollToMonth(YearMonth.from(it))
                refreshPreviousCalendarButton(YearMonth.from(it))
                refreshNextCalendarButton(YearMonth.from(it))
                refresh()
            } else {
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