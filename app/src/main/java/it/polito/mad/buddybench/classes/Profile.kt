package it.polito.mad.buddybench.classes



import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getInt
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getJSONArray
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getString
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class Profile(var name: String?, var surname: String?, var nickname: String?, var email: String, var location: String?, var birthdate: LocalDate, var reliability: Int, var imageUri: Uri?, var sports: List<Sport> ) {
    var matchesPlayed:Int = sports.fold(0){a: Int, b: Sport -> a + b.matchesPlayed }
    var age:Int = ChronoUnit.YEARS.between(birthdate, LocalDate.now()).toInt()
    var matchesOrganized: Int = sports.fold(0){a:Int, b: Sport -> a + b.matchesOrganized}
    var fullName = "$name $surname"
    companion object {

        fun fromJSON(jsonProfile: JSONObject): Profile{
            val name = jsonProfile.getString("name", "No name")
            val surname = jsonProfile.getString("surname", "No surname")
            val nickname = jsonProfile.getString("nickname", "No nickname")
            val email = jsonProfile.getString("email", "No email")
            val birthday = LocalDate.parse(jsonProfile.getString("birthdate", "27/04/1999"),  DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val location = jsonProfile.getString("location", "No location")
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
            return Profile(name, surname, nickname, email, location, birthday, reliability, imageUri,sports)
        }



        fun mockJSON(): String {
            return Profile("Vittorio", "Arpino","TheNextLayer", "varpino@buddybench.it", "Scafati", LocalDate.parse("27/04/1999", DateTimeFormatter.ofPattern("dd/MM/yyyy")), 70,
                null,
                listOf(
                    Sport(Sports.TENNIS, Skills.SKILLED, 3, 2),
                    Sport(Sports.FOOTBALL, Skills.NEWBIE, 7, 6)
                )).toJSON().toString()
        }


    }

    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("name", name)
        json.put("surname",surname)
        json.put("nickname", nickname)
        json.put("location", location)
        json.put("email", email)
        json.put("birthdate", birthdate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
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




    fun getSportsEnum(): List<Sports> {
        val sportsList = mutableListOf<Sports>()
        println("getSportsEnum (this.sports): ${this.sports}")
        for (sport in this.sports) {
            println("getSportsEnum: $sport")
            sportsList.add(sport.name)
        }
        return sportsList
    }

    fun updateSkillLevel(sport: Sport, skill: Skills) {
        this.sports.find { it.name == sport.name }?.skill = skill
    }

    //TODO: BETTER TO MOVE THE FOLLOWING METHODS IN ANOTHER FILE

    /**
     * Adds sport cards to the profile section relative to the profile.sports values
     * The context (activity) and the container layout are passed as parameters
     * @param context The Activity or context in which the function is called
     * @param sportContainer The container layout in which to add the cards
     */
    fun populateSportCards(context: AppCompatActivity, sportContainer: LinearLayout) {
        sportContainer.removeAllViews()
        if (this.sports.isEmpty()) {
            val emptySportsText = TextView(context)
            emptySportsText.text = context.getString(R.string.no_sports)
            sportContainer.addView(emptySportsText)
            emptySportsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            return
        }

        for (sport in this.sports) {
            val sportCard = LayoutInflater.from(context).inflate(R.layout.card_sport, null, false)

            // ** Sport card dynamic values
            val sportName = sportCard.findViewById<TextView>(R.id.sport_card_name)
            val sportIcon = sportCard.findViewById<ImageView>(R.id.sport_card_icon)
            val sportSkillLevelText = sportCard.findViewById<TextView>(R.id.skill_level_card_text)
            val sportGamesPlayed = sportCard.findViewById<TextView>(R.id.games_played_text)

            sportName.text = Utils.formatString(sport.name.toString())
            sportIcon.setImageResource(Sports.sportToIconDrawable(sport.name))
            // TODO: Doesn't work
            // sportSkillLevel.setBackgroundColor(Skills.skillToColor(sport.skill))
            sportSkillLevelText.text = Utils.formatString(sport.skill.toString())
            sportGamesPlayed.text = String.format(context.resources.getString(R.string.games_played), sport.matchesPlayed)

            // ** Add card to container
            sportContainer.addView(sportCard)
        }
    }



}