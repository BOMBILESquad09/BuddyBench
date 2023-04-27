package it.polito.mad.buddybench.activities.findcourt.sportselection

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils

class SportsSelectionViewHolder(private val v: View, private val callback: (Sports) -> Unit): RecyclerView.ViewHolder(v) {

    fun bind(sport: Sports){
        val icon = ContextCompat.getDrawable(v.context, Sports.sportToIconDrawable(sport))
        val wrappedDrawable = DrawableCompat.wrap(icon!!)
        val sportSelectionCard = v.findViewById<CardView>(R.id.sport_selection_card)

        Utils.setColoredDrawable(wrappedDrawable, v.findViewById<ImageView>(R.id.sport_image))
        sportSelectionCard.setBackgroundColor(Sports.getSportColor(sport, v.context))
        v.findViewById<TextView>(R.id.sport_name).text = Utils.capitalize(sport.name)
        v.setOnClickListener { callback(sport) }
    }

}
