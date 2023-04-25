package it.polito.mad.buddybench.database

import androidx.room.Database
import androidx.room.RoomDatabase
import it.polito.mad.buddybench.dao.*
import it.polito.mad.buddybench.entities.*

@Database(
    entities = [
        Sport::class,
        Court::class,
        CourtTime::class,
        Invitation::class,
        Reservation::class,
        UserSport::class,
        User::class,
        UnavailableDayCourt::class
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

}