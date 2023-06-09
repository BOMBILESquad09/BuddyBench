package it.polito.mad.buddybench.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.UserSportDTO

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
            parentColumns = arrayOf("sport_name"),
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
    val user: Int = 0,

    @ColumnInfo(name = "skill")
    val skill: String,

    @ColumnInfo(name = "games_played")
    val gamesPlayed: Int,

    @ColumnInfo(name = "games_organized")
    val gamesOrganized: Int,

    @ColumnInfo(name = "sport")
    val sport: String,

    @ColumnInfo(name = "achievements")
    val achievements: String

)



