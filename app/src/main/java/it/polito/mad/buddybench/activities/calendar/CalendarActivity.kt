package it.polito.mad.buddybench.activities.calendar


import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Print
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.repositories.ReservationRepository
import it.polito.mad.buddybench.utils.BottomBar
import it.polito.mad.buddybench.viewmodels.CourtViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarActivity : AppCompatActivity() {

    @Inject
    lateinit var print: Print

    var  selectedDate: LocalDate? = null
    val reservations: MutableLiveData<HashMap<LocalDate,List<ReservationDTO>>> = MutableLiveData(
        null
    )

    private val bottomBar = BottomBar(this)
    lateinit var recyclerViewReservations: RecyclerView

    private val viewModel by viewModels<ReservationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call the repo in this way
        Log.d("HiltApplication", application.javaClass.name)

        // Adding the ViewModel inside reservations with observer
        viewModel.getAll().observe(this, androidx.lifecycle.Observer {
            reservations.value = it
        })

        setContentView(R.layout.custom_calendar)
        val calendarView = findViewById<CalendarView>(R.id.calendar)
        recyclerViewReservations = findViewById(R.id.reservations)
        recyclerViewReservations.layoutManager = LinearLayoutManager(this)
        val dayTitle = findViewById<TextView>(R.id.dayTitle)
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
                        if (selectedDate != container.day.date) {
                            val oldDate = selectedDate
                            selectedDate = container.day.date
                            if(oldDate != null)
                                calendarView.notifyDateChanged(oldDate)
                            calendarView.notifyDateChanged(data.date)
                        }
                    }
                    dayTitle.text = selectedDate?.format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))
                }
                container.textView.text = data.date.dayOfMonth.toString()
                container.setBackground(selectedDate )
                container.setTextColor(selectedDate, this@CalendarActivity)
                container.reservations = reservations.value?.get(data.date)
                container.setSportsIcon(this@CalendarActivity)

                if(selectedDate == null) {
                    recyclerViewReservations.adapter = ReservationAdapter(
                        reservations.value?.get(LocalDate.now()) ?: listOf())
                } else {
                    recyclerViewReservations.adapter = ReservationAdapter(
                        reservations.value?.get(selectedDate) ?: listOf())
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


