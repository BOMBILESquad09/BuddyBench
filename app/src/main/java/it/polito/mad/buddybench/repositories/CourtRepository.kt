package it.polito.mad.buddybench.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.mad.buddybench.DAO.CourtDao
import it.polito.mad.buddybench.DAO.SportDao
import it.polito.mad.buddybench.DAO.UserDao
import it.polito.mad.buddybench.DAO.UserSportDao
import it.polito.mad.buddybench.DTO.CourtDTO
import it.polito.mad.buddybench.Entities.Sport
import it.polito.mad.buddybench.Entities.toCourtDTO
import javax.inject.Inject

class CourtRepository @Inject constructor(
    private val courtDao: CourtDao,
    private val sportDao: SportDao
) {

    fun getAll(): List<CourtDTO> = courtDao.getAll().map { it.toCourtDTO() }

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