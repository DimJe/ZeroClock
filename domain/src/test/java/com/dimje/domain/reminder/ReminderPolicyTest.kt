package com.dimje.domain.reminder

import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReminderPolicyTest {
    @Test
    fun `한국 밤 11시 전이면 오늘 이후면 다음 날을 예약한다`() {
        val cases = mapOf(
            "2026-09-30T13:59:59Z" to "2026-09-30T14:00:00Z",
            "2026-09-30T14:00:00Z" to "2026-10-01T14:00:00Z",
            "2026-09-30T15:00:00Z" to "2026-10-01T14:00:00Z",
        )
        cases.forEach { (now, expected) ->
            assertEquals(Instant.parse(expected), ReminderPolicy.nextTrigger(Instant.parse(now)))
        }
    }

    @Test
    fun `예정일 밤 11시부터 자정 전까지만 알림을 허용한다`() {
        val scheduled = Instant.parse("2026-09-30T14:00:00Z")
        assertEquals(LocalDate.of(2026, 9, 30), ReminderPolicy.eligibleDate(scheduled, scheduled))
        assertEquals(LocalDate.of(2026, 9, 30), ReminderPolicy.eligibleDate(scheduled.plusSeconds(3599), scheduled))
        assertNull(ReminderPolicy.eligibleDate(scheduled.minusSeconds(1), scheduled))
        assertNull(ReminderPolicy.eligibleDate(scheduled.plusSeconds(3600), scheduled))
        assertNull(ReminderPolicy.eligibleDate(scheduled.plusSeconds(86400), scheduled))
    }
}
