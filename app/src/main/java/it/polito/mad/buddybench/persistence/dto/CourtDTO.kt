package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.persistence.entities.Court

class CourtDTO(
    val name: String,
    val address: String,
    val location: String,
    val feeHour: Int,
    val sport: String,
    val phoneNumber: String,
    val path: String,
    val feeEquipment: Int,
    val rating: Double,
    val nReviews: Int,
    val facilities: List<String>? = null
) {
    fun toEntity(): Court {
        return Court(

            name = this.name,
            address = this.address,
            location = this.location,
            feeHour = this.feeHour,
            sport = this.sport,
            phoneNumber = this.phoneNumber,
            path = this.path,
            feeEquipment = this.feeEquipment,
            rating = this.rating,
            nReviews = this.nReviews

        )
    }

}




