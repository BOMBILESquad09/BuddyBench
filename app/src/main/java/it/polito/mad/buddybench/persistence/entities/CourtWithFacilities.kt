package it.polito.mad.buddybench.persistence.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import it.polito.mad.buddybench.persistence.dto.CourtDTO


data class CourtWithFacilities(

    @Embedded val court: Court,

    @Relation(
        parentColumn = "id",
        entity = Facility::class,
        entityColumn = "facility_id",
        associateBy = Junction(
            value = CourtFacility::class,
            parentColumn = "court",
            entityColumn = "facility"
        )
    )
    val facilities: List<Facility>?

)


fun CourtWithFacilities.toCourtDTO(): CourtDTO {
    return CourtDTO(
        name = this.court.name,
        address = this.court.address,
        location = this.court.location,
        feeHour = this.court.feeHour,
        sport = this.court.sport,
        path = this.court.path,
        feeEquipment = this.court.feeEquipment,
        rating = this.court.rating,
        nReviews = this.court.nReviews,
        facilities = this.facilities?.map { it.name }
    )
}