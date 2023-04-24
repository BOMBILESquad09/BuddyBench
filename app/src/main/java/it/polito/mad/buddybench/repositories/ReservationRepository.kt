package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dao.CourtTimeDao
import it.polito.mad.buddybench.dao.ReservationDao
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.entities.toReservationDTO
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ReservationRepository @Inject constructor(
    private val reservationDao: ReservationDao,
    private val userDao: UserDao,
    private val courtDao: CourtDao,
    private val courtTimeDao: CourtTimeDao
) {

    fun getAll(): HashMap<LocalDate, List<ReservationDTO>> {
        return ReservationDTO.toHashmap(reservationDao.getAll().map { it.toReservationDTO() })
    }

    fun getAllByUser(email: String): HashMap<LocalDate, List<ReservationDTO>> {
        return ReservationDTO.toHashmap(reservationDao.getAllByUser(email).map { it.toReservationDTO() })
    }

    fun save(reservationDTO: ReservationDTO) {
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.name, reservationDTO.court.sport)
        reservationDao.save(reservationDTO.toEntity(user.id, courtWithSport.court.id, reservationDTO.equipment))
    }

    fun delete(reservationDTO: ReservationDTO) {
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.name, reservationDTO.court.sport)
        reservationDao.delete(reservationDTO.toEntity(user.id, courtWithSport.court.id, reservationDTO.equipment))
    }

    fun getTimeSlotsOccupiedForCourtAndDate(court: CourtDTO, date: LocalDate): List<LocalTime> {
        val courtSelected = courtDao.getByNameAndSport(court.name, court.sport)
        val reservations = reservationDao.getAllByCourtAndDate(
            courtId = courtSelected.court.id,
            date = date.toString()
        )
        val timeSlots = reservations.map {
            if(it.reservation.startTime < 10) {
                LocalTime.parse( "0" + it.reservation.startTime.toString() + ":00" )
            } else {
                LocalTime.parse( it.reservation.startTime.toString() + ":00" )
            }
        }
        return timeSlots
    }


}

