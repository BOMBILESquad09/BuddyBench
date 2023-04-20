package it.polito.mad.buddybench.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database
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
        User::class
    ], version = 1, exportSchema = true
)
abstract class CourtReservationDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao
    abstract fun courtTimeDao(): CourtTimeDao
    abstract fun invitationDao(): InvitationDao
    abstract fun reservationDao(): ReservationDao
    abstract fun sportDao(): SportDao
    abstract fun userSportDao(): UserSportDao

    companion object {
        @Volatile
        private var INSTANCE: CourtReservationDatabase? = null
        fun getDatabase(context: Context): CourtReservationDatabase =
            (INSTANCE ?: synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CourtReservationDatabase::class.java,
                    "CourtReservationDatabase"
                ).createFromAsset("database/CourtReservationDB.db")
                    .build()
                INSTANCE = i
                INSTANCE
            })!!
    }
}