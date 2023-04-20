package it.polito.mad.buddybench.Database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database
import it.polito.mad.buddybench.DAO.*
import it.polito.mad.buddybench.Entities.*
import it.polito.mad.buddybench.repositories.UserRepository

@Database(
    entities = [
        Sport::class,
        Court::class,
        CourtTime::class,
        Invitation::class,
        Reservation::class,
        UserSport::class,
        User::class
    ], version = 1, exportSchema = true
)
abstract class CourtReservationDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao
    abstract fun courtTimeDao(): CourtTimeDao
    abstract fun invitationDao(): InvitationDao
    abstract fun sportDao(): SportDao
    abstract fun userSportDao(): UserSportDao
    abstract fun reservationDao(): ReservationDao


}