package it.polito.mad.buddybench.dao
import androidx.room.*
import it.polito.mad.buddybench.entities.ReservationWithUserAndCourt
import it.polito.mad.buddybench.entities.Reservation

@Dao
interface ReservationDao {

    @Query("SELECT * FROM reservation")
    fun getAll(): List<ReservationWithUserAndCourt>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

    @Query("SELECT * FROM reservation WHERE court = :courtId AND date = :date")
    fun getAllByCourtAndDate(courtId: Int, date: String): List<ReservationWithUserAndCourt>



}