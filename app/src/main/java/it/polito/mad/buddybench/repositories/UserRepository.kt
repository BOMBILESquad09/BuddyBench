package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.entities.toUserDTO
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    ) {

    fun getAll(): List<UserDTO> = userDao.getAll().map { it.toUserDTO() }

    fun insert(user: UserDTO) {
        userDao.save(user.toEntity())
    }

    fun delete(user: UserDTO) = userDao.save(user.toEntity())

}