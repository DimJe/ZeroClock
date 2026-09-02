package com.dimje.data.repository

import com.dimje.data.local.WorryEntity
import com.dimje.data.local.datasource.WorryLocalDataSource
import com.dimje.data.mapper.toDomain
import com.dimje.data.mapper.toWorryEntity
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorryRepositoryImpl @Inject constructor(
    private val localDataSource: WorryLocalDataSource,
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) : WorryRepository {
    override fun observeAll(): Flow<List<WorryEntry>> =
        localDataSource.observeAll().map { entities ->
            flowLogger.log(DATA_MODULE, "Room 고민 목록 조회", "entityCount=${entities.size}")
            entities.map(WorryEntity::toDomain)
        }

    override suspend fun getByDate(date: LocalDate): WorryEntry? {
        flowLogger.log(DATA_MODULE, "Room 날짜 조회 요청", "date=$date")
        return localDataSource.getByDate(date.toString())?.toDomain().also { entry ->
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
        val entity = toWorryEntity(
            worry = worry,
            response = response,
            date = date,
            createdAt = System.currentTimeMillis(),
            riskLevel = riskLevel,
        )
        return localDataSource.save(entity).toDomain().also { entry ->
            flowLogger.log(DATA_MODULE, "Room 저장 완료", "entryId=${entry.id}, date=${entry.date}")
        }
    }

    private companion object {
        const val DATA_MODULE = "DATA"
    }
}
