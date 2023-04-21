package it.polito.mad.buddybench.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.dto.CourtTimeDTO
import java.time.LocalTime

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

fun CourtTime.toCourtTimeDTO(): CourtTimeDTO {
    return CourtTimeDTO(
        court = this.court,
        openingTime = LocalTime.parse(this.openingTime),
        closingTime = LocalTime.parse(this.closingTime),
        dayOfWeek = this.dayOfWeek
    )
}
