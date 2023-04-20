package it.polito.mad.buddybench.activities.calendar


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.DAO.ReservationDao
import it.polito.mad.buddybench.DAO.UserDao
import it.polito.mad.buddybench.DTO.ReservationDTO
import it.polito.mad.buddybench.Database.CourtReservationDatabase
import it.polito.mad.buddybench.Database.DatabaseModule
import it.polito.mad.buddybench.Entities.User
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.repositories.ReservationRepository
import it.polito.mad.buddybench.repositories.UserRepository
import it.polito.mad.buddybench.utils.BottomBar
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarActivity : AppCompatActivity() {
    var  selectedDate: LocalDate? = null
    val reservations = ReservationDTO.mockReservationDTOs()
    val bottomBar = BottomBar(this)
    lateinit var recyclerViewReservations: RecyclerView

    @Inject
    lateinit var repoReservation: ReservationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call the repo in this way
        repoReservation.getAll()

        setContentView(R.layout.custom_calendar)
        val calendarView = findViewById<CalendarView>(R.id.calendar)
        recyclerViewReservations = findViewById(R.id.reservations)
        recyclerViewReservations.layoutManager = LinearLayoutManager(this)

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
                        if (selectedDate != container.day.date) {
                            val oldDate = selectedDate
                            selectedDate = container.day.date
                            if(oldDate != null)
                                calendarView.notifyDateChanged(oldDate)
                            calendarView.notifyDateChanged(data.date)
                        }
                    }
                }
                container.textView.text = data.date.dayOfMonth.toString()
                container.setBackground(selectedDate, this@CalendarActivity )
                container.setTextColor(selectedDate, this@CalendarActivity)
                container.reservations = reservations[data.date]
                container.setSportsIcon(this@CalendarActivity)

                if(selectedDate == null) {
                    recyclerViewReservations.adapter = ReservationAdapter(reservations[LocalDate.now()] ?: listOf())
                } else {
                    recyclerViewReservations.adapter = ReservationAdapter(reservations[selectedDate] ?: listOf())
                }



            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first()) // Available from the library
        calendarView.scrollToMonth(currentMonth)
        val monthName = findViewById<TextView>(R.id.monthName)

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

        val previousButton = findViewById<ImageView>(R.id.previousButton)
        val nextButton = findViewById<ImageView>(R.id.nextButton)
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

        bottomBar.setup()
    }
}


