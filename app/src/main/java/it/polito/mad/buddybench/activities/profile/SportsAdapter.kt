package it.polito.mad.buddybench.activities.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.myreservations.ReservationViewHolder
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Sports

class SportsAdapter(private val sports: LiveData<MutableList<Sport>>, private val edit: Boolean,
    private val sportRemoveCallback: (Sport) -> Unit = {},
    private val sportSkillCallback: (Sport, View) -> Unit = { s, v -> {}},
                    private val achievementRemoveCallback: (Sport, String) -> Unit = {s,v -> {}},
                    private val achievementAddCallback: (Sport, String) -> Unit = {s,v -> {}}
): RecyclerView.Adapter<SportsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(if (edit) R.layout.card_sport_edit else R.layout.card_sport, parent, false)
        return SportsViewHolder(v, edit, sportRemoveCallback, sportSkillCallback,achievementRemoveCallback, achievementAddCallback)
    }

    override fun getItemCount(): Int {
        return sports.value!!.size
    }

    override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
        val sport = sports.value!![position]
        holder.bind(sport)
    }
}