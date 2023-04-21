package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.Court

class CourtDTO(courtName: String, address: String, feeHour: Int, sport: Int) {

class CourtDTO(val name: String, val address: String, val location: String, val feeHour: Int, val sport: String) {
    fun toEntity(): Court {
        return Court(

            name = this.name,
            address = this.address,
            location = this.location,
            feeHour = this.feeHour,
            sport = this.sport
        )
    }

}




