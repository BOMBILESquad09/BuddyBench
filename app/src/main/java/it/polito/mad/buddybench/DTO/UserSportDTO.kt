package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Sport
import it.polito.mad.buddybench.Entities.User
import it.polito.mad.buddybench.Entities.UserSport
import it.polito.mad.buddybench.Entities.toUserDTO
import java.time.LocalDate

class UserSportDTO(
    val user: User,
    val skill: String,
    val gamesPlayed: Int,
    val gamesOrganized: Int,
    val sport: String
) {
    fun toEntity(userId: Int, sportId: Int): UserSport {
        return UserSport(
            user = userId,
            skill = this.skill,
            gamesPlayed = this.gamesPlayed,
            gamesOrganized = this.gamesOrganized,
            sport = sportId,
        )
    }


}

