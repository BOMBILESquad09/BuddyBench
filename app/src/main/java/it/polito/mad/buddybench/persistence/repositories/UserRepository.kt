package it.polito.mad.buddybench.persistence.repositories

import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.persistence.dao.UserDao
import it.polito.mad.buddybench.persistence.dao.UserSportDao
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.persistence.entities.UserSport

import it.polito.mad.buddybench.persistence.entities.UserWithSportsDTO
import it.polito.mad.buddybench.persistence.entities.toUserDTO
import it.polito.mad.buddybench.persistence.entities.toUserSportDTO
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.entities.User
import it.polito.mad.buddybench.persistence.entities.UserWithSports
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userSportDao: UserSportDao,


    ) {

    fun getUser(email: String): UserWithSportsDTO{
        return userDao.getUserByEmail(email)!!.toUserSportDTO()
    }
    fun getAll(): List<UserDTO> = userDao.getAll().map { it.toUserDTO() }

    fun save(user: UserDTO) {
        userDao.save(user.toEntity())
    }

    fun checkUser(email: String): UserWithSports? {
        return userDao.getUserByEmail(email)
    }


    fun update(user: UserWithSportsDTO, oldEmail: String) {

        val u = userDao.getUserByEmail(oldEmail)!!
        val newUser = User (
            u.user.id,
            user.user.name,
            user.user.surname,
            user.user.nickname,
            user.user.birthdate.toString(),
            user.user.location,
            user.user.email,
            user.user.reliability,
            user.user.imagePath
        )

        userDao.update(newUser)
        val dbSports = userSportDao.getAll(u.user.id)
        val hashMap: HashMap<Sports, Sport> = HashMap()
        user.sports.forEach {
            hashMap[it.name] = it
        }

        for (dbS in dbSports){
            val entry = hashMap[Sports.valueOf(dbS.sport.uppercase())]
            if(entry != null && entry.skill != Skills.valueOf(dbS.skill)){

                val convertedSport = UserSport(
                    id = dbS.id,
                    user = u.user.id,
                    skill = entry.skill.name,
                    sport = entry.name.name,
                    gamesOrganized = entry.matchesOrganized,
                    gamesPlayed = entry.matchesPlayed,
                    achievements = entry.achievements.joinToString(";")
                )

                userSportDao.update(convertedSport)
            }


        }






    }

    fun delete(user: UserDTO) = userDao.save(user.toEntity())

}