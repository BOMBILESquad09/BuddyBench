package it.polito.mad.buddybench.classes


import android.content.Context
import android.net.Uri
import android.provider.CalendarContract.EventDays
import android.provider.ContactsContract.CommonDataKinds.Email
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
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getInt
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getString
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getJSONArray
import java.text.DateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import java.util.Calendar


class Profile(var fullName: String?, var nickname: String?, var email: String, var location: String?, var birthday: LocalDate, var matchesOrganized: Int?, var reliability: Int, var imageUri: Uri?, var sports: List<Sport> ) {
    var matchesPlayed:Int = sports.fold(0){a: Int, b: Sport -> a + b.matchesPlayed }
    var age:Int = ChronoUnit.YEARS.between(birthday, LocalDate.now()).toInt()
    companion object {

        fun fromJSON(jsonProfile: JSONObject): Profile{
            val fullName = jsonProfile.getString("fullName", "No name")
            val nickname = jsonProfile.getString("nickname", "No nickname")
            val email = jsonProfile.getString("email", "No email")
            val birthday = LocalDate.parse(jsonProfile.getString("birthday", "27/04/1999"),  DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val location = jsonProfile.getString("location", "No location")
            val matchesOrganized = jsonProfile.getInt("matchesOrganized",0)
            val reliability = jsonProfile.getInt("reliability",0)
            var imageUri:Uri? = null
            try {
                imageUri = Uri.parse(jsonProfile.getString("imageUri"))
            } catch (_: JSONException){
            }
            val sportsList = jsonProfile.getJSONArray("sports", JSONArray())
            val sports = mutableListOf<Sport>()
            for(i in 0 until sportsList.length()) {
                val jsonSport = sportsList.getJSONObject(i)
                sports.add(Sport.fromJSON(jsonSport))
            }
            return Profile(fullName, nickname, email, location, birthday, matchesOrganized, reliability, imageUri,sports)
        }



        fun mockJSON(): String {
            return Profile("Vittorio", "Arpino", "varpino@buddybench.it", "Scafati", LocalDate.parse("27/04/1999", DateTimeFormatter.ofPattern("dd/MM/yyyy")), 10, 70,
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
        json.put("email", email)
        json.put("birthday", birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        json.put("matchesOrganized", matchesOrganized)
        json.put("reliability", reliability)
        if (imageUri == null){
           json.put("imageUri", JSONObject.NULL)
        } else {
            println("Putting the image in Json")
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