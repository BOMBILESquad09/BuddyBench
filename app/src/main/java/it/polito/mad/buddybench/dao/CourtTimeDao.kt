package it.polito.mad.buddybench.dao
import androidx.room.*
import it.polito.mad.buddybench.entities.CourtTime
import it.polito.mad.buddybench.entities.CourtWithCourtTime
import it.polito.mad.buddybench.entities.UnavailableDayCourt

@Dao
interface CourtTimeDao {

    @Query("SELECT * FROM court_time")
    fun getAll(): List<CourtWithCourtTime>


    @Query("SELECT * FROM court_time WHERE court = :courtId")
    fun getCourtTimeTable(courtId: Int): List<CourtWithCourtTime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(courtTime: CourtTime)

    @Delete
    fun delete(courtTime: CourtTime)

    @Query("SELECT * FROM court C, court_time CT  WHERE CT.court = C.id AND  day_of_week = :day AND C.sport = :sport")
    fun getCourtTimesByDay(sport: String, day: Int): List<CourtWithCourtTime>

    @Query("SELECT * FROM court_time WHERE court = :courtId AND day_of_week =  :day")
    fun getDayTimeByCourt(courtId: Int, day: Int): CourtWithCourtTime?

    @Query("SELECT * FROM unavailable_court_date WHERE date = :date")
    fun getUnavailableCourts(date: String): List<UnavailableDayCourt>

    @Query("SELECT * FROM unavailable_court_date")
    fun getUnavailableCourts(): List<UnavailableDayCourt>


}