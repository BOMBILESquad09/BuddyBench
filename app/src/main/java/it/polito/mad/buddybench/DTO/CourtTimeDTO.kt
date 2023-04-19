package it.polito.mad.buddybench.DTO


import it.polito.mad.buddybench.Entities.CourtTime
import java.time.LocalTime

class CourtTimeDTO(
    val courtName: String,
    val address: String,
    val feeHour: Int,
    val sport: String,
    val openingTime: LocalTime,
    val closingTime: LocalTime,
    val dayOfWeek: String) {

    fun toEntity(courtId: Int): CourtTime {
        return CourtTime(
            court = courtId,
            openingTime = this.openingTime.toString(),
            closingTime = this.closingTime.toString(),
            dayOfWeek = this.dayOfWeek,
        )
    }

}


