package it.polito.mad.buddybench.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.Entities.UserSport
import it.polito.mad.buddybench.Entities.UserSportsWithUserAndSport

@Dao
interface UserSportDao {

    @Query("SELECT * FROM user_sport")
    fun getAll(): List<UserSportsWithUserAndSport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userSport: UserSport)

    @Delete
    fun delete(userSport: UserSport)

}