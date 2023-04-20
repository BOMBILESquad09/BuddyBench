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

