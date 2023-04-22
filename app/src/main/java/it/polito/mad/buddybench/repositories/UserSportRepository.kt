package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.SportDao
import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dao.UserSportDao
import it.polito.mad.buddybench.dto.UserSportDTO
import it.polito.mad.buddybench.entities.Sport
import it.polito.mad.buddybench.entities.toUserSportDTO
import javax.inject.Inject

class UserSportRepository @Inject constructor(
    private val userSportDao: UserSportDao,
    private val sportDao: SportDao,
    private val userDao: UserDao,
) {

    fun getAll(): List<UserSportDTO> = userSportDao.getAll().map { it.toUserSportDTO() }

    fun save(userSportDTO: UserSportDTO) {
        val user = userDao.getUserByEmail(userSportDTO.user.email)!!
        userSportDao.save(userSportDTO.toEntity( user.id))
    }

    fun delete(userSportDTO: UserSportDTO) {
        val user = userDao.getUserByEmail(userSportDTO.user.email)!!
        userSportDao.delete(userSportDTO.toEntity(user.id))
    }

}