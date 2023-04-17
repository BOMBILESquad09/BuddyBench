package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Sport

class SportDTO(sportName: String) {

    val sportName = sportName

}

fun SportDTO.toEntity(): Sport {
    return Sport (
        sportName = this.sportName
    )
}