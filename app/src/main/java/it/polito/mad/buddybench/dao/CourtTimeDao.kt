package it.polito.mad.buddybench.dao
import androidx.room.*
import it.polito.mad.buddybench.entities.CourtTime
import it.polito.mad.buddybench.entities.CourtWithCourtTime
import java.time.DayOfWeek

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

    @Query("SELECT * FROM court_time WHERE day_of_week = :day")
    fun getCourtTimesByDay(day: DayOfWeek): List<CourtWithCourtTime>

    @Query("SELECT * FROM court_time WHERE court = :courtId AND day_of_week =  :day")
    fun getDayTimeByCourt(courtId: Int, day: Int): CourtWithCourtTime?


}