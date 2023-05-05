package it.polito.mad.buddybench.activities.myreservations

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.R

class ReservationAdapter(private val reservations: List<ReservationDTO>, private val launcher: ActivityResultLauncher<Intent>): RecyclerView.Adapter<ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_reservation, parent, false)
        return ReservationViewHolder(v, launcher)
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation)
    }

}
