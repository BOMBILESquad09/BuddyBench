package it.polito.mad.buddybench.activities.myreservations

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.viewmodels.ReservationViewModel

class ReservationAdapter(
    val viewModel: ReservationViewModel,
    var reservations: List<ReservationDTO>,
    private val launcher: ActivityResultLauncher<Intent>,
): RecyclerView.Adapter<ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_reservation, parent, false)
        return ReservationViewHolder(viewModel, v, launcher)
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation)
    }

}
