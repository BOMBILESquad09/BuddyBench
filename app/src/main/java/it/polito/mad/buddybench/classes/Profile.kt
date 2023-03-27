package it.polito.mad.buddybench.classes

import org.json.JSONArray
import org.json.JSONObject


class Profile(var fullName: String?, var nickname: String?, var location: String?, var age: Int?, var matchesOrganized: Int?, var matchesPlayed: Int?, var reliability: Int, var sports: List<Sport> ) {

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
            val sportsList = jsonProfile.getJSONArray("sports")
            val sports = mutableListOf<Sport>()
            for(i in 0 until sportsList.length()) {
                val jsonSport = sportsList.getJSONObject(i)

                sports.add(Sport.fromJSON(jsonSport))
            }
            return Profile(fullName, nickname, location, age, matchesOrganized, matchesPlayed, reliability, sports)
        }



        fun mockJSON(): String{
            return Profile("Vittorio", "Arpino", "Scafati", 23, 10, 10, 70, listOf(Sport("Tennis", 5, 3))).toJSON().toString()
        }
    }

    fun toJSON(): JSONObject{
        val json = JSONObject()
        json.put("fullName", fullName)
        json.put("nickname", nickname)
        json.put("location", location)
        json.put("age", age)
        json.put("matchesOrganized", matchesOrganized)
        json.put("matchesPlayed", matchesPlayed)
        json.put("reliability", reliability)
        json.put("sports", JSONArray(sports.map { it.toJSON() }))
        return json
    }




}