package it.polito.mad.buddybench.Entities

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.buddybench.DTO.CourtDTO

data class CourtWithSport (

    @Embedded val court: Court,
    @Relation(
        parentColumn = "sport",
        entityColumn = "name"
    )
    val sport: Sport
)

fun CourtWithSport.toCourtDTO(): CourtDTO {
    return CourtDTO(
        name = this.court.name,
        address = this.court.address,
        location = this.court.location,
        feeHour = this.court.feeHour,
        sport = this.sport.name
    )
}
