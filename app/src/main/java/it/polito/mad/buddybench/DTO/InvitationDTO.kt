package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Invitation

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