package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.persistence.entities.User
import it.polito.mad.buddybench.persistence.entities.UserSport

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