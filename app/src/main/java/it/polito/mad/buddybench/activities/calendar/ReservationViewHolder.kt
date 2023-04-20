package it.polito.mad.buddybench.activities.calendar

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.DTO.ReservationDTO
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import java.time.format.DateTimeFormatter

class ReservationViewHolder(v: View): RecyclerView.ViewHolder(v) {

    val courtName : TextView = v.findViewById(R.id.textView6)
    val slot : TextView = v.findViewById(R.id.textView5)
    val iconSport : ImageView = v.findViewById(R.id.imageView)
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    val view: View = v

    fun bind(reservation: ReservationDTO){
        courtName.text = reservation.court.name
        val iconDrawable = ContextCompat.getDrawable(view.context,
            Sports.sportToIconDrawable(
                Sports.fromJSON(
                    reservation.court.sport
                )!!
            )
        )
        val wrappedDrawable = DrawableCompat.wrap(iconDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
        iconSport.setImageDrawable(wrappedDrawable)

        slot.text = "${reservation.startTime.format(formatter)} - ${reservation.endTime.format(formatter)}"
    }

}