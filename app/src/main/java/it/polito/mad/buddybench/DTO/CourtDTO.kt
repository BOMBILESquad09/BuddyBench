package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.Entities.Court
import it.polito.mad.buddybench.enums.Sports

class CourtDTO(courtName: String, address: String, feeHour: Int, sport: String) {

    val courtName = courtName
    val address = address
    val feeHour = feeHour
    val sport = sport

}

fun CourtDTO.toEntity(sportId: Int): Court {
    return Court(
        courtName = this.courtName,
        address = this.address,
        feeHour = this.feeHour,
        sport = sportId
    )
}