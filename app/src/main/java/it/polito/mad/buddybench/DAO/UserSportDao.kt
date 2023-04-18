package it.polito.mad.buddybench.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.DTO.UserSportDTOComplete
import it.polito.mad.buddybench.Entities.UserSport

@Dao
interface UserSportDao {

    @Query("SELECT * FROM UserSport")
    fun getAll(): List<UserSport>

    @Query("SELECT US.skill, US.gamesPlayed, US.gamesOrganized, S.sportName as sport, " +
            "U.name, U.surname, U.nickname, U.birthdate, U.location, U.email, U.reliability" +
            " FROM UserSport US, User U, Sport S WHERE US.sport = S.id AND U.id = US.user")
    fun getAllWithUserAndSportInformations(): List<UserSportDTOComplete>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userSport: UserSport)

    @Delete
    fun delete(userSport: UserSport)

}