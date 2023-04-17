package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.Entities.Court

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