package it.polito.mad.buddybench.Entities

import androidx.room.*
import it.polito.mad.buddybench.DTO.InvitationDTO
import it.polito.mad.buddybench.DTO.ReservationDTO
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "Reservation", foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userOrganizer"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Court::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("court"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Reservation(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "userOrganizer")
    val userOrganizer: Int,

    @ColumnInfo(name = "court")
    val court: Int,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "startTime")
    val startTime: String,

    )

fun ReservationWithUserAndCourt.toReservationDTO(): ReservationDTO {
    return ReservationDTO(
        userOrganizer = this.userOrganizer.toUserDTO(),
        court = this.court.toCourtDTO(this.sport.id),
        date = LocalDate.parse(this.reservation.date),
        startTime = LocalTime.parse(this.reservation.startTime),
        endTime = LocalTime.parse(this.reservation.startTime).plusHours(1)
    )
}

fun Reservation.toReservationDTO(user: User, court: Court, sport: Sport): ReservationDTO {
    return ReservationDTO (
        userOrganizer = user.toUserDTO(),
        court = court.toCourtDTO(sport.id),
        date = LocalDate.parse(this.date),
        startTime = LocalTime.parse(this.startTime),
        endTime = LocalTime.parse(this.startTime).plusHours(1)
    )
}

data class ReservationWithUserAndCourt(

    @Embedded val reservation: Reservation,
    @Relation(
        parentColumn = "userOrganizer",
        entityColumn = "id"
    )
    val userOrganizer: User,

    @Relation(
        parentColumn = "court",
        entityColumn = "id",
    )
    val court: Court,

    @Relation(
        parentColumn = "court",
        entityColumn = "id",
        associateBy = Junction(
            value = Court::class,
            parentColumn = "sport",
            entityColumn = "id"
        )
    )
    val sport: Sport

)