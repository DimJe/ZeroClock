package com.dimje.zeroclock.di

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.repository.ComfortResponseRepository
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
        responseRepository: ComfortResponseRepository,
        flowLogger: DataFlowLogger,
    ) = SubmitWorryUseCase(repository, responseRepository, flowLogger)

    @Provides
    fun provideAnalyzeWorriesUseCase(flowLogger: DataFlowLogger) = AnalyzeWorriesUseCase(flowLogger)
}
