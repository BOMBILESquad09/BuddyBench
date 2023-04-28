package it.polito.mad.buddybench.utils

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import java.time.LocalDate

class WeeklyCalendarAdapter(private val l: List<Pair<LocalDate,Boolean>>,
                            var selectedPosition: Int,
                            var callback: (Int, Int) -> Unit
): RecyclerView.Adapter<WeeklyCalendarAdapter.WeeklyCalendarViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyCalendarViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.datepicker_scroll_item,parent,false)
        return WeeklyCalendarViewHolder(v)
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: WeeklyCalendarViewHolder, position: Int) {

        val date = l[position]

        holder.bind(date)
        holder.v.setOnClickListener {
            callback(selectedPosition, position)
            selectedPosition = position
        }
    }

    inner class WeeklyCalendarViewHolder(val v: View): RecyclerView.ViewHolder(v){

        fun bind(date: Pair<LocalDate, Boolean>) {
            val dayTv: TextView = v.findViewById(R.id.day_of_month_tv)
            val dayOfMonthTv: TextView = v.findViewById(R.id.day_of_month_tv)
            val monthTv: TextView = v.findViewById(R.id.month_tv)

            dayTv.text = Utils.capitalize(date.first.dayOfWeek.name.subSequence(0, 3).toString())
            dayOfMonthTv.text = date.first.dayOfMonth.toString()
            monthTv.text = Utils.capitalize(date.first.month.name.subSequence(0, 3).toString())



            val primaryColor =
                ContextCompat.getColor(v.context, R.color.md_theme_light_primary)
            val whiteColor =
                ContextCompat.getColor(v.context, R.color.md_theme_light_background)
            // ** Selected day
            if (date.second) {

                dayOfMonthTv.background.setTint(primaryColor)
                dayOfMonthTv.setTextColor(whiteColor)
            } else {
                dayOfMonthTv.background.setTint(whiteColor)
                dayOfMonthTv.setTextColor(primaryColor)
            }

            if (true){

                return
            }



            // ** Last item no margin at the end
        }
    }
}



