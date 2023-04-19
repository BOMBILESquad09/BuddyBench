package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.Entities.Court
import it.polito.mad.buddybench.enums.Sports

class CourtDTO(val courtName: String, val address: String, val feeHour: Int, val sport: String) {



    fun toEntity(sportId: Int): Court {
        return Court(
            courtName = this.courtName,
            address = this.address,
            feeHour = this.feeHour,
            sport = sportId
        )
    }
}

