package it.polito.mad.buddybench.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.dto.SportDTO

@Entity(tableName = "sport")
data class Sport (

    @PrimaryKey
    val name: String,


)

fun Sport.toSportDTO(): SportDTO {
    return SportDTO(
        name = name
    )
}