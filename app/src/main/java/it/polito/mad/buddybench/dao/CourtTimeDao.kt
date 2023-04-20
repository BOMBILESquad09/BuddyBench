package it.polito.mad.buddybench.dao
import androidx.room.*
import it.polito.mad.buddybench.entities.CourtTime

@Dao
interface CourtTimeDao {

    @Query("SELECT * FROM CourtTime")
    fun getAll(): List<CourtTime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(courtTime: CourtTime)

    @Delete
    fun delete(courtTime: CourtTime)

}