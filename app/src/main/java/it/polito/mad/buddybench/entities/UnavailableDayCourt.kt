package it.polito.mad.buddybench.entities

import java.time.LocalDate



import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "unavailable_court_date", primaryKeys = arrayOf("id", "date") ,foreignKeys = arrayOf(
        ForeignKey(
            entity = Court::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class UnavailableDayCourt(

    @ColumnInfo(name = "id")
    val court: Int,

    @ColumnInfo(name = "date")
    val date: String,
)