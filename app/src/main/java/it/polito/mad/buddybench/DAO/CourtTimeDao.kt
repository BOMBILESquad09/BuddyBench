package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.Entities.CourtTime
import it.polito.mad.buddybench.Entities.CourtWithCourtTime
import java.time.DayOfWeek

@Dao
interface CourtTimeDao {

    @Query("SELECT * FROM court_time")
    fun getAll(): List<CourtWithCourtTime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(courtTime: CourtTime)

    @Delete
    fun delete(courtTime: CourtTime)

    @Query("SELECT * FROM court_time WHERE day_of_week = :day")
    fun getCourtTimesByDay(day: DayOfWeek): List<CourtWithCourtTime>


}