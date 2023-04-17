package it.polito.mad.buddybench.activities


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import it.polito.mad.buddybench.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.dayText)
    val textViewContainer: ConstraintLayout = view.findViewById(R.id.dayTextContainer)
    lateinit var day: CalendarDay

    fun setOnClickCallback(callback : (View) -> Unit){
        textViewContainer.setOnClickListener(callback)
    }
    // With ViewBinding
    //val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}

fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}


class MonthViewContainer(view: View) : ViewContainer(view) {
    // Alternatively, you can add an ID to the container layout and use findViewById()
    val titlesContainer = view as ViewGroup
}


class CalendarActivity : AppCompatActivity() {
     var  selectedDate: LocalDate? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_calendar)
        val calendarView = findViewById<CalendarView>(R.id.exFiveCalendar)
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

                if(selectedDate == container.day.date){
                    container.textView.setTextColor(Color.RED)
                    container.textViewContainer.setBackgroundColor(getColor(R.color.md_theme_dark_primary))
                }
                else if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                    container.textViewContainer.setBackgroundColor(Color.WHITE)

                } else {
                    container.textView.setTextColor(Color.GRAY)
                    container.textViewContainer.setBackgroundColor(Color.WHITE)

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
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Remember that the header is reused so this will be called for each month.
                // However, the first day of the week will not change so no need to bind
                // the same view every time it is reused.
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth

                    (container.titlesContainer.children.first() as ViewGroup).children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek[index]
                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                            // In the code above, we use the same `daysOfWeek` list
                            // that was created when we set up the calendar.
                            // However, we can also get the `daysOfWeek` list from the month data:
                            // val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
                            // Alternatively, you can get the value for this specific index:
                            // val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
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
            println(calendarView.findFirstVisibleMonth()?.yearMonth)

            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)

            }
        }
    }
}


