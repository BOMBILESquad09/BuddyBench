package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.DTO.ReservationCompleteDTO
import it.polito.mad.buddybench.Entities.Reservation

@Dao
interface ReservationDao {

    @Query("SELECT * FROM Reservation")
    fun getAll(): List<Reservation>

    @Query("SELECT U.name, U.surname, U.nickname, U.birthdate, U.location, U.email, U.reliability," +
            "S.sportName as sport, C.courtName, C.address, C.feeHour," +
            "R.date, R.startTime, R.endTime FROM Reservation R, Court C, User U, Sport S " +
            "WHERE S.id = C.sport AND R.court = C.id AND R.userOrganizer = U.id ")
    fun getAllWithUserAndCourtAndSportInformations(): List<ReservationCompleteDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

    @Query("SELECT R.* FROM Reservation R, User U WHERE date > :currDate AND R.userOrganizer = U.id AND U.email = :email")
    fun getFutureReservationByEmail(email: String, currDate: String): List<Reservation>

}