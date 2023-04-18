package it.polito.mad.buddybench.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.DTO.ReservationDTO
import it.polito.mad.buddybench.DTO.SportDTO
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "Sport")
data class Sport (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "sportName")
    val sportName: String

)

fun Sport.toSportDTO(): SportDTO {
    return SportDTO(
        sportName = sportName
    )
}