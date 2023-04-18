package it.polito.mad.buddybench.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.buddybench.DTO.CourtCompleteDTO
import it.polito.mad.buddybench.Entities.Court

@Dao
interface CourtDao {

    @Query("SELECT * FROM Court")
    fun getAll(): List<Court>

    @Query("SELECT C.courtName, C.address, C.feeHour, S.sportName AS sport FROM Court C, Sport S WHERE C.sport = S.id")
    fun getAllWithSportInformations(): List<CourtCompleteDTO>

    @Insert(onConflict = REPLACE)
    fun save(court: Court)

    @Delete
    fun delete(court: Court)

}