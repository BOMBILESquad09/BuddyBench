package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dao.CourtTimeDao
import it.polito.mad.buddybench.dao.ReservationDao
import it.polito.mad.buddybench.dao.UnavailableDayCourtDao
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.entities.CourtWithSport
import it.polito.mad.buddybench.entities.Reservation
import it.polito.mad.buddybench.entities.UnavailableDayCourt
import it.polito.mad.buddybench.entities.toReservationDTO
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ReservationRepository @Inject constructor(
    private val reservationDao: ReservationDao,
    private val userDao: UserDao,
    private val courtDao: CourtDao,
    private val courtTimeDao: CourtTimeDao,
    private val unavailableDayCourtDao: UnavailableDayCourtDao,
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

        reservationDao.save(reservationDTO.toEntity(user.user.id, courtWithSport.court.id, reservationDTO.equipment))
        updateUnavailableDayCourt(reservationDTO, courtWithSport)
    }

    fun update(reservationDTO: ReservationDTO, oldDate: LocalDate, oldStartTime: Int){
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.name, reservationDTO.court.sport)
        var oldReservation = reservationDao.getReservation(reservationDTO.userOrganizer.email, courtWithSport.court.id,
            oldDate.format(DateTimeFormatter.ISO_LOCAL_DATE), oldStartTime)

        println("-------------------")

        println(oldDate)
        println(oldStartTime)
        println(reservationDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        println("-------------------")

        val newReservation = Reservation(
            id = oldReservation.reservation.id,
            startTime = reservationDTO.startTime.hour,
            endTime = reservationDTO.endTime.hour,
            date = reservationDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
            userOrganizer = user.user.id,
            equipment = reservationDTO.equipment,
            court = courtWithSport.court.id
        )

        reservationDao.update(newReservation)

        updateUnavailableDayCourt(reservationDTO, courtWithSport)
    }



    private fun updateUnavailableDayCourt(reservationDTO: ReservationDTO, courtWithSport: CourtWithSport){
        val reservations = reservationDao.getAllByCourtAndDate(courtWithSport.court.id, reservationDTO.date.format(
            DateTimeFormatter.ISO_LOCAL_DATE))
        reservations.map { it.reservation.endTime - it.reservation.startTime }.fold(0){
                a,b -> a+b
        }.let {
            val time = courtTimeDao.getDayTimeByCourt(courtWithSport.court.id, reservationDTO.date.dayOfWeek.value)!!
            if ((time.courtTime.closingTime - time.courtTime.openingTime) <= it){
                unavailableDayCourtDao.save(UnavailableDayCourt(courtWithSport.court.id, reservationDTO.date.format(
                    DateTimeFormatter.ISO_LOCAL_DATE)))
            } else{
                unavailableDayCourtDao.delete(UnavailableDayCourt(courtWithSport.court.id, reservationDTO.date.format(
                    DateTimeFormatter.ISO_LOCAL_DATE)))
            }
        }
    }

    fun delete(reservationDTO: ReservationDTO) {
        val user = userDao.getUserByEmail(reservationDTO.userOrganizer.email)!!
        val courtWithSport = courtDao.getByNameAndSport(reservationDTO.court.name, reservationDTO.court.sport)
        reservationDao.delete(reservationDTO.toEntity(user.user.id, courtWithSport.court.id, reservationDTO.equipment))
        unavailableDayCourtDao.delete(UnavailableDayCourt(courtWithSport.court.id, reservationDTO.date.format(
            DateTimeFormatter.ISO_LOCAL_DATE)))
    }

    fun getTimeSlotsOccupiedForCourtAndDate(court: CourtDTO, date: LocalDate): List<LocalTime> {
        val courtSelected = courtDao.getByNameAndSport(court.name, court.sport)
        val reservations = reservationDao.getAllByCourtAndDate(
            courtId = courtSelected.court.id,
            date = date.toString()
        )
        val timeSlots = reservations.map {
            Utils.getTimeSlots(LocalTime.of(it.reservation.startTime,0), LocalTime.of(it.reservation.endTime, 0))
        }.flatten().toList()
        println(timeSlots.toString() )
        return timeSlots
    }

    fun getReservation(courtName: String, sportInCourt: String, email: String, date: LocalDate, startTime: Int): ReservationDTO {
        val court = courtDao.getByNameAndSport(courtName, sportInCourt.uppercase())
        val user = userDao.getUserByEmail(email)

        val reservation = reservationDao.getReservation(
            user!!.user.email,
            court.court.id,
            date.toString(),
            startTime

        )
        return reservation.toReservationDTO()

    }




}

