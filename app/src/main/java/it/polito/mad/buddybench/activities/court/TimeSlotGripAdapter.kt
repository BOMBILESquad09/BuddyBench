package it.polito.mad.buddybench.activities.court

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import java.time.LocalTime

class TimeSlotGripAdapter(
        private val timeSlots: LiveData<List<Pair<LocalTime, Boolean>>>,
        private val callback: (Pair<LocalTime, Boolean>) -> Unit,
        private val sport: Sports
    ): RecyclerView.Adapter<TimeSlotViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.datepicker_time_scroll_item, parent, false)
        return TimeSlotViewHolder(v, callback, sport)
    }

    override fun getItemCount(): Int {
        return timeSlots.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bind(timeSlots.value!![position])
    }

}