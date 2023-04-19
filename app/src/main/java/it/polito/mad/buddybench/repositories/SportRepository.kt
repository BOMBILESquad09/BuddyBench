package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.DAO.SportDao
import it.polito.mad.buddybench.DTO.SportDTO
import it.polito.mad.buddybench.DTO.UserSportDTO
import it.polito.mad.buddybench.DTO.toEntity
import it.polito.mad.buddybench.Entities.Sport
import it.polito.mad.buddybench.Entities.toSportDTO
import javax.inject.Inject

class SportRepository @Inject constructor(private val sportDao: SportDao) {

    fun getAll(): List<SportDTO> = sportDao.getAll().map { it.toSportDTO() }

    fun insert(sport: SportDTO) = sportDao.save(sport.toEntity())

    fun delete(sport: SportDTO) = sportDao.delete(sport.toEntity())

}