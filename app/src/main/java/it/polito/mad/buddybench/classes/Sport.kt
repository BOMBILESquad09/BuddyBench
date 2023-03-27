package it.polito.mad.buddybench.classes

import org.json.JSONObject

class Sport(val name: String, var skill: Int, val matchesPlayed: Int) {
    companion object{
        fun fromJSON(jsonSport: JSONObject): Sport{
            val name = jsonSport.getString("name")
            val skill = jsonSport.getInt("skill")
            val matchesPlayed = jsonSport.getInt("matchesPlayed")
            return Sport(name, skill, matchesPlayed)
        }
    }

    fun toJSON(): JSONObject{
        val json = JSONObject()
        json.put("name", name)
        json.put("skill", skill)
        json.put("matchesPlayed", matchesPlayed)
        return json
    }
}