package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.Court

class CourtDTO(courtName: String, address: String, feeHour: Int, sport: Int) {

    val courtName = courtName
    val address = address
    val feeHour = feeHour
    val sport = sport

}

fun CourtDTO.toEntity(): Court {
    return Court(
        courtName = this.courtName,
        address = this.address,
        feeHour = this.feeHour,
        sport = this.sport
    )
}