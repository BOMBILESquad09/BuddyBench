package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.Court

class CourtDTO(
    val name: String,
    val address: String,
    val location: String,
    val feeHour: Int,
    val sport: String,
    val path: String,
    val fee_equipment: Int
) {
    fun toEntity(): Court {
        return Court(

            name = this.name,
            address = this.address,
            location = this.location,
            feeHour = this.feeHour,
            sport = this.sport,
            path = this.path,
            fee_equipment = fee_equipment
        )
    }

}




