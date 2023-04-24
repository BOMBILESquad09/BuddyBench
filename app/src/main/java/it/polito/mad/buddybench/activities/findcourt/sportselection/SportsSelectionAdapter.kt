package it.polito.mad.buddybench.activities.findcourt.sportselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports

class SportsSelectionAdapter(val sports: List<Sports>, private val callback: (Sports) -> Unit): RecyclerView.Adapter<SportsSelectionViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsSelectionViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.sport_selector, parent, false)

        return SportsSelectionViewHolder(v, callback)
    }

    override fun getItemCount(): Int {
        return sports.size
    }

    override fun onBindViewHolder(holder: SportsSelectionViewHolder, position: Int) {
        holder.bind(sports[position])
    }


}