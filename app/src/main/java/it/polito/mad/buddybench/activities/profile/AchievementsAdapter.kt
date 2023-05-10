package it.polito.mad.buddybench.activities.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Sport

class AchievementsAdapter(private val sport: Sport,
                          private val pos:Int,
                          private val edit: Boolean,
                            private val achievementAddCallback: (Sport, String) -> Unit = {s, v -> {}},
                            private val achievementRemoveCallback: (Sport, String) -> Unit = { s, v -> {}}
) : RecyclerView.Adapter<AchievementViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(if (edit) R.layout.achievement_item else R.layout.achievement_item, parent, false)
        return AchievementViewHolder(v, edit, achievementAddCallback, achievementRemoveCallback)
    }

    override fun getItemCount(): Int {
        return sport.achievements.size
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {

        val achievement = sport.achievements[position]
        holder.bind(achievement, sport)
    }


}