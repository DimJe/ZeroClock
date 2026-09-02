package com.dimje.zeroclock.di

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.repository.ComfortResponseGenerator
import com.dimje.domain.repository.WorryRepository
import com.dimje.domain.time.DateProvider
import com.dimje.domain.usecase.AnalyzeWorriesUseCase
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.domain.usecase.SubmitWorryUseCase
import com.dimje.zeroclock.util.AndroidDataFlowLogger
import com.dimje.zeroclock.util.KoreaDateProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {
    @Binds
    @Singleton
    abstract fun bindDataFlowLogger(logger: AndroidDataFlowLogger): DataFlowLogger

    @Binds
    @Singleton
    abstract fun bindDateProvider(provider: KoreaDateProvider): DateProvider
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideObserveWorriesUseCase(
        repository: WorryRepository,
        flowLogger: DataFlowLogger,
    ) = ObserveWorriesUseCase(repository, flowLogger)

    @Provides
    fun provideGetWorryByDateUseCase(
        repository: WorryRepository,
        flowLogger: DataFlowLogger,
    ) = GetWorryByDateUseCase(repository, flowLogger)

    @Provides
    fun provideSubmitWorryUseCase(
        repository: WorryRepository,
        responseGenerator: ComfortResponseGenerator,
        flowLogger: DataFlowLogger,
    ) = SubmitWorryUseCase(repository, responseGenerator, flowLogger)

    @Provides
    fun provideAnalyzeWorriesUseCase(flowLogger: DataFlowLogger) = AnalyzeWorriesUseCase(flowLogger)
}
