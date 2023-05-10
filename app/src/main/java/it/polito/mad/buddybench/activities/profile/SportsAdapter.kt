package it.polito.mad.buddybench.activities.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.myreservations.ReservationViewHolder
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Sports

class SportsAdapter(private val sports: List<Sport>, private val edit: Boolean): RecyclerView.Adapter<SportsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(if (edit) R.layout.card_sport_edit else R.layout.card_sport, parent, false)
        return SportsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return sports.size
    }

    override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
        val sport = sports[position]
        holder.bind(sport)
    }
}