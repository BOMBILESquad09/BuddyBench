package it.polito.mad.buddybench.persistence.dao

import androidx.room.*
import it.polito.mad.buddybench.persistence.entities.User
import it.polito.mad.buddybench.persistence.entities.UserWithSports

@Dao
interface UserDao {

    @Insert
    fun save(user: User): Long

    @Update(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Query("SELECT MAX(ID) FROM user")
    fun getMaxId(): Long

    @Query("SELECT * FROM User WHERE email = :emailUser")
    fun getUserByEmail(emailUser: String): UserWithSports?

}