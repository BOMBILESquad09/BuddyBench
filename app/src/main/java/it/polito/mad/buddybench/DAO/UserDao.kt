package it.polito.mad.buddybench.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.Entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Query("SELECT * FROM User WHERE email = :emailUser")
    fun getUserByEmail(emailUser: String): User?

}