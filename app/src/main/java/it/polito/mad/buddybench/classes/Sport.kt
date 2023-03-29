package it.polito.mad.buddybench.classes

import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import org.json.JSONObject

class Sport(val name: Sports, var skill: Skills, val matchesPlayed: Int): java.io.Serializable {
    companion object{
        fun fromJSON(jsonSport: JSONObject): Sport {
            val name = jsonSport.get("name").toString()
            val sportName = Sports.fromJSON(name)

            checkNotNull(sportName) {
                throw Error("Error parsing JSON sports name")
            }

            val skill = jsonSport.get("skill").toString()
            val skillName = Skills.fromJSON(skill)

            checkNotNull(skillName) {
                throw Error("Error parsing JSON skill name")
            }

            val matchesPlayed = jsonSport.getInt("matchesPlayed")

            return Sport(sportName, skillName, matchesPlayed)
        }
    }

    fun toJSON(): JSONObject{
        val json = JSONObject()
        json.put("name", Sports.toJSON(name))
        json.put("skill", Skills.toJSON(skill))
        json.put("matchesPlayed", matchesPlayed)
        return json
    }
}