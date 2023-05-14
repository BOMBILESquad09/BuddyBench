package it.polito.mad.buddybench.activities.profile

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Sport

class AchievementViewHolder(private val v: View,
                            private val edit: Boolean,

                            private val achievementAddCallback: (Sport, String) -> Unit = { s, v -> {}},
                            private val achievementRemoveCallback: (Sport, String) -> Unit = { s, v -> {}}):  RecyclerView.ViewHolder(v)
{

    @SuppressLint("SetTextI18n")
    fun bind(achievement: String, sport: Sport){
        println("------------------")
        println(achievement)
        println("-------------------------------------")


        v.findViewById<TextView>(R.id.achievement_name).text = "• $achievement"
        if(!edit){
            v.findViewById<ImageView>(R.id.remove_achievement).visibility = View.GONE
            return
        }

        v.findViewById<ImageView>(R.id.remove_achievement).setOnClickListener {
            val lastAchievement = sport.achievements.map { it }
            achievementRemoveCallback(sport, achievement)
            val diffUtils = AchievementDiffUtils(lastAchievement, sport.achievements)
            val diffResult = DiffUtil.calculateDiff(diffUtils)
            diffResult.dispatchUpdatesTo(this.bindingAdapter!!)
        }
    }
}