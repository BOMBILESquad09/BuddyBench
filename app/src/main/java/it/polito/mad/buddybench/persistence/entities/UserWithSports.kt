package it.polito.mad.buddybench.persistence.entities

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports


data class UserWithSports(

    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "user"
    )
    val sports: List<UserSport>,
)

fun UserWithSports.toUserSportDTO(): UserWithSportsDTO {
    return UserWithSportsDTO(
        user = user.toUserDTO(),
        sports = sports.map {
            Sport(
                Sports.fromJSON(it.sport)!!,
                Skills.valueOf(it.skill.uppercase()),
                it.gamesPlayed,
                it.gamesOrganized
            )
        }
        )
}

data class UserWithSportsDTO(
    val user: UserDTO,
    val sports: List<Sport>
)

fun UserWithSportsDTO.toEntity(): UserWithSports{
    return UserWithSports(
        user = this.user.toEntity(),
        sports = this.sports.map {
            UserSport(
                skill = it.skill.name,
                gamesPlayed = it.matchesPlayed,
                gamesOrganized = it.matchesOrganized,
                sport = it.name.name
            )
        }
    )
}