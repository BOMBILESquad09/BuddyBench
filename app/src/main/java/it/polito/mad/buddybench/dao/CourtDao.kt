package it.polito.mad.buddybench.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.entities.Court

@Dao
interface CourtDao {

    @Query("SELECT * FROM Court")
    fun getAll(): List<Court>

    @Insert(onConflict = REPLACE)
    fun save(court: Court)

    @Delete
    fun delete(court: Court)

}