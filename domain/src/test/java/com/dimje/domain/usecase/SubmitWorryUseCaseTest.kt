package com.dimje.domain.usecase

import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.model.SubmitWorryResult
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.domain.repository.ComfortResponseRepository
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SubmitWorryUseCaseTest {
    @Test
    fun `같은 날짜에 두 번 제출하면 두 번째 요청을 거부한다`() = runBlocking {
        val repository = InMemoryWorryRepository()
        val useCase = SubmitWorryUseCase(repository, FixedComfortResponseRepository())
        val date = LocalDate.of(2026, 9, 2)

        useCase("첫 번째 고민", date)

        assertThrows(AlreadySubmittedTodayException::class.java) {
            runBlocking { useCase("두 번째 고민", date) }
        }
        assertEquals(1, repository.entries.value.size)
    }

    @Test
    fun `서로 다른 날짜의 고민은 각각 저장한다`() = runBlocking {
        val repository = InMemoryWorryRepository()
        val useCase = SubmitWorryUseCase(repository, FixedComfortResponseRepository())

        useCase("어제의 고민", LocalDate.of(2026, 9, 1))
        useCase("오늘의 고민", LocalDate.of(2026, 9, 2))

        assertEquals(2, repository.entries.value.size)
    }

    @Test
    fun `유효하지 않은 입력 결과는 저장하지 않아 다시 제출할 수 있다`() = runBlocking {
        val repository = InMemoryWorryRepository()
        val rejectedUseCase = SubmitWorryUseCase(repository, InvalidComfortResponseRepository())
        val date = LocalDate.of(2026, 9, 2)

        val rejected = rejectedUseCase("asdf", date)
        val saved = SubmitWorryUseCase(repository, FixedComfortResponseRepository())("새로운 고민", date)

        assertEquals(SubmitWorryResult.Rejected::class, rejected::class)
        assertEquals(SubmitWorryResult.Saved::class, saved::class)
        assertEquals(1, repository.entries.value.size)
    }
}

private class FixedComfortResponseRepository : ComfortResponseRepository {
    override suspend fun generate(worry: String): ComfortResponseResult = ComfortResponseResult.Success(
        response = "따뜻한 답변",
        riskLevel = WorryRiskLevel.NORMAL,
        isGenerated = true,
    )
}

private class InvalidComfortResponseRepository : ComfortResponseRepository {
    override suspend fun generate(worry: String): ComfortResponseResult =
        ComfortResponseResult.Invalid("입력을 확인해 주세요.")
}

private class InMemoryWorryRepository : WorryRepository {
    val entries = MutableStateFlow<List<WorryEntry>>(emptyList())

    override fun observeAll(): Flow<List<WorryEntry>> = entries

    override suspend fun getByDate(date: LocalDate): WorryEntry? =
        entries.value.firstOrNull { it.date == date }

    override suspend fun save(
        worry: String,
        response: String,
        date: LocalDate,
        riskLevel: WorryRiskLevel,
    ): WorryEntry {
        val entry = WorryEntry(
            id = entries.value.size.toLong() + 1L,
            worry = worry,
            response = response,
            date = date,
            createdAt = 0L,
            riskLevel = riskLevel,
        )
        entries.value += entry
        return entry
    }
}
