package it.polito.mad.buddybench.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "Reservation", foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userOrganizer"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Court::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("court"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Reservation(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "userOrganizer")
    val userOrganizer: Int,

    @ColumnInfo(name = "court")
    val court: Int,

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "startTime")
    val startTime: LocalTime,

    @ColumnInfo(name = "endTime")
    val endTime: LocalTime,

    )