package it.polito.mad.buddybench.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.dto.SportDTO

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