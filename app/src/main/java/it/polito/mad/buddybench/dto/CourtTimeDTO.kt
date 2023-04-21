package it.polito.mad.buddybench.dto


import it.polito.mad.buddybench.entities.CourtTime
import java.time.LocalTime

class CourtTimeDTO(
    val courtName: String,
    val address: String,
    val feeHour: Int,
    val sport: String,
    val openingTime: LocalTime,
    val closingTime: LocalTime,
    val dayOfWeek: DayOfWeek ) {

    fun toEntity(courtId: Int): CourtTime {
        return CourtTime(
            court = courtId,
            openingTime = this.openingTime.hour,
            closingTime = this.closingTime.hour,
            dayOfWeek = this.dayOfWeek.value,
        )
    }

}