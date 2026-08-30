package com.dimje.data.repository

import com.dimje.data.local.WorryDao
import com.dimje.data.local.WorryEntity
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomWorryRepository @Inject constructor(
    private val dao: WorryDao,
) : WorryRepository {
    override fun observeAll(): Flow<List<WorryEntry>> =
        dao.observeAll().map { entities -> entities.map { entity -> entity.toDomain() } }

    override suspend fun getByDate(date: LocalDate): WorryEntry? =
        dao.getByDate(date.toString())?.toDomain()

    override suspend fun save(
        worry: String,
        response: String,
        date: LocalDate,
    ): WorryEntry {
        val entity = WorryEntity(
            worry = worry,
            response = response,
            localDate = date.toString(),
            createdAt = System.currentTimeMillis(),
        )
        return entity.copy(id = dao.insert(entity)).toDomain()
    }

    private fun WorryEntity.toDomain() = WorryEntry(
        id = id,
        worry = worry,
        response = response,
        date = LocalDate.parse(localDate),
        createdAt = createdAt,
    )
}
