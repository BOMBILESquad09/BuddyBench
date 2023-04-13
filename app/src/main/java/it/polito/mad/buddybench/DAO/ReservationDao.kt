import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.Entities.Reservation

@Dao
interface ReservationDao {

    @Query("SELECT * FROM RESERVATION")
    fun getAll(): LiveData<List<Reservation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

}