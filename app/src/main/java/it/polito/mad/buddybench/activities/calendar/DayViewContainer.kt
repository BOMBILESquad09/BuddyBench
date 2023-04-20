package it.polito.mad.buddybench.activities.calendar

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import java.time.LocalDate

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.dayText)
    val textViewContainer: ConstraintLayout = view.findViewById(R.id.dayTextContainer)
    val reservationsContainer = view.findViewById<LinearLayout>(R.id.reservationsContainer)
    var reservations: List<ReservationDTO>? = null
    lateinit var day: CalendarDay

    fun setOnClickCallback(callback : (View) -> Unit){
        textViewContainer.setOnClickListener(callback)
    }

    fun setBackground(selectedDate: LocalDate?, context: Context){
        if(selectedDate == day.date ){
            textViewContainer.setBackgroundColor(context.getColor(R.color.md_theme_dark_onPrimary))
        }
        else if (day.date == LocalDate.now()){

            textViewContainer.setBackgroundColor(Color.WHITE)
        }
        else if (day.position == DayPosition.MonthDate) {
            textViewContainer.setBackgroundColor(Color.WHITE)

        } else {
            textViewContainer.setBackgroundColor(Color.WHITE)
        }
    }

    fun setTextColor(selectedDate: LocalDate?, context: Context){

        if (day.date == LocalDate.now()){ textView.setBackgroundResource(R.drawable.circle)
            textView.setTextColor(Color.WHITE)
        }
        else if(selectedDate == day.date ){
            textView.setTextColor(context.getColor(R.color.md_theme_dark_onPrimary))
        }
        else if (day.position == DayPosition.MonthDate) {
            textView.setTextColor(Color.BLACK)

        } else {
            textView.setTextColor(Color.GRAY)
        }
    }

    fun setSportsIcon(context: Activity){
        if(reservations != null){
            reservationsContainer.removeAllViews()
            reservations!!.forEachIndexed { idx, _  ->
                if (idx == 2 && idx != (reservations!!.size -1) ){
                    val overflowText = context.layoutInflater.inflate(
                        R.layout.overflow_text,
                        reservationsContainer
                    )

                    val iv = overflowText.findViewById<TextView>(R.id.overflow_text)
                    iv.text = context.getString(R.string.overflowReservations)
                    return@setSportsIcon

                } else {
                    val sportIcon = context.layoutInflater.inflate(
                        R.layout.sport_icon,
                        reservationsContainer
                    )
                    val iv = sportIcon.findViewById<ImageView>(R.id.sport_icon)
                    iv.setImageResource(Sports.sportToIconDrawable(Sports.TENNIS))
                }
            }
        }
    }
    // With ViewBinding
    //val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}