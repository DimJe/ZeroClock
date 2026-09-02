package com.dimje.domain.usecase

import com.dimje.domain.model.WorryEntry
import com.dimje.domain.repository.ComfortResponseGenerator
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
        val useCase = SubmitWorryUseCase(repository, FixedResponseGenerator())
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
        val useCase = SubmitWorryUseCase(repository, FixedResponseGenerator())

        useCase("어제의 고민", LocalDate.of(2026, 9, 1))
        useCase("오늘의 고민", LocalDate.of(2026, 9, 2))

        assertEquals(2, repository.entries.value.size)
    }
}

private class FixedResponseGenerator : ComfortResponseGenerator {
    override suspend fun generate(worry: String): String = "따뜻한 답변"
}

private class InMemoryWorryRepository : WorryRepository {
    val entries = MutableStateFlow<List<WorryEntry>>(emptyList())

    override fun observeAll(): Flow<List<WorryEntry>> = entries

    override suspend fun getByDate(date: LocalDate): WorryEntry? =
        entries.value.firstOrNull { it.date == date }

    override suspend fun save(worry: String, response: String, date: LocalDate): WorryEntry {
        val entry = WorryEntry(
            id = entries.value.size.toLong() + 1L,
            worry = worry,
            response = response,
            date = date,
            createdAt = 0L,
        )
        entries.value += entry
        return entry
    }
}
