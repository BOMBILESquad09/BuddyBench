package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.UserSport

class UserSportDTO(user: Int, skill: String, gamesPlayed: Int, gamesOrganized: Int, sport: Int) {

    val user = user
    val skill = skill
    val gamesPlayed = gamesPlayed
    val gamesOrganized = gamesOrganized
    val sport = sport

}

fun UserSportDTO.toEntity(): UserSport {
    return UserSport(
        user = this.user,
        skill = this.skill,
        gamesPlayed = this.gamesPlayed,
        gamesOrganized = this.gamesOrganized,
        sport = this.sport,
    )
}