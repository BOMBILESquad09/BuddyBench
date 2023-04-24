package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.SportDao
import it.polito.mad.buddybench.dto.SportDTO

import it.polito.mad.buddybench.entities.toSportDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportRepository @Inject constructor(private val sportDao: SportDao) {

    fun getAll(): List<SportDTO> = sportDao.getAll().map { it.toSportDTO() }

    fun insert(sport: SportDTO) = sportDao.save(sport.toEntity())

    fun delete(sport: SportDTO) = sportDao.delete(sport.toEntity())

}