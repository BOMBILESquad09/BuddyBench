package it.polito.mad.buddybench.activities.search_court

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.DTO.CourtDTO
import it.polito.mad.buddybench.R

class SearchCourtActivity : AppCompatActivity() {

    private val l = listOf<CourtDTO>(
        CourtDTO("Prova1","Via Alto",10,3),
        CourtDTO("Prova2","Via basso",12,2),
        CourtDTO("CAMPETTO DA TENNIS!!1!","Assurdo",14,2),
        CourtDTO("Goku","SSJ2",1,2),
        CourtDTO("Justin","xk no napoli? :(",100,2),
        CourtDTO("Vai","Via!!!",1212,2)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_court)

        val recyclerView = findViewById<RecyclerView>(R.id.searchRecyclerView)
        recyclerView.adapter = Adapter(l)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}

class ViewHolder(v: View): RecyclerView.ViewHolder(v){
    private val name: TextView = v.findViewById(R.id.court_name)
    private val address: TextView = v.findViewById(R.id.court_address)
    private val feeHour: TextView = v.findViewById(R.id.court_fee_hour)

    fun bind(court: CourtDTO){
        name.text = court.courtName
        address.text = court.address
        feeHour.text = court.feeHour.toString() + "€/h"
    }
}

class Adapter(private val l:List<CourtDTO>): RecyclerView.Adapter<ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_court,parent,false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val court = l[position]
        holder.bind(court)
    }

}

