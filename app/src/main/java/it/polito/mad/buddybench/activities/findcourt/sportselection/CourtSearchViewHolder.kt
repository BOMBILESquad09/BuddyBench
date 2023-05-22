package it.polito.mad.buddybench.activities.findcourt.sportselection

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.findcourt.SearchFragment
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import java.io.FileNotFoundException
import java.text.DecimalFormat

class CourtSearchViewHolder(val v: View, val callback: (String, Sports) -> Unit, val reviewsCallback: (String, Sports) -> Unit): RecyclerView.ViewHolder(v){

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
        feeHour.backgroundTintList = ColorStateList.valueOf(Sports.getSportColor(Sports.valueOf(court.sport), v.context))
        courtRating.text = DecimalFormat("#.0").format(court.rating)
        // ** Reviews
        ((FragmentComponentManager.findActivity(v.context) as HomeActivity)).imageViewModel.getCourtImage(court.path + ".jpg",
            {
                courtImage.setImageBitmap(BitmapFactory.decodeStream(courtImage.context?.assets?.open("courtImages/default_image.jpg")))
            }){
            Glide.with(v.context)
                .load(it)
                .into(courtImage)
        }

        courtRating.setOnClickListener { reviewsCallback(court.name, Sports.valueOf(court.sport)) }

        /*val bitmap = try {
            BitmapFactory.decodeStream(courtImage.context?.assets?.open("courtImages/" + court.path + ".jpg"))
        } catch (_: FileNotFoundException) {

        }*/

    }

}