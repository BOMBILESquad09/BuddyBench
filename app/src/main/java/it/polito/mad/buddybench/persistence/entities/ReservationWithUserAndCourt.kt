package it.polito.mad.buddybench.persistence.entities

import androidx.room.Embedded
import androidx.room.Relation
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import java.time.LocalDate
import java.time.LocalTime

data class ReservationWithUserAndCourt(

    @Embedded val reservation: Reservation,
    @Relation(
        parentColumn = "user",
        entityColumn = "id"
    )
    val userOrganizer: User,

    @Relation(
        parentColumn = "court",
        entityColumn = "id",
    )
    val court: Court,



    )

fun ReservationWithUserAndCourt.toReservationDTO(): ReservationDTO {
    return ReservationDTO(
        userOrganizer = this.userOrganizer.toUserDTO(),
        court = this.court.toCourtDTO(),
        date = LocalDate.parse(this.reservation.date),
        startTime = LocalTime.of(this.reservation.startTime,0),
        endTime = LocalTime.of(this.reservation.endTime,0),
        equipment = this.reservation.equipment
    )
}