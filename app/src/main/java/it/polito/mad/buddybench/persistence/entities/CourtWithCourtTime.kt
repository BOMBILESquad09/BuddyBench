package it.polito.mad.buddybench.persistence.entities

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.buddybench.persistence.dto.CourtTimeDTO
import java.time.DayOfWeek
import java.time.LocalTime


data class CourtWithCourtTime(

    @Embedded val courtTime: CourtTime,

    @Relation(
        parentColumn = "court",
        entityColumn = "id",
    )
    val court: Court,

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

