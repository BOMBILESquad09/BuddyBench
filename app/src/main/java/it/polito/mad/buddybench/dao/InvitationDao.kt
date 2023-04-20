package it.polito.mad.buddybench.dao
import androidx.room.*
import it.polito.mad.buddybench.entities.Invitation


@Dao
interface InvitationDao {

    @Query("SELECT * FROM Invitation")
    fun getAll(): List<Invitation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(invitation: Invitation)

    @Delete
    fun delete(invitation: Invitation)

    @Query("SELECT I.* FROM User U, Invitation I, Reservation R WHERE U.id == I.user AND R.id == I.reservation " +
            "AND U.email == :email AND R.date > :currDate")
    fun getInvitationByEmailAndDate(email: String, currDate: String): List<Invitation>
}