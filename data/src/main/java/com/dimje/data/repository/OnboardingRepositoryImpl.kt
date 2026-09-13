package com.dimje.data.repository

import com.dimje.data.local.datasource.OnboardingLocalDataSource
import com.dimje.domain.repository.OnboardingRepository
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val localDataSource: OnboardingLocalDataSource,
) : OnboardingRepository {
    override suspend fun isCompleted(): Boolean = localDataSource.isCompleted()
    override suspend fun complete() = localDataSource.complete()
}
