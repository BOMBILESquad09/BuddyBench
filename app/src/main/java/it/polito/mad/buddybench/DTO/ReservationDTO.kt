package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Reservation
import java.time.LocalDate
import java.time.LocalTime

class ReservationDTO(userOrganizer: Int, court: Int, date: LocalDate, startTime: LocalTime, endTime: LocalTime) {

    val userOrganizer = userOrganizer
    val court = court
    val date = date
    val startTime = startTime
    val endTime = endTime

}

fun ReservationDTO.toEntity(): Reservation {
    return Reservation(
        userOrganizer = this.userOrganizer,
        court = this.court,
        startTime = this.startTime.toString(),
        endTime = this.endTime.toString(),
        date = this.date.toString(),
    )
}