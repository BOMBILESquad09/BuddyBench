package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dao.ReservationDao
import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.entities.Reservation
import it.polito.mad.buddybench.entities.toReservationDTO
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ReservationRepository @Inject constructor(
    private val reservationDao: ReservationDao,
    private val userDao: UserDao,
    private val courtDao: CourtDao,
) {

    fun getAll(): HashMap<LocalDate, List<ReservationDTO>> {
        return ReservationDTO.toHashmap(reservationDao.getAll().map { it.toReservationDTO() })
    }

    fun save(reservationDTO: ReservationDTO) {
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.name, reservationDTO.court.sport)
        reservationDao.save(reservationDTO.toEntity(user.id, courtWithSport.court.id))
    }

    fun delete(reservationDTO: ReservationDTO) {
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.name, reservationDTO.court.sport)
        reservationDao.delete(reservationDTO.toEntity(user.id, courtWithSport.court.id))
    }

}

