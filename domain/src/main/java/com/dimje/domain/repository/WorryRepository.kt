package com.dimje.domain.repository

import com.dimje.domain.model.WorryEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface WorryRepository {
    fun observeAll(): Flow<List<WorryEntry>>

    suspend fun getByDate(date: LocalDate): WorryEntry?

    suspend fun save(worry: String, response: String, date: LocalDate): WorryEntry
}
