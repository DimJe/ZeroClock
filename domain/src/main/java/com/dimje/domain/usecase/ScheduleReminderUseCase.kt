package com.dimje.domain.usecase

import com.dimje.domain.reminder.ReminderPolicy
import com.dimje.domain.repository.ReminderRepository
import java.time.Instant

class ScheduleReminderUseCase(private val repository: ReminderRepository) {
    operator fun invoke(now: Instant = Instant.now()) {
        repository.scheduleAt(ReminderPolicy.nextTrigger(now))
    }
}
