package com.dimje.data.di

import android.content.Context
import androidx.room.Room
import com.dimje.data.ai.FakeComfortResponseGenerator
import com.dimje.data.local.WorryDao
import com.dimje.data.local.ZeroClockDatabase
import com.dimje.data.repository.RoomWorryRepository
import com.dimje.domain.repository.ComfortResponseGenerator
import com.dimje.domain.repository.WorryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {
    @Binds
    @Singleton
    abstract fun bindWorryRepository(repository: RoomWorryRepository): WorryRepository

    @Binds
    @Singleton
    abstract fun bindComfortResponseGenerator(
        generator: FakeComfortResponseGenerator,
    ): ComfortResponseGenerator
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZeroClockDatabase =
        Room.databaseBuilder(
            context,
            ZeroClockDatabase::class.java,
            "zero-clock.db",
        ).build()

    @Provides
    fun provideWorryDao(database: ZeroClockDatabase): WorryDao = database.worryDao()
}
