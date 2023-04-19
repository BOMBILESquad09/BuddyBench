package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Invitation

class InvitationDTO(
    reservation: ReservationDTO,
    userInvited: UserDTO,
    presence: Boolean,
    confirmed: Boolean
) {

    val reservation = reservation
    val confirmed = confirmed
    val presence = presence
    val userInvited = userInvited

}

fun InvitationDTO.toEntity(reservationId: Int, userId: Int): Invitation {
    return Invitation(
        reservation = reservationId,
        confirmed = this.confirmed,
        presence = this.presence,
        user = userId,
    )
}