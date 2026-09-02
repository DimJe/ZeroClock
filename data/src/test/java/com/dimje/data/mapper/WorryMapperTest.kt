package com.dimje.data.mapper

import com.dimje.domain.model.WorryRiskLevel
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class WorryMapperTest {
    @Test
    fun `저장 정보와 Room 엔티티를 도메인 모델로 변환한다`() {
        val date = LocalDate.of(2026, 9, 3)
        val entity = toWorryEntity(
            worry = "내일 일정이 걱정돼요.",
            response = "하나씩 준비해도 충분해요.",
            date = date,
            createdAt = 1234L,
            riskLevel = WorryRiskLevel.CONCERN,
        ).copy(id = 7L)

        val domain = entity.toDomain()

        assertEquals(7L, domain.id)
        assertEquals("내일 일정이 걱정돼요.", domain.worry)
        assertEquals("하나씩 준비해도 충분해요.", domain.response)
        assertEquals(date, domain.date)
        assertEquals(1234L, domain.createdAt)
        assertEquals(WorryRiskLevel.CONCERN, domain.riskLevel)
    }
}
