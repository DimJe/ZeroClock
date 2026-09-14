package com.dimje.domain.reminder

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object ReminderPolicy {
    val zone: ZoneId = ZoneId.of("Asia/Seoul")
    private val reminderTime = LocalTime.of(23, 0)

    fun nextTrigger(now: Instant): Instant {
        val localNow = now.atZone(zone)
        val todayTrigger = localNow.toLocalDate().atTime(reminderTime).atZone(zone)
        return if (todayTrigger.toInstant().isAfter(now)) todayTrigger.toInstant()
        else todayTrigger.plusDays(1).toInstant()
    }

    fun eligibleDate(now: Instant, scheduledAt: Instant): LocalDate? {
        val localNow = now.atZone(zone)
        return localNow.toLocalDate().takeIf {
            !now.isBefore(scheduledAt) &&
                it == scheduledAt.atZone(zone).toLocalDate() &&
                scheduledAt.atZone(zone).toLocalTime() == reminderTime &&
                !localNow.toLocalTime().isBefore(reminderTime)
        }
    }
}
