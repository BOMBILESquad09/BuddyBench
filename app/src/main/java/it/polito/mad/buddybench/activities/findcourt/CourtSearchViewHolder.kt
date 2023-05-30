package it.polito.mad.buddybench.activities.findcourt

import android.app.Application
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.internal.managers.FragmentComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.utils.Utils
import java.text.DecimalFormat
import kotlin.concurrent.thread

class CourtSearchViewHolder(val v: View, val callback: (String, Sports) -> Unit, val reviewsCallback: (String, Sports) -> Unit): RecyclerView.ViewHolder(v){

    private val name: TextView = v.findViewById(R.id.court_name)
    private val courtImage: ImageView = v.findViewById(R.id.court_image)
    private val address: TextView = v.findViewById(R.id.court_address)
    private val feeHour: TextView = v.findViewById(R.id.court_fee_hour)
    private val courtRating: TextView = v.findViewById(R.id.court_rating)

    fun bind(court: CourtDTO){

        v.setOnClickListener {
            callback(court.name, Sports.valueOf(court.sport))
        }
        name.text = court.name
        address.text = String.format(v.context.getString(R.string.court_address_card), court.location, court.address)
        feeHour.text = String.format(v.context.getString(R.string.court_fee), court.feeHour.toString())
        feeHour.backgroundTintList = ColorStateList.valueOf(Sports.getSportColor(Sports.valueOf(court.sport), v.context))
        courtRating.text = DecimalFormat("#.0").format(court.rating)
        // ** Reviews
        ((FragmentComponentManager.findActivity(v.context) as HomeActivity)).imageViewModel.getCourtImage(court.path + ".jpg",
            {
                courtImage.setImageResource(R.drawable.default_image)
            }){
            val options = RequestOptions()
            Glide.with(v.context)
                .load(it)
                .apply(options.centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform())
                .into(courtImage)

        }

        courtRating.setOnClickListener {
            reviewsCallback(court.name, Sports.valueOf(court.sport)) }

        /*val bitmap = try {
            BitmapFactory.decodeStream(courtImage.context?.assets?.open("courtImages/" + court.path + ".jpg"))
        } catch (_: FileNotFoundException) {

        }*/

    }

}