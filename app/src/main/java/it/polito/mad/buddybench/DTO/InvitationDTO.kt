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

data class InvitationDTOComplete (

    val name: String,
    val surname: String,
    val nickname: String,
    val birthdate: String,
    val location: String,
    val email: String,
    val reliability: String,

    val courtName: String,
    val address: String,
    val feeHour: Int,
    val sport: String,

    val date: String,
    val startTime: String,
    val endTime: String,

    val invitedName: String,
    val invitedSurname: String,
    val invitedNickname: String,
    val invitedBirthdate: String,
    val invitedLocation: String,
    val invitedEmail: String,
    val invitedReliability: String,

    val presence: Boolean,
    val confirmed: Boolean

)

fun InvitationDTOComplete.toEntity(reservationId: Int, userId: Int): Invitation {
    return Invitation(
        reservation = reservationId,
        confirmed = this.confirmed,
        presence = this.presence,
        user = userId,
    )
}

fun InvitationDTO.toEntity(): Invitation {
    return Invitation(
        reservation = this.reservation,
        confirmed = this.confirmed,
        presence = this.presence,
        user = this.user,
    )
}