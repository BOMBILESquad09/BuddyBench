import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.Entities.CourtTime

@Dao
interface CourtTimeDao {

    @Query("SELECT * FROM COURTTIME")
    fun getAll(): LiveData<List<CourtTime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(courtTime: CourtTime)

    @Delete
    fun delete(courtTime: CourtTime)

}