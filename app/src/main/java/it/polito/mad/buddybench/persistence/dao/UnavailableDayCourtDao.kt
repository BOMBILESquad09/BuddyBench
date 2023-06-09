package it.polito.mad.buddybench.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.polito.mad.buddybench.persistence.entities.Court
import it.polito.mad.buddybench.persistence.entities.Sport
import it.polito.mad.buddybench.persistence.entities.UnavailableDayCourt

@Dao
interface UnavailableDayCourtDao {

    @Query("SELECT * FROM unavailable_court_date")
    fun getAll(): List<UnavailableDayCourt>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(unavailableDayCourt: UnavailableDayCourt)

    @Delete
    fun delete(unavailableDayCourt: UnavailableDayCourt)

    @Query("SELECT * FROM unavailable_court_date WHERE id = :courtId")
    fun getUnavailableDaysCourt(courtId: Int): List<UnavailableDayCourt>?

}