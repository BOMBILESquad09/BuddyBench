package it.polito.mad.buddybench.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.Entities.Sport
import it.polito.mad.buddybench.enums.Sports

@Dao
interface SportDao {

    @Query("SELECT * FROM Sport")
    fun getAll(): List<Sport>

    @Insert(onConflict = REPLACE)
    fun save(sport: Sport)

    @Delete
    fun delete(sport: Sport)

    @Query("SELECT * FROM Sport WHERE sportName = :name")
    fun getSportByName(name: String): Sport?

}