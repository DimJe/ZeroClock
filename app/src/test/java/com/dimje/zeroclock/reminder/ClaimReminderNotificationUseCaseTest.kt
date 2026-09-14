package com.dimje.zeroclock.reminder

import com.dimje.domain.repository.WorryRepository
import com.dimje.domain.usecase.ClaimReminderNotificationUseCase
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.zeroclock.testing.FakeReminderRepository
import com.dimje.zeroclock.testing.FakeWorryRepository
import com.dimje.zeroclock.testing.worryEntry
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class ClaimReminderNotificationUseCaseTest {
    private val scheduled = Instant.parse("2026-09-30T14:00:00Z")
    private val date = LocalDate.of(2026, 9, 30)

    @Test
    fun `미작성 날짜는 한 번만 선점하고 다음 날 다시 알릴 수 있다`() = runBlocking {
        val useCase = ClaimReminderNotificationUseCase(GetWorryByDateUseCase(FakeWorryRepository()), FakeReminderRepository())
        assertEquals(date, useCase(scheduled, scheduled))
        assertNull(useCase(scheduled, scheduled))
        assertEquals(date.plusDays(1), useCase(scheduled.plusSeconds(86400), scheduled.plusSeconds(86400)))
    }

    @Test
    fun `작성한 날이나 자정 이후에는 알림 날짜를 선점하지 않는다`() = runBlocking {
        val reminders = FakeReminderRepository()
        val recorded = ClaimReminderNotificationUseCase(GetWorryByDateUseCase(FakeWorryRepository(listOf(worryEntry(1, date)))), reminders)
        assertNull(recorded(scheduled, scheduled))
        val empty = ClaimReminderNotificationUseCase(GetWorryByDateUseCase(FakeWorryRepository()), reminders)
        assertNull(empty(scheduled.plusSeconds(3600), scheduled))
        assertEquals(date, empty(scheduled, scheduled))
    }

    @Test
    fun `기록 조회 실패는 미작성으로 처리하지 않고 날짜도 선점하지 않는다`() {
        val reminders = FakeReminderRepository()
        val failing = object : WorryRepository by FakeWorryRepository() {
            override suspend fun getByDate(date: LocalDate) = error("테스트 조회 실패")
        }
        val useCase = ClaimReminderNotificationUseCase(GetWorryByDateUseCase(failing), reminders)
        assertThrows(IllegalStateException::class.java) { runBlocking { useCase(scheduled, scheduled) } }
        runBlocking {
            val retry = ClaimReminderNotificationUseCase(GetWorryByDateUseCase(FakeWorryRepository()), reminders)
            assertEquals(date, retry(scheduled, scheduled))
        }
    }
}
