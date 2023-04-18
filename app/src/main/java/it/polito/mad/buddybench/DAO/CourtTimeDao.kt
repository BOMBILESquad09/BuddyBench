package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.DTO.CourtTimeCompleteDTO
import it.polito.mad.buddybench.Entities.CourtTime

@Dao
interface CourtTimeDao {

    @Query("SELECT * FROM CourtTime")
    fun getAll(): List<CourtTime>

    @Query("SELECT CT.openingTime, CT.closingTime, C.feeHour, S.sportName AS sport, C.courtName, C.address, CT.dayOfWeek FROM CourtTime CT, Court C, Sport S WHERE CT.court = C.id AND C.sport = S.id")
    fun getAllWithCourtInformations(): List<CourtTimeCompleteDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(courtTime: CourtTime)

    @Delete
    fun delete(courtTime: CourtTime)

}