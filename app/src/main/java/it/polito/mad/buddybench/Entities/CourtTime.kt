package it.polito.mad.buddybench.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "CourtTime", foreignKeys = arrayOf(
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

    @ColumnInfo(name = "openingTime")
    val openingTime: String,

    @ColumnInfo(name = "closingTime")
    val closingTime: String,

    @ColumnInfo(name = "dayOfWeek")
    val dayOfWeek: String,

    )