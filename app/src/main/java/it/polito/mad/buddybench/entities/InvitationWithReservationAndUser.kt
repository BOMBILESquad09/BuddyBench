package it.polito.mad.buddybench.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import it.polito.mad.buddybench.dto.InvitationDTO

fun InvitationWithReservationAndUser.toInvitationDTO(): InvitationDTO {
    return InvitationDTO(
        reservation = this.reservation.toReservationDTO(
            user = this.userInvited,
            court = this.court,
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

    )