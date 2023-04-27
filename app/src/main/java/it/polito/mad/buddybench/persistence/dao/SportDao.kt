package it.polito.mad.buddybench.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.persistence.entities.Sport

@Dao
interface SportDao {

    @Query("SELECT * FROM sport")
    fun getAll(): List<Sport>

    @Insert(onConflict = REPLACE)
    fun save(sport: Sport)

    @Delete
    fun delete(sport: Sport)

    @Query("SELECT * FROM sport WHERE sport_name = :name")
    fun getSportByName(name: String): Sport?

}