package it.polito.mad.buddybench.classes

import android.net.Uri
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import org.json.JSONArray
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
            val imageFile = File(jsonProfile.getString("imageUri"))
            var imageUri: Uri? = null
            if (imageFile.exists()){
                imageUri = Uri.fromFile(imageFile)
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
                Uri.fromFile(File("")),
                listOf(
                    Sport(Sports.TENNIS, Skills.SKILLED, 3),
                    Sport(Sports.FOOTBALL, Skills.NEWBIE, 17)
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
        json.put("imageUri", imageUri.toString())
        json.put("sports", JSONArray(sports.map { it.toJSON() }))
        return json
    }




}