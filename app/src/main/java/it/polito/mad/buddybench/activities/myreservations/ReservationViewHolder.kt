package it.polito.mad.buddybench.activities.myreservations

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import java.time.format.DateTimeFormatter

class ReservationViewHolder(v: View): RecyclerView.ViewHolder(v) {

    private val courtName : TextView = v.findViewById(R.id.textView6)
    private val slot : TextView = v.findViewById(R.id.textView5)
    private val iconSport : ImageView = v.findViewById(R.id.imageView)
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
        Utils.setColoredDrawable(iconDrawable!!, iconSport)
        slot.text = "${reservation.startTime.format(formatter)} - ${reservation.endTime.format(formatter)}"

    }


}