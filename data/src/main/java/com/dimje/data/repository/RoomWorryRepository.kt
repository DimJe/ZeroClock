package com.dimje.data.repository

import com.dimje.data.local.WorryDao
import com.dimje.data.local.WorryEntity
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomWorryRepository @Inject constructor(
    private val dao: WorryDao,
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) : WorryRepository {
    override fun observeAll(): Flow<List<WorryEntry>> =
        dao.observeAll().map { entities ->
            flowLogger.log(DATA_MODULE, "Room 고민 목록 조회", "entityCount=${entities.size}")
            entities.map { entity -> entity.toDomain() }
        }

    override suspend fun getByDate(date: LocalDate): WorryEntry? {
        flowLogger.log(DATA_MODULE, "Room 날짜 조회 요청", "date=$date")
        return dao.getByDate(date.toString())?.toDomain().also { entry ->
            flowLogger.log(DATA_MODULE, "Room 날짜 조회 결과", "date=$date, found=${entry != null}")
        }
    }

    override suspend fun save(
        worry: String,
        response: String,
        date: LocalDate,
        riskLevel: WorryRiskLevel,
    ): WorryEntry {
        flowLogger.log(
            DATA_MODULE,
            "Room 저장 요청",
            "date=$date, worryLength=${worry.length}, responseLength=${response.length}",
        )
        val entity = WorryEntity(
            worry = worry,
            response = response,
            localDate = date.toString(),
            createdAt = System.currentTimeMillis(),
            riskLevel = riskLevel.name,
        )
        return entity.copy(id = dao.insert(entity)).toDomain().also { entry ->
            flowLogger.log(DATA_MODULE, "Room 저장 완료", "entryId=${entry.id}, date=${entry.date}")
        }
    }

    private fun WorryEntity.toDomain() = WorryEntry(
        id = id,
        worry = worry,
        response = response,
        date = LocalDate.parse(localDate),
        createdAt = createdAt,
        riskLevel = riskLevel?.let { value ->
            runCatching { WorryRiskLevel.valueOf(value) }.getOrNull()
        },
    )

    private companion object {
        const val DATA_MODULE = "DATA"
    }
}
