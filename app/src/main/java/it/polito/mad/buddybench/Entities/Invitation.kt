package it.polito.mad.buddybench.Entities

import androidx.room.*
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

fun InvitationWithReservationAndUser.toInvitationDTO(): InvitationDTO {
    return InvitationDTO(
        reservation = this.reservation.toReservationDTO(
            user = this.userInvited,
            court = this.court,
            sport = this.sport
        ),
        confirmed = this.invitation.confirmed,
        presence = this.invitation.presence,
        userInvited = this.userInvited.toUserDTO(),

        )
}

data class InvitationWithReservationAndUser(

    @Embedded val invitation: Invitation,
    @Relation(
        parentColumn = "user",
        entityColumn = "id"
    )
    val userInvited: User,

    @Relation(
        parentColumn = "reservation",
        entityColumn = "id",
    )
    val reservation: Reservation,

    @Relation(
        parentColumn = "reservation",
        entityColumn = "id",
        associateBy = Junction(
            value = Reservation::class,
            parentColumn = "court",
            entityColumn = "id"
        )
    )
    val court: Court,

    @Relation(
        parentColumn = "reservation",
        entityColumn = "id",
        associateBy = Junction(
            value = Court::class,
            parentColumn = "sport",
            entityColumn = "id"
        )
    )
    val sport: Sport

)