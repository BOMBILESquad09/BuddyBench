package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.DAO.CourtDao
import it.polito.mad.buddybench.DAO.ReservationDao
import it.polito.mad.buddybench.DAO.UserDao
import it.polito.mad.buddybench.DTO.ReservationDTO
import it.polito.mad.buddybench.DTO.toEntity
import it.polito.mad.buddybench.Entities.toReservationDTO
import javax.inject.Inject

class ReservationRepository @Inject constructor(
    private val reservationDao: ReservationDao,
    private val userDao: UserDao,
    private val courtDao: CourtDao,
) {

    fun getAll(): List<ReservationDTO> = reservationDao.getAll().map { it.toReservationDTO() }

    fun save(reservationDTO: ReservationDTO) {
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.courtName, reservationDTO.court.sport)
        reservationDao.save(reservationDTO.toEntity(user.id, courtWithSport.court.id))
    }

    fun delete(reservationDTO: ReservationDTO) {
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.courtName, reservationDTO.court.sport)
        reservationDao.delete(reservationDTO.toEntity(user.id, courtWithSport.court.id))
    }

}