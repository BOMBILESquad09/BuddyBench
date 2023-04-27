package it.polito.mad.buddybench.persistence.repositories

import it.polito.mad.buddybench.persistence.dao.CourtDao
import it.polito.mad.buddybench.persistence.dao.InvitationDao
import it.polito.mad.buddybench.persistence.dao.ReservationDao
import it.polito.mad.buddybench.persistence.dao.UserDao
import it.polito.mad.buddybench.persistence.dto.InvitationDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.entities.toInvitationDTO
import javax.inject.Inject

class InvitationRepository @Inject constructor(
    private val invitationDao: InvitationDao,
    private val userDao: UserDao,
    private val reservationDao: ReservationDao
) {

    fun getAll(): List<InvitationDTO> = invitationDao.getAll().map { it.toInvitationDTO() }
}