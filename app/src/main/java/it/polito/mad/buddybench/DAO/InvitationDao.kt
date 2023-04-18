package it.polito.mad.buddybench.DAO
import androidx.room.*
import it.polito.mad.buddybench.DTO.InvitationDTOComplete
import it.polito.mad.buddybench.Entities.Invitation


@Dao
interface InvitationDao {

    @Query("SELECT * FROM Invitation")
    fun getAll(): List<Invitation>

    @Query("SELECT UO.name, UO.surname, UO.nickname, UO.birthdate, UO.location, UO.email, UO.reliability," +
            "S.sportName AS sport, C.courtName, C.address, C.feeHour, K.presence, K.confirmed, " +
            "UI.name AS invitedName, UI.surname AS invitedSurname, UI.nickname AS invitedNickname, " +
            "UI.birthdate AS invitedBirthdate, UI.location AS invitedLocation, UI.email AS invitedEmail, " +
            "UI.reliability AS invitedReliability,R.date, R.startTime, R.endTime " +
            "FROM Invitation K, Reservation R, User UO, User UI, Sport S, Court C " +
            "WHERE S.id = C.sport AND R.court = C.id AND R.userOrganizer = UO.id AND R.id = K.reservation AND " +
            "K.user = UI.id")
    fun getAllWithUserInvitedAndReservationInformations(): List<InvitationDTOComplete>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(invitation: Invitation)

    @Delete
    fun delete(invitation: Invitation)

    @Query("SELECT I.* FROM User U, Invitation I, Reservation R WHERE U.id == I.user AND R.id == I.reservation " +
            "AND U.email == :email AND R.date > :currDate")
    fun getInvitationByEmailAndDate(email: String, currDate: String): List<Invitation>
}