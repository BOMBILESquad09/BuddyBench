package it.polito.mad.buddybench.persistence.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.mad.buddybench.persistence.dao.CourtDao
import it.polito.mad.buddybench.persistence.dao.CourtTimeDao
import it.polito.mad.buddybench.persistence.dao.SportDao

import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.entities.Sport
import it.polito.mad.buddybench.persistence.entities.toCourtDTO
import it.polito.mad.buddybench.enums.Sports
import java.time.DayOfWeek
import java.util.Date
import javax.inject.Inject

class CourtRepository @Inject constructor(
    private val courtDao: CourtDao,
    private val sportDao: SportDao,

    ) {

    fun getAll(): List<CourtDTO> = courtDao.getAll().map { it.toCourtDTO() }




    fun getByNameAndSports(name: String, sport: Sports): CourtDTO {
        courtDao.getByNameAndSport(name, sport.name.uppercase()).let {
            return it.toCourtDTO()
        }
    }

    fun save(court: CourtDTO) {
        courtDao.save(court.toEntity())
    }

    fun delete(court: CourtDTO) {
        courtDao.delete(court.toEntity())
    }

    fun getCourtsBySports(sport: String): List<CourtDTO> {
        return courtDao.getCourtsBySport(sport).map { it.toCourtDTO() }
    }



}