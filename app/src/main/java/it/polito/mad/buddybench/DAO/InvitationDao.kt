import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.buddybench.Entities.Invitation

@Dao
interface InvitationDao {

    @Query("SELECT * FROM INVITATION")
    fun getAll(): LiveData<List<Invitation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(invitation: Invitation)

    @Delete
    fun delete(invitation: Invitation)
}