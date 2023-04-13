package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.Entities.Reservation

@Dao
interface ReservationDao {

    @Query("SELECT * FROM Reservation")
    fun getAll(): List<Reservation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

}