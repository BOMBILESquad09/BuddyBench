package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.Invitation

class InvitationDTO(
    val reservation: ReservationDTO,
    val userInvited: UserDTO,
    val presence: Boolean,
    val confirmed: Boolean
) {

    fun toEntity(reservationId: Int, userId: Int): Invitation {
        return Invitation(
            reservation = reservationId,
            confirmed = this.confirmed,
            presence = this.presence,
            user = userId,
        )
    }

}

