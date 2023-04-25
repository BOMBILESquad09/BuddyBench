package it.polito.mad.buddybench.dao

import androidx.room.*
import it.polito.mad.buddybench.entities.Sport
import it.polito.mad.buddybench.entities.User
import it.polito.mad.buddybench.entities.UserSport

@Dao
interface UserSportDao {

    @Query("SELECT * FROM user_sport WHERE user=:userId")
    fun getAll(userId: Int): List<UserSport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userSport: UserSport)

    @Update
    fun update(userSport: UserSport)
    @Delete
    fun delete(userSport: UserSport)

}

