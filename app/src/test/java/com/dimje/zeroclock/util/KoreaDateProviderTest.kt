package com.dimje.zeroclock.util

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class KoreaDateProviderTest {
    @Test
    fun `한국 시간 자정 전에는 같은 날짜와 남은 시간을 반환한다`() {
        val clock = Clock.fixed(Instant.parse("2026-09-01T14:59:59Z"), ZoneOffset.UTC)
        val provider = KoreaDateProvider(clock)

        assertEquals(LocalDate.of(2026, 9, 1), provider.today())
        assertEquals(1_000L, provider.millisecondsUntilNextDate())
    }

    @Test
    fun `한국 시간 자정이 되면 다음 날짜를 반환한다`() {
        val clock = Clock.fixed(Instant.parse("2026-09-01T15:00:00Z"), ZoneOffset.UTC)
        val provider = KoreaDateProvider(clock)

        assertEquals(LocalDate.of(2026, 9, 2), provider.today())
    }
}
