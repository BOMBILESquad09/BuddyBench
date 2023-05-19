package it.polito.mad.buddybench.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
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

    @ColumnInfo(name = "end_time")
    val endTime: Int,

    @ColumnInfo(name = "equipment")
    val equipment: Boolean,


    )

fun Reservation.toReservationDTO(user: User, court: Court): ReservationDTO {
    return ReservationDTO (
        id = this.id.toString(),
        userOrganizer = user.toUserDTO().toProfile(),
        court = court.toCourtDTO(),
        date = LocalDate.parse(this.date),
        startTime = LocalTime.of(this.startTime,0),
        endTime = LocalTime.of(this.endTime,0),
        equipment = this.equipment
    )
}