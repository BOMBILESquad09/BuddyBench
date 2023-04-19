package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.Invitation

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

