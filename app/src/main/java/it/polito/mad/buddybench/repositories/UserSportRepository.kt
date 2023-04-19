package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.DAO.SportDao
import it.polito.mad.buddybench.DAO.UserDao
import it.polito.mad.buddybench.DAO.UserSportDao
import it.polito.mad.buddybench.DTO.UserSportDTO
import it.polito.mad.buddybench.Entities.Sport
import it.polito.mad.buddybench.Entities.toUserSportDTO
import javax.inject.Inject

class UserSportRepository @Inject constructor(
    private val userSportDao: UserSportDao,
    private val sportDao: SportDao,
    private val userDao: UserDao,
) {

    fun getAll(): List<UserSportDTO> = userSportDao.getAll().map { it.toUserSportDTO() }

    fun save(userSportDTO: UserSportDTO) {
        val sport = sportDao.getSportByName(userSportDTO.sport)!!
        val user = userDao.getUserByEmail(userSportDTO.user.email)!!
        userSportDao.save(userSportDTO.toEntity( user.id, sport.id))
    }

    fun delete(userSportDTO: UserSportDTO) {
        val sport = sportDao.getSportByName(userSportDTO.sport)!!
        val user = userDao.getUserByEmail(userSportDTO.user.email)!!
        userSportDao.delete(userSportDTO.toEntity(user.id, sport.id))
    }

}