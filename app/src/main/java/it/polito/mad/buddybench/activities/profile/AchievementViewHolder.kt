package it.polito.mad.buddybench.activities.profile

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Sport

class AchievementViewHolder(private val v: View,
                            private val edit: Boolean,

                            private val achievementAddCallback: (Sport, String) -> Unit = { s, v -> {}},
                            private val achievementRemoveCallback: (Sport, String) -> Unit = { s, v -> {}}):  RecyclerView.ViewHolder(v)
{

    fun bind(achievement: String, sport: Sport){
        v.findViewById<TextView>(R.id.achievement_name).text = achievement
        v.findViewById<TextView>(R.id.remove_achievement).setOnClickListener {
            achievementRemoveCallback(sport, achievement)
        }
    }
}