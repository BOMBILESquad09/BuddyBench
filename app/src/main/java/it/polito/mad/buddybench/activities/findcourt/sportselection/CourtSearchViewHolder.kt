package it.polito.mad.buddybench.activities.findcourt.sportselection

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.court.CourtActivity
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.enums.Sports
import java.io.FileNotFoundException

class CourtSearchViewHolder(val v: View, val callback: (String, Sports) -> Unit): RecyclerView.ViewHolder(v){

    private val name: TextView = v.findViewById(R.id.court_name)
    private val courtImage: ImageView = v.findViewById(R.id.court_image)
    private val address: TextView = v.findViewById(R.id.court_address)
    private val feeHour: TextView = v.findViewById(R.id.court_fee_hour)
    private val courtRating: TextView = v.findViewById(R.id.court_rating)

    fun bind(court: CourtDTO){

        v.setOnClickListener { callback(court.name, Sports.valueOf(court.sport)) }
        name.text = court.name
        address.text = String.format(v.context.getString(R.string.court_address_card), court.location, court.address)
        feeHour.text = String.format(v.context.getString(R.string.court_fee), court.feeHour.toString())
        courtRating.text = court.rating.toString()
        val bitmap = try {
            BitmapFactory.decodeStream(courtImage.context?.assets?.open("courtImages/" + court.path + ".jpg"))
        } catch (_: FileNotFoundException) {
            BitmapFactory.decodeStream(courtImage.context?.assets?.open("courtImages/default_image.jpg"))
        }
        courtImage.setImageBitmap(bitmap)

    }

}