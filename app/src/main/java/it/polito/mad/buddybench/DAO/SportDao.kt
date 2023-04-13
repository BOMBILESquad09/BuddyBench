import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.Entities.Sport

@Dao
interface SportDao {

    @Query("SELECT * FROM SPORT")
    fun getAll(): LiveData<List<Sport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(sport: Sport)

    @Delete
    fun delete(sport: Sport)

}