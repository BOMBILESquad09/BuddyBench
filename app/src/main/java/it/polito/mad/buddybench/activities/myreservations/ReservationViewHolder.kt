package it.polito.mad.buddybench.activities.myreservations

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.court.CourtActivity
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationViewHolder(v: View): RecyclerView.ViewHolder(v) {

    private val courtName : TextView = v.findViewById(R.id.textView6)
    private val slot : TextView = v.findViewById(R.id.textView5)
    private val iconSport : ImageView = v.findViewById(R.id.imageView)
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    private var manageBtn: TextView = v.findViewById(R.id.manage_btn)

    val view: View = v

    fun bind(reservation: ReservationDTO){

        // ** Card color
        val card = view.findViewById<CardView>(R.id.card_reservation)
        card.setCardBackgroundColor(Sports.getSportColor(Sports.valueOf(reservation.court.sport), view.context))

        courtName.text = reservation.court.name
        val iconDrawable = ContextCompat.getDrawable(view.context,
            Sports.sportToIconDrawableAlternative(
                Sports.fromJSON(
                    reservation.court.sport
                )!!
            )
        )

        val wrappedDrawable = DrawableCompat.wrap(iconDrawable!!)
        iconSport.setImageDrawable(wrappedDrawable)
        slot.text = "${reservation.startTime.format(formatter)} - ${reservation.endTime.format(formatter)}"
        if (LocalDate.now() > reservation.date || (LocalDate.now() == reservation.date && LocalTime.now() > reservation.startTime))
            manageBtn.visibility = View.INVISIBLE
        manageBtn.setTextColor(Sports.getSportColor(Sports.valueOf(reservation.court.sport), view.context))
        manageBtn.setOnClickListener {
            if (LocalDate.now() > reservation.date || (LocalDate.now() == reservation.date && LocalTime.now() > reservation.startTime)){
                return@setOnClickListener
            }
            launchEditReservation(reservation)
        }

    }

    private fun launchEditReservation(reservation: ReservationDTO) {
        val intent = Intent(view.context, CourtActivity::class.java)
        intent.putExtra("edit", true)
        intent.putExtra("courtName", reservation.court.name)
        intent.putExtra("sport", reservation.court.sport)
        intent.putExtra("date", reservation.date.toString())
        intent.putExtra("email", reservation.userOrganizer.email)
        intent.putExtra("startTime", reservation.startTime.hour)
        intent.putExtra("endTime", reservation.endTime.hour)

        view.context.startActivity(intent)
    }


}