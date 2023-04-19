package it.polito.mad.buddybench.repositories

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
        val sport = sportDao.getSportByName(court.sport)!!
        courtDao.save(court.toEntity(sport.id))
    }

    fun delete(court: CourtDTO) {
        val sport = sportDao.getSportByName(court.sport)!!
        courtDao.delete(court.toEntity(sport.id))
    }

    fun getCourtsBySports(sport: String): List<CourtDTO> {
        val sport = sportDao.getSportByName(sport)!!
        return courtDao.getCourtsBySport(sport.id).map { it.toCourtDTO() }
    }

}