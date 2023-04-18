package it.polito.mad.buddybench.Entities

import androidx.room.*
import it.polito.mad.buddybench.DTO.InvitationDTO
import it.polito.mad.buddybench.DTO.ReservationDTO
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "reservation", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Court::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("court"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class Reservation(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user")
    val userOrganizer: Int,

    @ColumnInfo(name = "court")
    val court: Int,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "start_time")
    val startTime: Int,

    )

fun ReservationWithUserAndCourt.toReservationDTO(): ReservationDTO {
    return ReservationDTO(
        userOrganizer = this.userOrganizer.toUserDTO(),
        court = this.court.toCourtDTO(),
        date = LocalDate.parse(this.reservation.date),
        startTime = LocalTime.of(this.reservation.startTime,0),
        endTime = LocalTime.of(this.reservation.startTime,0).plusHours(1)
    )
}

fun Reservation.toReservationDTO(user: User, court: Court): ReservationDTO {
    return ReservationDTO (
        userOrganizer = user.toUserDTO(),
        court = court.toCourtDTO(),
        date = LocalDate.parse(this.date),
        startTime = LocalTime.of(this.startTime,0),
        endTime = LocalTime.of(this.startTime,0).plusHours(1)
    )
}

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