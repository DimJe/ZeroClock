package com.dimje.zeroclock.di

import com.dimje.domain.repository.ComfortResponseGenerator
import com.dimje.domain.repository.WorryRepository
import com.dimje.domain.usecase.AnalyzeWorriesUseCase
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.domain.usecase.SubmitWorryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideObserveWorriesUseCase(repository: WorryRepository) = ObserveWorriesUseCase(repository)

    @Provides
    fun provideGetWorryByDateUseCase(repository: WorryRepository) = GetWorryByDateUseCase(repository)

    @Provides
    fun provideSubmitWorryUseCase(
        repository: WorryRepository,
        responseGenerator: ComfortResponseGenerator,
    ) = SubmitWorryUseCase(repository, responseGenerator)

    @Provides
    fun provideAnalyzeWorriesUseCase() = AnalyzeWorriesUseCase()
}
