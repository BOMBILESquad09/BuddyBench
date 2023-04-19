package it.polito.mad.buddybench.Entities

import androidx.room.*
import it.polito.mad.buddybench.DTO.CourtDTO
import it.polito.mad.buddybench.DTO.CourtTimeDTO
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

fun CourtTimeWithCourt.toCourtTimeDTO(): CourtTimeDTO {
    return CourtTimeDTO(
        courtName = this.court.name,
        sport = this.sport.name,
        address = this.court.address,
        feeHour = this.court.feeHour,
        openingTime = LocalTime.parse(this.courtTime.openingTime),
        closingTime = LocalTime.parse(this.courtTime.closingTime),
        dayOfWeek = this.courtTime.dayOfWeek
    )
}

data class CourtTimeWithCourt(

    @Embedded val courtTime: CourtTime,

    @Relation(
        parentColumn = "court",
        entityColumn = "id",
    )
    val court: Court,

    @Relation(
        parentColumn = "court",
        entityColumn = "id",
        associateBy = Junction (
            value = Court::class,
            parentColumn = "sport",
            entityColumn = "id"
        )
    )
    val sport: Sport

)