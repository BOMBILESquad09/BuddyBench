package it.polito.mad.buddybench.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.entities.UserSport

@Dao
interface UserSportDao {

    @Query("SELECT * FROM user_sport")
    fun getAll(): List<UserSportsWithUserAndSport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userSport: UserSport)

    @Delete
    fun delete(userSport: UserSport)

}