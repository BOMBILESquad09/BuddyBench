package it.polito.mad.buddybench.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.entities.Sport

@Dao
interface SportDao {

    @Query("SELECT * FROM Sport")
    fun getAll(): List<Sport>

    @Insert(onConflict = REPLACE)
    fun save(sport: Sport)

    @Delete
    fun delete(sport: Sport)

    @Query("SELECT * FROM Sport WHERE ID = :id")
    fun getSportById(id: Int): Sport?

}