package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.Entities.CourtTime
import it.polito.mad.buddybench.Entities.CourtTimeWithCourt
import java.time.DayOfWeek

@Dao
interface CourtTimeDao {

    @Query("SELECT * FROM CourtTime")
    fun getAll(): List<CourtTimeWithCourt>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(courtTime: CourtTime)

    @Delete
    fun delete(courtTime: CourtTime)

    @Query("SELECT * FROM CourtTime WHERE dayOfWeek = :day")
    fun getCourtTimesByDay(day: DayOfWeek): List<CourtTimeWithCourt>


}