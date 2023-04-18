package it.polito.mad.buddybench.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.DTO.CourtTimeDTO
import it.polito.mad.buddybench.DTO.InvitationDTO
import java.time.LocalTime

@Entity(
    tableName = "Invitation", foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("user"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Reservation::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("reservation"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Invitation(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "reservation")
    val reservation: Int,

    @ColumnInfo(name = "confirmed")
    val confirmed: Boolean,

    @ColumnInfo(name = "presence")
    val presence: Boolean,

    @ColumnInfo(name = "user")
    val user: Int

)

fun Invitation.toInvitationDTO(): InvitationDTO {
    return InvitationDTO(
        reservation = this.reservation,
        confirmed = this.confirmed,
        presence = this.presence,
        user = this.user,

    )
}