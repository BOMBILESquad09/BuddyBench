package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dao.InvitationDao
import it.polito.mad.buddybench.dao.ReservationDao
import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dto.InvitationDTO
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.entities.toInvitationDTO
import javax.inject.Inject

class InvitationRepository @Inject constructor(
    private val invitationDao: InvitationDao,
    private val userDao: UserDao,
    private val reservationDao: ReservationDao
) {

    fun getAll(): List<InvitationDTO> = invitationDao.getAll().map { it.toInvitationDTO() }
}