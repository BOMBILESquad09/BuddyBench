package it.polito.mad.buddybench.persistence.dao
import androidx.room.*
import it.polito.mad.buddybench.persistence.entities.Invitation
import it.polito.mad.buddybench.persistence.entities.InvitationWithReservationAndUser


@Dao
interface InvitationDao {

    @Query("SELECT * FROM Invitation")
    fun getAll(): List<InvitationWithReservationAndUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(invitation: Invitation)

    @Delete
    fun delete(invitation: Invitation)

}