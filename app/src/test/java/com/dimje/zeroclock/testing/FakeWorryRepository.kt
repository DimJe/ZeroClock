package com.dimje.zeroclock.testing

import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWorryRepository(
    initialEntries: List<WorryEntry> = emptyList(),
) : WorryRepository {
    private val entries = MutableStateFlow(initialEntries)

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
            id = (entries.value.maxOfOrNull { it.id } ?: 0L) + 1L,
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
