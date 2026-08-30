package com.dimje.domain.usecase

import com.dimje.domain.model.WorryEntry
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AnalyzeWorriesUseCaseTest {
    private val useCase = AnalyzeWorriesUseCase()

    @Test
    fun `서로 다른 날짜의 기록이 15개 미만이면 분석하지 않는다`() {
        val entries = createEntries(14, "회사 업무가 걱정돼")

        assertNull(useCase(entries))
    }

    @Test
    fun `서로 다른 날짜의 기록이 15개면 주요 고민을 분석한다`() {
        val entries = createEntries(15, "회사 업무와 실수가 걱정돼")

        val result = requireNotNull(useCase(entries))

        assertEquals(15, result.entryCount)
        assertEquals("일과 성취에 대한 부담", result.mainConcern)
    }

    @Test
    fun `같은 날짜의 중복 기록은 한 번만 계산한다`() {
        val entries = createEntries(14, "미래가 불안해").toMutableList().apply {
            add(first().copy(id = 100))
        }

        assertNull(useCase(entries))
    }

    private fun createEntries(count: Int, worry: String): List<WorryEntry> =
        (0 until count).map { index ->
            WorryEntry(
                id = index.toLong(),
                worry = worry,
                response = "답변",
                date = LocalDate.of(2026, 1, 1).plusDays(index.toLong()),
                createdAt = index.toLong(),
            )
        }
}
