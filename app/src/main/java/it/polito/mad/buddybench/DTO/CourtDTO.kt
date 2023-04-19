package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.Entities.Court
import it.polito.mad.buddybench.enums.Sports

class CourtDTO(val name: String, val address: String, val feeHour: Int, val sport: String) {
    fun toEntity(): Court {
        return Court(
            name = this.name,
            address = this.address,
            feeHour = this.feeHour,
            sport = this.sport
        )
    }

}




