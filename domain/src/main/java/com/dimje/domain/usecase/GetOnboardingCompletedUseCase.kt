package com.dimje.domain.usecase

import com.dimje.domain.repository.OnboardingRepository

class GetOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    suspend operator fun invoke(): Boolean = repository.isCompleted()
}
