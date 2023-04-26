package it.polito.mad.buddybench.activities.findcourt.sportselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.dto.CourtDTO

class CourtSearchAdapter(private val l: LiveData<List<CourtDTO>>): RecyclerView.Adapter<CourtSearchViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtSearchViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_court,parent,false)

        return CourtSearchViewHolder(v)
    }

    override fun getItemCount(): Int {
        return l.value!!.size
    }

    override fun onBindViewHolder(holder: CourtSearchViewHolder, position: Int) {
        val court = l.value!![position]
        holder.bind(court)
    }

}