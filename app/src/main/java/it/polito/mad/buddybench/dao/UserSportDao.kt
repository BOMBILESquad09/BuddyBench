package it.polito.mad.buddybench.dao

import androidx.room.*
import it.polito.mad.buddybench.entities.UserSport

@Dao
interface UserSportDao {

    @Query("SELECT * FROM UserSport")
    fun getAll(): List<UserSport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userSport: UserSport)

    @Delete
    fun delete(userSport: UserSport)

}