package it.polito.mad.buddybench.activities.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
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
        val achievementButton = v.findViewById<ImageView>(R.id.achievements_button)


        val backgroundDrawable = GradientDrawable()
        backgroundDrawable.setColor(Sports.getSportColor(sport.name, v.context))
        backgroundDrawable.shape = GradientDrawable.OVAL
        val shapeDrawable = ShapeDrawable(OvalShape())
        shapeDrawable.paint.color = Color.TRANSPARENT
        shapeDrawable.paint.style = Paint.Style.STROKE
        shapeDrawable.paint.strokeWidth = 2f
        shapeDrawable.paint.isAntiAlias = true
        val layerDrawable = LayerDrawable(arrayOf(backgroundDrawable, shapeDrawable))
        layerDrawable.setLayerInset(1, 0, 0, 0, 0)
        achievementButton.background = layerDrawable

        achievementButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(v.context)
            val dialogCard = LayoutInflater.from(v.context).inflate(R.layout.card_sport_expanded, null)
            setAchievementCard(dialogCard, sport, edit, )
            builder.setView(dialogCard)
            val dialog: AlertDialog = builder.create()
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            dialog.show()

        }


        if(edit){
            v.findViewById<ImageView>(R.id.close_button).setOnClickListener {
                sportRemoveCallback(sport)
            }
            val sportSkillLevel = v.findViewById<CardView>(R.id.skill_level_card)

            sportSkillLevel.setOnClickListener {
                sportSkillCallback(sport.copy(), sportSkillLevel)
            }
        }

    }


    private fun setAchievementCard(view: View, sport: Sport, edit: Boolean){

        val cardLayout = view.findViewById<LinearLayout>(R.id.card_layout)
        val sportIcon = view.findViewById<ImageView>(R.id.sport_card_icon)
        val addButton = view.findViewById<TextView>(R.id.add_achievements)
        val newAchievement = view.findViewById<EditText>(R.id.new_achievement)
        val noAchivements = view.findViewById<TextView>(R.id.no_achivements)

        cardLayout.setBackgroundColor(Sports.getSportColor(sport.name, v.context))
        sportIcon?.setImageResource(Sports.sportToIconDrawableAlternative(sport.name))
        // TODO: Doesn't work
        //sportSkillLevelText.setBackgroundColor(Skills.skillToColor(sport.skill))
        //sportSkillLevelText.text = Utils.formatString(sport.skill.toString())

        println("-----------achievements-------------------")
        println(sport.achievements.size)
        println("--------------------------------------------------")

        val achievementsRecyclerView = view.findViewById<RecyclerView>(R.id.achievements)
        achievementsRecyclerView.adapter = AchievementsAdapter(sport, 0, edit, achievementAddCallback, achievementRemoveCallback)
        achievementsRecyclerView.layoutManager = LinearLayoutManager(v.context).let {
            it.orientation = RecyclerView.VERTICAL
            it
        }

        if(sport.achievements.isEmpty() && !edit) {
            achievementsRecyclerView.visibility = View.GONE
            noAchivements.visibility = View.VISIBLE
        }

        if(sport.achievements.isNotEmpty() && !edit) {
            achievementsRecyclerView.visibility = View.VISIBLE
            noAchivements.visibility = View.GONE
        }

        addButton.setOnClickListener {
            if (newAchievement.text.toString().trim().isNotEmpty()){
                val lastAchievement = sport.achievements.map { it }
                achievementAddCallback(sport, newAchievement.text.toString())
                val diffUtils = AchievementDiffUtils(lastAchievement, sport.achievements)
                val diffResult = DiffUtil.calculateDiff(diffUtils)
                diffResult.dispatchUpdatesTo(achievementsRecyclerView.adapter!!)
                newAchievement.setText("")

            }
        }

        if(!edit){
            newAchievement.visibility = View.GONE
            addButton.visibility = View.GONE
        }

    }
}