package it.polito.mad.buddybench.Database

import android.content.Context
import androidx.room.Room

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.polito.mad.buddybench.repositories.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provide(@ApplicationContext context: Context): CourtReservationDatabase {
        return Room.databaseBuilder(context, CourtReservationDatabase::class.java,
            "courtReservation.db")
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserRepo(appDatabase: CourtReservationDatabase): UserRepository {
        return UserRepository(appDatabase.userDao())
    }

    @Singleton
    @Provides
    fun provideReservationRepo(appDatabase: CourtReservationDatabase): ReservationRepository {
        return ReservationRepository(
            appDatabase.reservationDao(),
            appDatabase.userDao(),
            appDatabase.courtDao()
        )
    }

    @Singleton
    @Provides
    fun provideCourtRepo(appDatabase: CourtReservationDatabase): CourtRepository {
        return CourtRepository(
            appDatabase.courtDao(),
            appDatabase.sportDao()
        )
    }

    @Singleton
    @Provides
    fun provideCourtTimeRepo(appDatabase: CourtReservationDatabase): CourtTimeRepository {
        return CourtTimeRepository(
            appDatabase.courtTimeDao(),
            appDatabase.courtDao(),
            appDatabase.sportDao()
        )
    }

    @Singleton
    @Provides
    fun provideInvitationRepo(appDatabase: CourtReservationDatabase): InvitationRepository {
        return InvitationRepository(
            appDatabase.invitationDao(),
            appDatabase.userDao(),
            appDatabase.reservationDao()
        )
    }

    @Singleton
    @Provides
    fun provideSportRepo(appDatabase: CourtReservationDatabase): SportRepository {
        return SportRepository(
            appDatabase.sportDao()
        )
    }

    @Singleton
    @Provides
    fun provideUserSportRepo(appDatabase: CourtReservationDatabase): UserSportRepository {
        return UserSportRepository(
            appDatabase.userSportDao(),
            appDatabase.sportDao(),
            appDatabase.userDao()
        )
    }

}