package it.polito.mad.buddybench.activities.profile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils

class SportsViewHolder(val v: View,
                       val edit: Boolean,
                       private val sportRemoveCallback: (Sport) -> Unit = {},
                       private val sportSkillCallback: (Sport, View) -> Unit = {s,v -> {}},
                       private val achievementRemoveCallback: (Sport, String) -> Unit = {s,v -> {}},
                       private val achievementAddCallback: (Sport, String) -> Unit = {s,v -> {}}


): RecyclerView.ViewHolder(v) {
    fun bind(sport: Sport){
        println("refreshhhhh")
        v.visibility = View.VISIBLE
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
        val achievementButton = v.findViewById<ImageView>(R.id.achievements)
        achievementButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(v.context)
            val dialogCard = LayoutInflater.from(v.context).inflate(R.layout.card_sport_expanded, null);
            setAchievementCard(dialogCard, sport, edit, )
            builder.setView(dialogCard)
            val dialog: AlertDialog = builder.create()
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }


        if(edit){
            v.findViewById<LinearLayout>(R.id.close_button).setOnClickListener {
                println("okkkk")
                sportRemoveCallback(sport)
            }
            val sportSkillLevel = v.findViewById<CardView>(R.id.skill_level_card)

            sportSkillLevel.setOnClickListener {
                sportSkillCallback(sport.copy(), sportSkillLevel)
            }
        }

    }


    private fun setAchievementCard(view: View, sport: Sport, edit: Boolean){
        val sportName = view.findViewById<TextView>(R.id.sport_card_name)
        val sportIcon = view.findViewById<ImageView>(R.id.sport_card_icon)
        val sportSkillLevelText = view.findViewById<TextView>(R.id.skill_level_card_text)
        val sportGamesPlayed = view.findViewById<TextView>(R.id.games_played_text)
        val addButton = view.findViewById<TextView>(R.id.add_achievements)
        val newAchievement = view.findViewById<EditText>(R.id.new_achievement)
        sportName.text = Utils.formatString(sport.name.toString())
        sportIcon?.setImageResource(Sports.sportToIconDrawable(sport.name))
        // TODO: Doesn't work
        //sportSkillLevelText.setBackgroundColor(Skills.skillToColor(sport.skill))
        sportSkillLevelText.text = Utils.formatString(sport.skill.toString())
        sportGamesPlayed.text = String.format(
            v.context.resources.getString(R.string.games_played),
            sport.matchesPlayed
        )
        addButton.setOnClickListener {
            achievementAddCallback(sport, newAchievement.text.toString())
        }



        val achievementsRecyclerView = view.findViewById<RecyclerView>(R.id.achievements)
        achievementsRecyclerView.adapter = AchievementsAdapter(sport, 0, edit, achievementAddCallback, achievementRemoveCallback)
        achievementsRecyclerView.layoutManager = LinearLayoutManager(v.context).let {
            it.orientation = RecyclerView.HORIZONTAL
            it
        }

    }
}