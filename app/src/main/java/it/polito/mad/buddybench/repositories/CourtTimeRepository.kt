package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dao.CourtTimeDao
import it.polito.mad.buddybench.dao.SportDao
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.CourtTimeDTO
import it.polito.mad.buddybench.dto.CourtTimeTableDTO
import it.polito.mad.buddybench.entities.toCourtDTO
import it.polito.mad.buddybench.entities.toCourtTimeDTO
import it.polito.mad.buddybench.enums.Sports
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CourtTimeRepository @Inject constructor(
    private val courtTimeDao: CourtTimeDao,
    private val courtDao: CourtDao,
    private val sportDao: SportDao,
) {

    fun getAll(): List<CourtTimeDTO> = courtTimeDao.getAll().map { it.toCourtTimeDTO() }

    fun getCourtTimeTable(name: String, sport: Sports): CourtTimeTableDTO{
        val court = courtDao.getByNameAndSport(name, sport.name.uppercase())
        val list= courtTimeDao.getCourtTimeTable(court.court.id).map {
            it.toCourtTimeDTO()
        }
        val tt:HashMap<DayOfWeek, Pair<LocalTime, LocalTime>> = HashMap()
        for (x in list){
            tt[x.dayOfWeek] = Pair(x.openingTime, x.closingTime)
        }
        return CourtTimeTableDTO(
            court.toCourtDTO(),
            tt
        )
    }

    fun save(courtTime: CourtTimeDTO) {
        val courtAndSport = courtDao.getByNameAndSport(courtTime.courtName, courtTime.sport)
        courtTimeDao.save(courtTime.toEntity(courtAndSport.court.id))
    }

    fun delete(courtTime: CourtTimeDTO) {
        val courtAndSport = courtDao.getByNameAndSport(courtTime.courtName, courtTime.sport)
        courtTimeDao.delete(courtTime.toEntity(courtAndSport.court.id))
    }

    fun getCourtTimesByDay(sport: Sports,date: LocalDate): List<CourtDTO> {
        val courts =  courtTimeDao.getCourtTimesByDay(sport.name.uppercase(), date.dayOfWeek.value)
        val unavailableCourts = courtTimeDao.getUnavailableCourts(date.format(DateTimeFormatter.ISO_LOCAL_DATE)).map { it.court }

        val courtIdSet = courts.map { it.court.id }
        val availableCourts = courtIdSet.filter { !unavailableCourts.contains(it) }
        return courts.filter { availableCourts.contains(it.court.id) }.map { it.court.toCourtDTO() }
    }

    fun getCourtTimesByCourt(court: CourtDTO, day: DayOfWeek): CourtTimeDTO? {
        val courtWithSport = courtDao.getByNameAndSport(courtName = court.name, sportInCourt = court.sport)
        return courtTimeDao.getDayTimeByCourt(
            courtWithSport.court.id,
            day.value
        )?.toCourtTimeDTO()
    }
}