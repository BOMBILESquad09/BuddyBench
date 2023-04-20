package it.polito.mad.buddybench.Database

import android.content.Context
import androidx.room.Room

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.polito.mad.buddybench.repositories.UserRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context): CourtReservationDatabase {
        return CourtReservationDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserRepository(appDatabase: CourtReservationDatabase): UserRepository {
        return UserRepository(appDatabase.userDao())
    }

}