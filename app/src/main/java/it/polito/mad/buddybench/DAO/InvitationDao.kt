package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.Entities.Invitation


@Dao
interface InvitationDao {

    @Query("SELECT * FROM Invitation")
    fun getAll(): List<Invitation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(invitation: Invitation)

    @Delete
    fun delete(invitation: Invitation)
}