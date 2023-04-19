package it.polito.mad.buddybench.DTO


import it.polito.mad.buddybench.Entities.CourtTime
import java.time.LocalTime

class CourtTimeDTO(
    courtName: String,
    address: String,
    feeHour: Int,
    sport: String,
    openingTime: LocalTime,
    closingTime: LocalTime,
    dayOfWeek: String) {

    val courtName = courtName
    val address = address
    val sport = sport
    val feeHour = feeHour

    val openingTime = openingTime.toString()
    val closingTime = closingTime.toString()
    val dayOfWeek = dayOfWeek

}


fun CourtTimeDTO.toEntity(courtId: Int): CourtTime {
    return CourtTime(
        court = courtId,
        openingTime = this.openingTime,
        closingTime = this.closingTime,
        dayOfWeek = this.dayOfWeek,
    )
}