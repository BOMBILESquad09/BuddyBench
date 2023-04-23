package it.polito.mad.buddybench.entities

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.buddybench.dto.UserSportDTO

data class UserSportsWithUserAndSport(
    @Embedded val userSport: UserSport,
    @Relation(
        parentColumn = "user",
        entityColumn = "id"
    )
    val user: User,

    @Relation(
        parentColumn = "sport",
        entityColumn = "sport_name"
    )
    val sport: Sport,
)

fun UserSportsWithUserAndSport.toUserSportDTO(): UserSportDTO {
    return UserSportDTO(
        user = user,
        skill = this.userSport.skill,
        gamesOrganized = this.userSport.gamesOrganized,
        gamesPlayed = this.userSport.gamesPlayed,
        sport = this.sport.sport_name,

        )
}