package it.polito.mad.buddybench.activities.court

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeSlotViewHolder(private val v: View,
                         private val listSelected: MutableList<LocalTime>,
                         private val callback: (MutableList<LocalTime>, LocalTime) -> Unit
) : RecyclerView.ViewHolder(v) {


    fun bind(time: LocalTime) {
        renderTimeItem(time, listSelected,  v, callback)
    }
}

private fun renderTimeItem(
    time: LocalTime,
    selected: MutableList<LocalTime>,
    v: View,
    callback: (MutableList<LocalTime>, LocalTime) -> Unit
) {
    val timeSlotCard: CardView = v.findViewById(R.id.time_slot_card)
    val timeSlotTv: TextView = v.findViewById(R.id.time_slot_tv)

    // ** Text is (time)
    val timeSlotText = time.format(DateTimeFormatter.ofPattern("HH:mm"))
    timeSlotTv.text = timeSlotText

    // ** Selected time
    if (selected.contains(time)) {
        val primaryColor =
            ContextCompat.getColor(v.context, R.color.md_theme_light_primary)
        val whiteColor =
            ContextCompat.getColor(v.context, R.color.md_theme_light_background)
        timeSlotCard.background.setTint(primaryColor)
        timeSlotTv.setTextColor(whiteColor)
    }

    timeSlotCard.setOnClickListener { callback(selected, time) }

}