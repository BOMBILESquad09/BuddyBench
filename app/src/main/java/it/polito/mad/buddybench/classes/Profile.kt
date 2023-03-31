package it.polito.mad.buddybench.classes


import android.content.Context
import android.net.Uri
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class Profile(var fullName: String?, var nickname: String?, var location: String?, var age: Int?, var matchesOrganized: Int?, var matchesPlayed: Int?, var reliability: Int, var imageUri: Uri?, var sports: List<Sport> ): java.io.Serializable {

    companion object {
        fun fromJSON(jsonProfile: JSONObject): Profile{
            println(jsonProfile.toString())
            val fullName = jsonProfile.getString("fullName")
            val nickname = jsonProfile.getString("nickname")
            val location = jsonProfile.getString("location")
            val age = jsonProfile.getInt("age")
            val matchesOrganized = jsonProfile.getInt("matchesOrganized")
            val matchesPlayed = jsonProfile.getInt("matchesPlayed")
            val reliability = jsonProfile.getInt("reliability")
            var imageUri:Uri? = null
            try {
                imageUri = Uri.parse(jsonProfile.getString("imageUri"))
            } catch (_: JSONException){
            }
            val sportsList = jsonProfile.getJSONArray("sports")

            val sports = mutableListOf<Sport>()
            for(i in 0 until sportsList.length()) {
                val jsonSport = sportsList.getJSONObject(i)
                sports.add(Sport.fromJSON(jsonSport))
            }
            return Profile(fullName, nickname, location, age, matchesOrganized, matchesPlayed, reliability, imageUri,sports)
        }



        fun mockJSON(): String {
            return Profile("Vittorio", "Arpino", "Scafati", 23, 10, 10, 70,
                null,
                listOf(
                    Sport(Sports.TENNIS, Skills.SKILLED, 3),
                    Sport(Sports.FOOTBALL, Skills.NEWBIE, 7)
                )).toJSON().toString()
        }
    }

    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("fullName", fullName)
        json.put("nickname", nickname)
        json.put("location", location)
        json.put("age", age)
        json.put("matchesOrganized", matchesOrganized)
        json.put("matchesPlayed", matchesPlayed)
        json.put("reliability", reliability)
        if (imageUri == null){
           json.put("imageUri", JSONObject.NULL)
        } else {
            json.put("imageUri", imageUri.toString())
        }
        json.put("sports", JSONArray(sports.map { it.toJSON() }))
        return json
    }


    /**
     * Adds sport cards to the profile section relative to the profile.sports values
     * The context (activity) and the container layout are passed as parameters
     * @param context The Activity or context in which the function is called
     * @param sportContainer The container layout in which to add the cards
     */
    fun populateSportCards(context: AppCompatActivity, sportContainer: LinearLayout) {
        if (this.sports.isEmpty()) {
            val emptySportsText = TextView(context)
            emptySportsText.text = context.getString(R.string.no_sports)
            sportContainer.addView(emptySportsText)

            return
        }
        
        for (sport in this.sports) {
            val sportCard = LayoutInflater.from(context).inflate(R.layout.card_sport, null, false);

            // ** Sport card dynamic values
            val sportName = sportCard.findViewById<TextView>(R.id.sport_card_name);
            val sportIcon = sportCard.findViewById<ImageView>(R.id.sport_card_icon);
            val sportSkillLevel = sportCard.findViewById<CardView>(R.id.skill_level_card)
            val sportSkillLevelText = sportCard.findViewById<TextView>(R.id.skill_level_card_text)
            val sportGamesPlayed = sportCard.findViewById<TextView>(R.id.games_played_text)

            sportName.text = Utils.capitalize(sport.name.toString())
            sportIcon.setImageResource(Sports.sportToIconDrawable(sport.name))
            // TODO: Non funziona
            // sportSkillLevel.setBackgroundColor(Skills.skillToColor(sport.skill))
            sportSkillLevelText.text = Utils.capitalize(sport.skill.toString())
            sportGamesPlayed.text = String.format(context.resources.getString(R.string.games_played), sport.matchesPlayed)

            // ** Add card to container
            sportContainer.addView(sportCard)   
        }
    }

}