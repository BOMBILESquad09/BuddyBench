package it.polito.mad.buddybench.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "court_facility",
    foreignKeys = [ForeignKey(
        entity = Court::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("court"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Facility::class,
        parentColumns = arrayOf("facility_id"),
        childColumns = arrayOf("facility"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
    )
data class CourtFacility (

    @PrimaryKey
    @ColumnInfo(name = "cf_id")
    val id: Int = 0,

    @ColumnInfo(name = "court")
    val courtId: Int = 0,

    @ColumnInfo(name = "facility")
    val facilityId: Int = 0,


)


