package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Sport

class SportDTO(val name: String) {


    fun toEntity(): Sport {
        return Sport (
            name = this.name
        )
    }
}

