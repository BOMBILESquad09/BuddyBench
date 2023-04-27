package it.polito.mad.buddybench.activities.court

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeSlotViewHolder(
    private val v: View,
    private val callback: (Pair<LocalTime, Boolean>) -> Unit,
    private val sport: Sports
) : RecyclerView.ViewHolder(v) {

    fun bind(pair: Pair<LocalTime, Boolean>) {
        renderTimeItem(pair,  v, callback)
    }

    private fun renderTimeItem(
        pair: Pair<LocalTime, Boolean>,
        v: View,
        callback: (Pair<LocalTime, Boolean>) -> Unit
    ) {
        val timeSlotCard: CardView = v.findViewById(R.id.time_slot_card)
        val timeSlotTv: TextView = v.findViewById(R.id.time_slot_tv)

        // ** Text is (time)
        val timeSlotText = pair.first.format(DateTimeFormatter.ofPattern("HH:mm"))
        timeSlotTv.text = timeSlotText

        // ** Selected time
        val primaryColor = ContextCompat.getColor(v.context, R.color.md_theme_light_primary)
        val sportColor = Sports.getSportColor(sport, v.context)
        val whiteColor = ContextCompat.getColor(v.context, R.color.md_theme_light_background)
        if (pair.second) {
            timeSlotCard.background.setTint(sportColor)
            timeSlotTv.setTextColor(whiteColor)
        } else {
            timeSlotCard.background.setTint(whiteColor)
            timeSlotTv.setTextColor(primaryColor)
        }

        timeSlotCard.setOnClickListener {
            callback(pair)
        }
    }
}

