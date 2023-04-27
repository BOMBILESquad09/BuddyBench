package it.polito.mad.buddybench.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.persistence.dto.SportDTO

@Entity(tableName = "sport")
data class Sport (

    @PrimaryKey
    val sport_name: String,


)

fun Sport.toSportDTO(): SportDTO {
    return SportDTO(
        name = sport_name
    )
}

