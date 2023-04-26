package it.polito.mad.buddybench.activities.court

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeSlotViewHolder(private val v: View,
                         private val timeSlots: LiveData<List<Pair<LocalTime, Boolean>>>,
                         private val callback: (Pair<LocalTime, Boolean>) -> Unit
) : RecyclerView.ViewHolder(v) {

    private var created: Boolean = false
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
        val primaryColor =
            ContextCompat.getColor(v.context, R.color.md_theme_light_primary)
        val whiteColor =
            ContextCompat.getColor(v.context, R.color.md_theme_light_background)
        if (pair.second) {
            timeSlotCard.background.setTint(primaryColor)
            timeSlotTv.setTextColor(whiteColor)
        } else {
            timeSlotCard.background.setTint(whiteColor)
            timeSlotTv.setTextColor(primaryColor)
        }
        println(timeSlots.value.toString())

        timeSlotCard.setOnClickListener {

            callback(pair)



        }

    }
}

