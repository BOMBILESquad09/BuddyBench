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
        this.court.name,
        this.court.address,
        this.court.location,
        this.court.feeHour,
        this.sport.sport_name,
        this.court.phoneNumber,
        this.court.path,
        this.court.feeEquipment,
        this.court.rating,
        this.court.nReviews,
        listOf()

    )
}
