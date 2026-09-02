package com.dimje.data.di

import com.dimje.data.local.datasource.RoomWorryLocalDataSource
import com.dimje.data.local.datasource.WorryLocalDataSource
import com.dimje.data.remote.datasource.ComfortResponseRemoteDataSource
import com.dimje.data.remote.datasource.SupabaseComfortResponseRemoteDataSource
import com.dimje.data.repository.ComfortResponseRepositoryImpl
import com.dimje.data.repository.WorryRepositoryImpl
import com.dimje.domain.repository.ComfortResponseRepository
import com.dimje.domain.repository.WorryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {
    @Binds
    @Singleton
    abstract fun bindWorryLocalDataSource(dataSource: RoomWorryLocalDataSource): WorryLocalDataSource

    @Binds
    @Singleton
    abstract fun bindComfortResponseRemoteDataSource(
        dataSource: SupabaseComfortResponseRemoteDataSource,
    ): ComfortResponseRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindWorryRepository(repository: WorryRepositoryImpl): WorryRepository

    @Binds
    @Singleton
    abstract fun bindComfortResponseRepository(
        repository: ComfortResponseRepositoryImpl,
    ): ComfortResponseRepository
}
