package it.polito.mad.buddybench.persistence.repositories

import it.polito.mad.buddybench.persistence.dao.SportDao
import it.polito.mad.buddybench.persistence.dto.SportDTO

import it.polito.mad.buddybench.persistence.entities.toSportDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportRepository @Inject constructor(private val sportDao: SportDao) {

    fun getAll(): List<SportDTO> = sportDao.getAll().map { it.toSportDTO() }

    fun insert(sport: SportDTO) = sportDao.save(sport.toEntity())

    fun delete(sport: SportDTO) = sportDao.delete(sport.toEntity())

}