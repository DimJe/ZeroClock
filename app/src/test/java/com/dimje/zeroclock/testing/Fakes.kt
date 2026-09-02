package com.dimje.zeroclock.testing

import com.dimje.domain.model.WorryEntry
import com.dimje.domain.repository.ComfortResponseGenerator
import com.dimje.domain.repository.WorryRepository
import com.dimje.domain.time.DateProvider
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDateProvider(initialDate: LocalDate) : DateProvider {
    private val date = MutableStateFlow(initialDate)

    override fun today(): LocalDate = date.value

    override fun observeDateChanges(): Flow<LocalDate> = date

    fun moveTo(newDate: LocalDate) {
        date.value = newDate
    }
}

class FakeWorryRepository(
    initialEntries: List<WorryEntry> = emptyList(),
) : WorryRepository {
    private val entries = MutableStateFlow(initialEntries)

    override fun observeAll(): Flow<List<WorryEntry>> = entries

    override suspend fun getByDate(date: LocalDate): WorryEntry? =
        entries.value.firstOrNull { it.date == date }

    override suspend fun save(worry: String, response: String, date: LocalDate): WorryEntry {
        val entry = WorryEntry(
            id = (entries.value.maxOfOrNull { it.id } ?: 0L) + 1L,
            worry = worry,
            response = response,
            date = date,
            createdAt = 0L,
        )
        entries.value += entry
        return entry
    }
}

class FakeComfortResponseGenerator(
    private val response: String = "오늘도 충분히 잘해냈어요.",
) : ComfortResponseGenerator {
    override suspend fun generate(worry: String): String = response
}

fun worryEntry(id: Long, date: LocalDate): WorryEntry = WorryEntry(
    id = id,
    worry = "테스트 고민 $id",
    response = "테스트 답변 $id",
    date = date,
    createdAt = id,
)
