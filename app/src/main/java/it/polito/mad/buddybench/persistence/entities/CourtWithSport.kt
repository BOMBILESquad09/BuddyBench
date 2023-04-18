package it.polito.mad.buddybench.persistence.entities

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.buddybench.persistence.dto.CourtDTO

data class CourtWithSport (

    @Embedded val court: Court,
    @Relation(
        parentColumn = "sport",
        entityColumn = "sport_name"
    )
    val sport: Sport
)

fun CourtWithSport.toCourtDTO(): CourtDTO {
    return CourtDTO(
        name = this.court.name,
        address = this.court.address,
        location = this.court.location,
        feeHour = this.court.feeHour,
        sport = this.sport.sport_name,
        path = this.court.path,
        feeEquipment = this.court.feeEquipment,
        rating = this.court.rating,
        nReviews = this.court.nReviews

    )
}
