package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Sport
import it.polito.mad.buddybench.Entities.UserSport

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