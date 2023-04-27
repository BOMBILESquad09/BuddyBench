package it.polito.mad.buddybench.persistence.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.persistence.entities.Court
import it.polito.mad.buddybench.persistence.entities.CourtWithSport

@Dao
interface CourtDao {

    @Query("SELECT * FROM Court")
    fun getAll(): List<CourtWithSport>

    @Insert(onConflict = REPLACE)
    fun save(court: Court)

    @Delete
    fun delete(court: Court)

    @Query("SELECT * FROM Court C, Sport S WHERE C.sport = S.sport_name AND C.name = :courtName AND S.sport_name = :sportInCourt")
    fun getByNameAndSport(courtName: String, sportInCourt: String): CourtWithSport


    @Query("SELECT * FROM Court  WHERE   name = :courtName and sport = :sportInCourt")
    fun getByNameAndSportPlain(courtName: String, sportInCourt: String): Court


    @Query("SELECT * FROM Court  WHERE name = :courtName")
    fun getByName(courtName: String): Court


    @Query("SELECT * FROM Court WHERE sport = :sport")
    fun getCourtsBySport(sport: String): List<CourtWithSport>


}