package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.persistence.entities.Invitation

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

