package com.dimje.domain.usecase

import com.dimje.domain.reminder.ReminderPolicy
import com.dimje.domain.repository.ReminderRepository
import java.time.Instant
import java.time.LocalDate

class ClaimReminderNotificationUseCase(
    private val getWorryByDate: GetWorryByDateUseCase,
    private val reminderRepository: ReminderRepository,
) {
    suspend operator fun invoke(now: Instant, scheduledAt: Instant): LocalDate? {
        val date = ReminderPolicy.eligibleDate(now, scheduledAt) ?: return null
        if (getWorryByDate(date) != null) return null
        return date.takeIf { reminderRepository.claimNotificationDate(it) }
    }
}
