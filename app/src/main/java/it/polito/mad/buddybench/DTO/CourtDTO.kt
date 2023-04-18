package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.Entities.Court

class CourtDTO(courtName: String, address: String, feeHour: Int, sport: Int) {

    val courtName = courtName
    val address = address
    val feeHour = feeHour
    val sport = sport

}

data class CourtCompleteDTO (

    val courtName: String,
    val address: String,
    val feeHour: Int,
    val sport: String

)

fun CourtCompleteDTO.toEntity(mappingSports: Map<String, Int>): Court {

    return Court(
        courtName = this.courtName,
        address = this.address,
        feeHour = this.feeHour,
        sport = mappingSports[this.sport]!!
    )

}

fun CourtDTO.toEntity(): Court {
    return Court(
        courtName = this.courtName,
        address = this.address,
        feeHour = this.feeHour,
        sport = this.sport
    )
}