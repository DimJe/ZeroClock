package com.dimje.domain.usecase

import com.dimje.domain.repository.ReminderRepository

class ConsumeReminderPermissionRequestUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(): Boolean = repository.consumePermissionRequest()
}
