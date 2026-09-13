package com.dimje.domain.usecase

import com.dimje.domain.repository.OnboardingRepository

class CompleteOnboardingUseCase(private val repository: OnboardingRepository) {
    suspend operator fun invoke() = repository.complete()
}
