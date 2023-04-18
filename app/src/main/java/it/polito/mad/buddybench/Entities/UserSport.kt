package it.polito.mad.buddybench.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.DTO.ReservationDTO
import it.polito.mad.buddybench.DTO.UserSportDTO
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "UserSport", foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("user"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sport::class,
            parentColumns = arrayOf("id"),
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

    @ColumnInfo(name = "gamesPlayed")
    val gamesPlayed: Int,

    @ColumnInfo(name = "gamesOrganized")
    val gamesOrganized: Int,

    @ColumnInfo(name = "sport")
    val sport: Int

    )

fun UserSport.toUserSportDTO(): UserSportDTO {
    return UserSportDTO(
        user = this.user,
        skill = this.skill,
        gamesOrganized = this.gamesOrganized,
        gamesPlayed = this.gamesPlayed,
        sport = this.sport,

    )
}