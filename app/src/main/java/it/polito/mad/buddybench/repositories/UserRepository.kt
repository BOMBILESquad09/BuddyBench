package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.DAO.UserDao
import it.polito.mad.buddybench.DTO.UserDTO
import it.polito.mad.buddybench.DTO.toEntity
import it.polito.mad.buddybench.Entities.toUserDTO
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    fun getAll(): List<UserDTO> = userDao.getAll().map { it.toUserDTO() }

    fun insert(user: UserDTO) {
        userDao.save(user.toEntity())
    }

    fun update(user: UserDTO) = userDao.save(user.toEntity())

    fun delete(user: UserDTO) = userDao.save(user.toEntity())

}