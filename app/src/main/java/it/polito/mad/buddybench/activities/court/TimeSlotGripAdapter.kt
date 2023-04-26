package it.polito.mad.buddybench.activities.court

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.findcourt.sportselection.CourtSearchViewHolder
import it.polito.mad.buddybench.activities.findcourt.sportselection.SportsSelectionViewHolder
import it.polito.mad.buddybench.enums.Sports
import java.time.LocalTime

class TimeSlotGripAdapter(
    private val timeSlots: List<LocalTime>,
    private val callback: (MutableList<LocalTime>, LocalTime) -> Unit,
    private val selectedTimes: MutableList<LocalTime>,
): RecyclerView.Adapter<TimeSlotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.datepicker_time_scroll_item, parent, false)
        return TimeSlotViewHolder(v, selectedTimes, callback)
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }

}