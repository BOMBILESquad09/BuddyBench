package it.polito.mad.buddybench.activities.profile

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils

class SportsViewHolder(val v: View): RecyclerView.ViewHolder(v) {
    fun bind(sport: Sport){
        val sportName = v.findViewById<TextView>(R.id.sport_card_name)
        val sportIcon = v.findViewById<ImageView>(R.id.sport_card_icon)
        val sportSkillLevelText = v.findViewById<TextView>(R.id.skill_level_card_text)
        val sportGamesPlayed = v.findViewById<TextView>(R.id.games_played_text)

        sportName.text = Utils.formatString(sport.name.toString())
        sportIcon?.setImageResource(Sports.sportToIconDrawable(sport.name))
        // TODO: Doesn't work
        //sportSkillLevelText.setBackgroundColor(Skills.skillToColor(sport.skill))
        sportSkillLevelText.text = Utils.formatString(sport.skill.toString())
        sportGamesPlayed.text = String.format(
            v.context.resources.getString(R.string.games_played),
            sport.matchesPlayed
        )
    }
}