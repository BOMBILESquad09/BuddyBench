package it.polito.mad.buddybench.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "court_time", foreignKeys = arrayOf(
        ForeignKey(
            entity = Court::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("court"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class CourtTime(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "court")
    val court: Int,

    @ColumnInfo(name = "opening_time")
    val openingTime: Int,

    @ColumnInfo(name = "closing_time")
    val closingTime: Int,

    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: Int,

    )