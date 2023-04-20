package it.polito.mad.buddybench.dto


import it.polito.mad.buddybench.entities.CourtTime
import java.time.LocalTime

class CourtTimeDTO(court: Int, openingTime: LocalTime, closingTime: LocalTime, dayOfWeek: String) {

    val court = court
    val openingTime = openingTime.toString()
    val closingTime = closingTime.toString()
    val dayOfWeek = dayOfWeek

}

fun CourtTimeDTO.toEntity(): CourtTime {
    return CourtTime(
        court = this.court,
        openingTime = this.openingTime,
        closingTime = this.closingTime,
        dayOfWeek = this.dayOfWeek,
    )
}