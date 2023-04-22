package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dao.CourtTimeDao
import it.polito.mad.buddybench.dao.SportDao
import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dto.CourtTimeDTO
import it.polito.mad.buddybench.entities.toCourtTimeDTO
import java.time.DayOfWeek
import javax.inject.Inject

class CourtTimeRepository @Inject constructor(
    private val courtTimeDao: CourtTimeDao,
    private val courtDao: CourtDao,
    private val sportDao: SportDao,
) {

    fun getAll(): List<CourtTimeDTO> = courtTimeDao.getAll().map { it.toCourtTimeDTO() }

    fun save(courtTime: CourtTimeDTO) {
        val courtAndSport = courtDao.getByNameAndSport(courtTime.courtName, courtTime.sport)
        courtTimeDao.save(courtTime.toEntity(courtAndSport.court.id))
    }

    fun delete(courtTime: CourtTimeDTO) {
        val courtAndSport = courtDao.getByNameAndSport(courtTime.courtName, courtTime.sport)
        courtTimeDao.delete(courtTime.toEntity(courtAndSport.court.id))
    }

    fun getCourtTimesByDay(dayOfWeek: DayOfWeek): List<CourtTimeDTO> {
        return courtTimeDao.getCourtTimesByDay(dayOfWeek).map { it.toCourtTimeDTO() }
    }
}