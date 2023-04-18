package it.polito.mad.buddybench.Entities

import androidx.room.*
import it.polito.mad.buddybench.DTO.CourtDTO
import it.polito.mad.buddybench.DTO.CourtTimeDTO
import java.time.DayOfWeek
import java.time.LocalDate
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

fun CourtWithCourtTime.toCourtTimeDTO(): CourtTimeDTO {
    return CourtTimeDTO(
        courtName = this.court.name,
        sport = this.court.sport,
        address = this.court.address,
        feeHour = this.court.feeHour,
        openingTime = LocalTime.of(this.courtTime.openingTime,0),
        closingTime = LocalTime.of(this.courtTime.closingTime,0),
        dayOfWeek = DayOfWeek.of(this.courtTime.dayOfWeek)
    )
}

data class CourtWithCourtTime(

    @Embedded val courtTime: CourtTime,

    @Relation(
        parentColumn = "court",
        entityColumn = "id",
    )
    val court: Court,



)