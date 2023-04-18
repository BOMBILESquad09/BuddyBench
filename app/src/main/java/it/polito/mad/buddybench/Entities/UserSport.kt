package it.polito.mad.buddybench.Entities

import androidx.room.*
import it.polito.mad.buddybench.DTO.ReservationDTO
import it.polito.mad.buddybench.DTO.UserSportDTO
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "user_sport", foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("user"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sport::class,
            parentColumns = arrayOf("name"),
            childColumns = arrayOf("sport"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    )
)

data class UserSport(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user")
    val user: Int,

    @ColumnInfo(name = "skill")
    val skill: String,

    @ColumnInfo(name = "games_played")
    val gamesPlayed: Int,

    @ColumnInfo(name = "games_organized")
    val gamesOrganized: Int,

    @ColumnInfo(name = "sport")
    val sport: String

)

data class UserSportsWithUserAndSport(
    @Embedded val userSport: UserSport,
    @Relation(
        parentColumn = "user",
        entityColumn = "id"
    )
    val user: User,

    @Relation(
        parentColumn = "sport",
        entityColumn = "name"
    )
    val sport: Sport,
)

fun UserSportsWithUserAndSport.toUserSportDTO(): UserSportDTO {
    return UserSportDTO(
        user = user,
        skill = this.userSport.skill,
        gamesOrganized = this.userSport.gamesOrganized,
        gamesPlayed = this.userSport.gamesPlayed,
        sport = this.sport.name,

        )
}

