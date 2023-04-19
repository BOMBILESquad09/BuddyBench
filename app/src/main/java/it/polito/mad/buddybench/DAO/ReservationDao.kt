package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.Entities.Reservation
import it.polito.mad.buddybench.Entities.ReservationWithUserAndCourt

@Dao
interface ReservationDao {

    @Query("SELECT * FROM Reservation")
    fun getAll(): List<ReservationWithUserAndCourt>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

    @Query("SELECT R.* FROM Reservation R, User U WHERE date > :currDate AND R.userOrganizer = U.id AND U.email = :email")
    fun getFutureReservationByEmail(email: String, currDate: String): List<Reservation>

}