package com.dimje.zeroclock.di

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.repository.ComfortResponseRepository
import com.dimje.domain.repository.WorryRepository
import com.dimje.domain.repository.OnboardingRepository
import com.dimje.domain.repository.ReminderRepository
import com.dimje.domain.usecase.ScheduleReminderUseCase
import com.dimje.domain.usecase.ConsumeReminderPermissionRequestUseCase
import com.dimje.domain.usecase.ClaimReminderNotificationUseCase
import com.dimje.domain.usecase.GetOnboardingCompletedUseCase
import com.dimje.domain.usecase.CompleteOnboardingUseCase
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
    fun provideScheduleReminderUseCase(repository: ReminderRepository) = ScheduleReminderUseCase(repository)

    @Provides
    fun provideConsumeReminderPermissionRequestUseCase(repository: ReminderRepository) =
        ConsumeReminderPermissionRequestUseCase(repository)

    @Provides
    fun provideClaimReminderNotificationUseCase(
        getWorryByDate: GetWorryByDateUseCase,
        repository: ReminderRepository,
    ) = ClaimReminderNotificationUseCase(getWorryByDate, repository)

    @Provides
    fun provideGetOnboardingCompletedUseCase(repository: OnboardingRepository) =
        GetOnboardingCompletedUseCase(repository)

    @Provides
    fun provideCompleteOnboardingUseCase(repository: OnboardingRepository) =
        CompleteOnboardingUseCase(repository)

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
