package it.polito.mad.buddybench.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import it.polito.mad.buddybench.persistence.dao.CourtDao
import it.polito.mad.buddybench.persistence.dao.CourtTimeDao
import it.polito.mad.buddybench.persistence.dao.InvitationDao
import it.polito.mad.buddybench.persistence.dao.ReservationDao
import it.polito.mad.buddybench.persistence.dao.ReviewDao
import it.polito.mad.buddybench.persistence.dao.SportDao
import it.polito.mad.buddybench.persistence.dao.UnavailableDayCourtDao
import it.polito.mad.buddybench.persistence.dao.UserDao
import it.polito.mad.buddybench.persistence.dao.UserSportDao
import it.polito.mad.buddybench.persistence.entities.*

@Database(
    entities = [
        Sport::class,
        Court::class,
        CourtTime::class,
        Invitation::class,
        Reservation::class,
        UserSport::class,
        User::class,
        UnavailableDayCourt::class,
        CourtFacility::class,
        Facility::class,
        Review::class,
    ], version = 2, exportSchema = true
)
abstract class CourtReservationDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao
    abstract fun courtTimeDao(): CourtTimeDao
    abstract fun invitationDao(): InvitationDao
    abstract fun sportDao(): SportDao
    abstract fun userSportDao(): UserSportDao
    abstract fun reservationDao(): ReservationDao
    abstract fun unavailableDayCourtDao(): UnavailableDayCourtDao

    abstract fun reviewDao(): ReviewDao

}