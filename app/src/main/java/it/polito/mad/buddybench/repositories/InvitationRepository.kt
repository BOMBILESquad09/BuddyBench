package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.DAO.CourtDao
import it.polito.mad.buddybench.DAO.InvitationDao
import it.polito.mad.buddybench.DAO.ReservationDao
import it.polito.mad.buddybench.DAO.UserDao
import it.polito.mad.buddybench.DTO.InvitationDTO
import it.polito.mad.buddybench.DTO.ReservationDTO
import it.polito.mad.buddybench.Entities.toInvitationDTO
import javax.inject.Inject

class InvitationRepository @Inject constructor(
    private val invitationDao: InvitationDao,
    private val userDao: UserDao,
    private val reservationDao: ReservationDao
) {

    fun getAll(): List<InvitationDTO> = invitationDao.getAll().map { it.toInvitationDTO() }
}