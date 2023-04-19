package it.polito.mad.buddybench.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.Entities.Court
import it.polito.mad.buddybench.Entities.CourtWithSport

@Dao
interface CourtDao {

    @Query("SELECT * FROM Court")
    fun getAll(): List<CourtWithSport>

    @Insert(onConflict = REPLACE)
    fun save(court: Court)

    @Delete
    fun delete(court: Court)

    @Query("SELECT * FROM Court C, Sport S WHERE C.sport = S.id AND courtName = :courtName AND S.sportName = :sportInCourt")
    fun getByNameAndSport(courtName: String, sportInCourt: String): CourtWithSport



}