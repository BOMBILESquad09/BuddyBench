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
import it.polito.mad.buddybench.dto.CourtDTO
import java.io.FileNotFoundException

class CourtSearchViewHolder(val v: View): RecyclerView.ViewHolder(v){
    private val name: TextView = v.findViewById(R.id.court_name)
    private val courtImage: ImageView = v.findViewById(R.id.court_image)
    private val address: TextView = v.findViewById(R.id.court_address)
    private val feeHour: TextView = v.findViewById(R.id.court_fee_hour)
    private val courtRating: TextView = v.findViewById(R.id.court_rating)


    fun bind(court: CourtDTO){

        v.setOnClickListener {
            val intent = Intent(v.context, CourtActivity::class.java)
            intent.putExtra("courtName", court.name)
            intent.putExtra("sport", court.sport)
            v.context.startActivity(intent)

        }

        name.text = court.name
        address.text = court.location + ", " + court.address
        feeHour.text = court.feeHour.toString() + "€/h"
        courtRating.text = court.rating.toString()

        val bitmap = try {
            BitmapFactory.decodeStream(courtImage.context?.assets?.open("courtImages/" + court.path + ".jpg"))
        } catch (_: FileNotFoundException) {
            println(court.path)
            BitmapFactory.decodeStream(courtImage.context?.assets?.open("courtImages/default_image.jpg"))
        }
        courtImage.setImageBitmap(bitmap)


    }
}