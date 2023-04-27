package it.polito.mad.buddybench.persistence.repositories

import it.polito.mad.buddybench.persistence.dao.SportDao
import it.polito.mad.buddybench.persistence.dao.UserDao
import it.polito.mad.buddybench.persistence.dao.UserSportDao
import it.polito.mad.buddybench.persistence.dto.UserSportDTO
import it.polito.mad.buddybench.persistence.entities.Sport
import it.polito.mad.buddybench.persistence.entities.toUserSportDTO
import javax.inject.Inject

class UserSportRepository @Inject constructor(
    private val userSportDao: UserSportDao,
    private val sportDao: SportDao,
    private val userDao: UserDao,
) {


    fun save(userSportDTO: UserSportDTO) {
        val user = userDao.getUserByEmail(userSportDTO.user.email)!!
        userSportDao.save(userSportDTO.toEntity( user.user.id))
    }

    fun delete(userSportDTO: UserSportDTO) {
        val user = userDao.getUserByEmail(userSportDTO.user.email)!!
        userSportDao.delete(userSportDTO.toEntity(user.user.id))
    }

}