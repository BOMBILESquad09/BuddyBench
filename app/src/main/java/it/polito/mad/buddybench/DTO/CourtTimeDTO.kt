package it.polito.mad.buddybench.DTO


import it.polito.mad.buddybench.Entities.CourtTime
import java.time.LocalTime

class CourtTimeDTO(court: Int, openingTime: LocalTime, closingTime: LocalTime, dayOfWeek: String) {

    val court = court
    val openingTime = openingTime.toString()
    val closingTime = closingTime.toString()
    val dayOfWeek = dayOfWeek

}

data class CourtTimeCompleteDTO(

    val openingTime: String,
    val closingTime: String,
    val dayOfWeek: String,

    val courtName: String,
    val address: String,
    val feeHour: Int,
    val sport: String

)

/** Usalo a tuo rischio e pericolo -> Prenditi la responsabilita' sul courtId **/
fun CourtTimeCompleteDTO.toEntity(courtId: Int): CourtTime {
    return CourtTime (
        court = courtId,
        openingTime = this.openingTime,
        closingTime = this.closingTime,
        dayOfWeek = this.dayOfWeek,
    )
}


fun CourtTimeDTO.toEntity(): CourtTime {
    return CourtTime(
        court = this.court,
        openingTime = this.openingTime,
        closingTime = this.closingTime,
        dayOfWeek = this.dayOfWeek,
    )
}