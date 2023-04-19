package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Sport
import it.polito.mad.buddybench.Entities.User
import it.polito.mad.buddybench.Entities.UserSport
import it.polito.mad.buddybench.Entities.toUserDTO
import java.time.LocalDate

class UserSportDTO(
    user: User,
    skill: String,
    gamesPlayed: Int,
    gamesOrganized: Int,
    sport: String
) {

    val user = user.toUserDTO()
    val skill = skill
    val gamesPlayed = gamesPlayed
    val gamesOrganized = gamesOrganized
    val sport = sport

}

fun UserSportDTO.toEntity(userId: Int, sport: String): UserSport {
    return UserSport(
        user = userId,
        skill = this.skill,
        gamesPlayed = this.gamesPlayed,
        gamesOrganized = this.gamesOrganized,
        sport = sport,
    )
}