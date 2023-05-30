package it.polito.mad.buddybench.persistence.firebaseRepositories

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun createUserRepository(): UserRepository{
        return UserRepository()
    }

}