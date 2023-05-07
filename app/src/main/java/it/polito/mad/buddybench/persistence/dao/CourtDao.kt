package it.polito.mad.buddybench.persistence.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.persistence.entities.Court
import it.polito.mad.buddybench.persistence.entities.CourtWithFacilities
import it.polito.mad.buddybench.persistence.entities.CourtWithSport

@Dao
interface CourtDao {

    @Query("SELECT * FROM Court")
    fun getAll(): List<CourtWithSport>

    @Query("SELECT * FROM court C LEFT JOIN court_facility cf ON cf.court = C.id where C.name = :courtName and C.sport = :sportInCourt")
    fun getCourtWithFacilities(courtName: String, sportInCourt: String): CourtWithFacilities



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