package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.Invitation

class InvitationDTO(reservation: Int, confirmed: Boolean, presence: Boolean, user: Int) {

    val reservation = reservation
    val confirmed = confirmed
    val presence = presence
    val user = user

}

fun InvitationDTO.toEntity(): Invitation {
    return Invitation(
        reservation = this.reservation,
        confirmed = this.confirmed,
        presence = this.presence,
        user = this.user,
    )
}