package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.UserSport

class UserSportDTO(user: Int, skill: String, gamesPlayed: Int, gamesOrganized: Int, sport: Int) {

class UserSportDTO(
    val user: User,
    val skill: String,
    val gamesPlayed: Int,
    val gamesOrganized: Int,
    val sport: String
) {
    fun toEntity(userId: Int): UserSport {
        return UserSport(
            user = userId,
            skill = this.skill,
            gamesPlayed = this.gamesPlayed,
            gamesOrganized = this.gamesOrganized,
            sport = this.sport,
        )
    }


}