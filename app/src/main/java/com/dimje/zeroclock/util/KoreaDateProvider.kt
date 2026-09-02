package com.dimje.zeroclock.util

import com.dimje.domain.time.DateProvider
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class KoreaDateProvider internal constructor(
    private val clock: Clock,
) : DateProvider {
    @Inject
    constructor() : this(Clock.system(KOREA_ZONE))

    override fun today(): LocalDate = Instant.now(clock).atZone(KOREA_ZONE).toLocalDate()

    override fun observeDateChanges(): Flow<LocalDate> = flow {
        var lastDate: LocalDate? = null
        while (currentCoroutineContext().isActive) {
            val currentDate = today()
            if (currentDate != lastDate) {
                emit(currentDate)
                lastDate = currentDate
            }
            delay(millisecondsUntilNextDate())
        }
    }

    internal fun millisecondsUntilNextDate(): Long {
        val now = Instant.now(clock).atZone(KOREA_ZONE)
        val nextDate = now.toLocalDate().plusDays(1).atStartOfDay(KOREA_ZONE)
        return Duration.between(now, nextDate).toMillis().coerceAtLeast(1L)
    }

    private companion object {
        val KOREA_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
    }
}
