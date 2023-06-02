package it.polito.mad.buddybench.activities.myreservations

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate


class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.dayText)
    private val textViewContainer: ConstraintLayout = view.findViewById(R.id.dayTextContainer)
    private val reservationsContainer: LinearLayout = view.findViewById(R.id.reservationsContainer)
    var reservations: List<ReservationDTO>? = null
    lateinit var day: CalendarDay

    fun setOnClickCallback(callback : (View) -> Unit){
        textViewContainer.setOnClickListener(callback)
    }

    fun setBackground(selectedDate: LocalDate?){

        if(selectedDate == day.date && day.position == DayPosition.MonthDate){
            textView.setBackgroundResource(0)
            // ** Unwrap the R.drawable.circle_selected_drawable to change its color
            val unwrappedDrawable: Drawable? = AppCompatResources.getDrawable(view.context, R.drawable.circle_selected_day)
            val wrappedDrawable = unwrappedDrawable?.let { DrawableCompat.wrap(it) }
            val primaryColor = ContextCompat.getColor(view.context, R.color.md_theme_light_primary)

            // ** If there is 1 reservation change the drawable color to the sport color
            if ((reservations != null && reservations!!.size == 1) || (reservations?.map { it.court.sport }?.toSet()?.size == 1) ) {
                val sportColor = Sports.getSportColor(Sports.valueOf(reservations!![0].court.sport), view.context)

                if (wrappedDrawable != null) {
                    //TO BE FIXED
                    DrawableCompat.setTint(wrappedDrawable, sportColor)
                }
            }
            // ** Else ( > 1 reservation or no reservation) use the primary
            else {
                if (wrappedDrawable != null) {
                    DrawableCompat.setTint(wrappedDrawable, primaryColor)
                }
            }

            textView.setTextColor(ContextCompat.getColor(view.context, R.color.md_theme_light_background))
            textView.setBackgroundResource(R.drawable.circle_selected_day)

        }
        else if (day.date == LocalDate.now()){
            textView.setBackgroundResource(R.drawable.circle_current_day)
            textViewContainer.setBackgroundColor(Color.WHITE)
        }
        else {
            textView.setBackgroundResource(R.drawable.circle_ghost_day)
            textViewContainer.setBackgroundColor(Color.WHITE)
        }
        textViewContainer.setBackgroundColor(Color.WHITE)

    }

    fun setTextColor(selectedDate: LocalDate?, context: Context){
        if (day.date == LocalDate.now()){
            textView.setTextColor(Color.WHITE)
        }
        else if(selectedDate == day.date && day.position == DayPosition.MonthDate){
            textView.setTextColor(context.getColor(R.color.md_theme_light_background))
        }
        else if (day.position == DayPosition.MonthDate) {
            textView.setTextColor(Color.BLACK)
        } else {
            textView.setTextColor(Color.GRAY)
        }
    }

    fun setSportsIcon(context: Activity){
        reservationsContainer.removeAllViews()
        if(reservations != null){

            reservations!!.forEachIndexed { idx, r  ->
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
                        reservationsContainer,
                        true
                    )
                    val iv = sportIcon.findViewById<ImageView>(R.id.sport_icon)
                    iv.id = idx

                    iv.setImageResource(Sports.sportToIconDrawable(Sports.fromJSON(r.court.sport)!!))
                    if(reservations!![idx].requests.isNotEmpty())
                        iv.setColorFilter(Color.rgb(255, 12, 16))
                }
            }
        } else {

            val sportIcon = context.layoutInflater.inflate(
                R.layout.sport_icon,
                reservationsContainer)
            val iv = sportIcon.findViewById<ImageView>(R.id.sport_icon)
            iv.setImageResource(0)
    }}
    // With ViewBinding
    //val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}