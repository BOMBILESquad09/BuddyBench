package it.polito.mad.buddybench.persistence.dao
import androidx.room.*
import it.polito.mad.buddybench.persistence.entities.ReservationWithUserAndCourt
import it.polito.mad.buddybench.persistence.entities.Reservation

@Dao
interface ReservationDao {

    @Query("SELECT * FROM reservation")
    fun getAll(): List<ReservationWithUserAndCourt>

    @Query("SELECT * FROM reservation WHERE id = :id")
    fun get(id: Int): Reservation

    @Insert
    fun save(reservation: Reservation)

    @Update(entity = Reservation::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(reservation: Reservation)
    @Delete
    fun delete(reservation: Reservation)

    @Query("SELECT * FROM reservation WHERE court = :courtId AND date = :date")
    fun getAllByCourtAndDate(courtId: Int, date: String): List<ReservationWithUserAndCourt>

    @Query("SELECT * FROM reservation R, user U WHERE U.email = :email and R.user = U.id")
    fun getAllByUser(email: String): List<ReservationWithUserAndCourt>

    @Query("SELECT * FROM reservation R, user U WHERE U.email = :email and R.user = U.id AND court = :courtId AND date = :date AND start_time = :startTime")
    fun getReservation(email: String, courtId: Int, date: String, startTime: Int): ReservationWithUserAndCourt


    @Query("SELECT * FROM reservation R WHERE  R.user = :userId  AND court = :courtId AND date = :date AND start_time = :startTime")
    fun getReservationPlain(userId: Int, courtId: Int, date: String, startTime: Int): Reservation





}