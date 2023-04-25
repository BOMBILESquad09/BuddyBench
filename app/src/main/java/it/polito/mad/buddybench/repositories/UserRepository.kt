package it.polito.mad.buddybench.repositories

import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.dao.UserDao
import it.polito.mad.buddybench.dao.UserSportDao
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.entities.UserSport

import it.polito.mad.buddybench.entities.UserWithSportsDTO
import it.polito.mad.buddybench.entities.toUserDTO
import it.polito.mad.buddybench.entities.toUserSportDTO
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
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


    fun update(user: UserWithSportsDTO) {

        val u = userDao.getUserByEmail(user.user.email)!!
        userSportDao.getAll(u.user.id).forEach{
            println(it)
        }
        userDao.update(user.user.toEntity())
        val dbSports = userSportDao.getAll(u.user.id)
        val hashMap: HashMap<Sports, Sport> = HashMap()
        user.sports.forEach{
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
                    gamesPlayed = entry.matchesPlayed
                )

                userSportDao.update(convertedSport)
            }


        }


        userSportDao.getAll(u.user.id).forEach{
            println(it)
        }




    }

    fun delete(user: UserDTO) = userDao.save(user.toEntity())

}