package it.polito.mad.buddybench.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.Entities.UserSport

@Dao
interface UserSportDao {

    @Query("SELECT * FROM USERSPORT")
    fun getAll(): LiveData<List<UserSport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userSport: UserSport)

    @Delete
    fun delete(userSport: UserSport)

}