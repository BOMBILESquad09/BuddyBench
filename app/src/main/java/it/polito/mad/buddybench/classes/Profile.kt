package it.polito.mad.buddybench.classes



import android.animation.*
import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.size
import androidx.transition.Scene
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getBoolean
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getInt
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getJSONArray
import it.polito.mad.buddybench.classes.JSONUtils.Companion.getString
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Math.hypot
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class Profile(var name: String?, var surname: String?, var nickname: String?, var email: String, var location: String?, var birthdate: LocalDate, var reliability: Int, var imageUri: Uri?, var sports: MutableList<Sport>,
            var friends: MutableList<Profile>, val pendings: MutableList<Profile>,
            var isFriend: Boolean = false, var isPending: Boolean = false,
              var isRequesting: Boolean = false,
              ) {



    fun copy(): Profile{
        return Profile(this.name, this.surname, this.nickname, this.email, this.location, this.birthdate, this.reliability, this.imageUri, this.sports, this.friends, this.pendings, this.isFriend, this.isPending, this.isRequesting)
    }

    var age: Int = ChronoUnit.YEARS.between(birthdate, LocalDate.now()).toInt()
    var fullName = "$name $surname"

    fun getMatchesOrganized():Int{
        return sports.filter { it.skill != Skills.NULL }
            .fold(0) { a: Int, b: Sport -> a + b.matchesOrganized }
    }

    fun getMatchesPlayed():Int{
        return sports.filter { it.skill != Skills.NULL }
            .fold(0) { a: Int, b: Sport -> a + b.matchesPlayed }
    }


    companion object {

        fun fromJSON(jsonProfile: JSONObject): Profile{
            val name = jsonProfile.getString("name", "No name")
            val surname = jsonProfile.getString("surname", "No surname")
            val nickname = jsonProfile.getString("nickname", "No nickname")
            val email = jsonProfile.getString("email", "No email")
            val birthday = LocalDate.parse(jsonProfile.getString("birthdate", "27/04/1999"),  DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val location = jsonProfile.getString("location", "No location")
            val reliability = jsonProfile.getInt("reliability",0)
            val isPending = jsonProfile.getBoolean("isPending", false)
            val isFriend = jsonProfile.getBoolean("isFriend", false)
            val isRequesting = jsonProfile.getBoolean("isRequesting", false)
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
            return Profile(name, surname, nickname, email, location, birthday, reliability, imageUri,sports, mutableListOf(), mutableListOf(), isFriend = isFriend, isPending = isPending , isRequesting = isRequesting)
        }



        fun mockJSON(): String {
            return Profile("Vittorio", "Arpino","TheNextLayer", "varpino@buddybench.it", "Scafati", LocalDate.parse("27/04/1999", DateTimeFormatter.ofPattern("dd/MM/yyyy")), 0,
                null,
                mutableListOf(
                ),
            mutableListOf(), mutableListOf()
            ).toJSON().toString()
        }

        fun mockProfile(): Profile {
            return Profile("Vittorio", "Arpino","TheNextLayer", "varpino@buddybench.it", "Scafati", LocalDate.parse("27/04/1999", DateTimeFormatter.ofPattern("dd/MM/yyyy")), 70,
                null,
                mutableListOf(
                    Sport(Sports.TENNIS, Skills.SKILLED, 3, 2, mutableListOf("Coppa del Nonno")),
                    Sport(Sports.FOOTBALL, Skills.NEWBIE, 7, 6, mutableListOf("Coppa Campionii"))
                ),
                mutableListOf(), mutableListOf()
            )
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
        json.put("isFriend", isFriend)
        json.put("isRequesting", isRequesting)
        json.put("isPending", isPending)
        if (imageUri == null){
            json.put("imageUri", JSONObject.NULL)
        } else {
            json.put("imageUri", imageUri.toString())
        }
        json.put("sports", JSONArray(sports.map { it.toJSON() }))
        return json
    }

    fun toUserDto(): UserDTO {
        return UserDTO(
            name = this.name!!,
            surname = this.surname!!,
            nickname = this.nickname!!,
            birthdate = this.birthdate,
            location = this.location!!,
            email = this.email,
            imagePath = this.imageUri.toString(),
            reliability = this.reliability,
        )
    }


    fun getSportsEnum(): List<Sports> {
        val sportsList = mutableListOf<Sports>()
        for (sport in this.sports.filter { it.skill != Skills.NULL }) {
            sportsList.add(sport.name)
        }
        return sportsList
    }

}