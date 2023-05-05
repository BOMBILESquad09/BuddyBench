package it.polito.mad.buddybench.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.persistence.dto.SportDTO

@Entity(tableName = "facility")
data class Facility (

    @PrimaryKey
    @ColumnInfo(name = "facility_id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String = "",
)

