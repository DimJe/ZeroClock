package com.dimje.domain.usecase

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.repository.ComfortResponseGenerator
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class AlreadySubmittedTodayException : IllegalStateException("오늘의 고민은 이미 기록했습니다.")

class ObserveWorriesUseCase(
    private val repository: WorryRepository,
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) {
    operator fun invoke(): Flow<List<WorryEntry>> = repository.observeAll().onEach { entries ->
        flowLogger.log(DOMAIN_MODULE, "고민 목록 전달", "entryCount=${entries.size}")
    }
}

class GetWorryByDateUseCase(
    private val repository: WorryRepository,
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) {
    suspend operator fun invoke(date: LocalDate): WorryEntry? {
        flowLogger.log(DOMAIN_MODULE, "날짜별 고민 요청", "date=$date")
        return repository.getByDate(date).also { entry ->
            flowLogger.log(DOMAIN_MODULE, "날짜별 고민 수신", "date=$date, found=${entry != null}")
        }
    }
}

class SubmitWorryUseCase(
    private val repository: WorryRepository,
    private val responseGenerator: ComfortResponseGenerator,
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) {
    suspend operator fun invoke(worry: String, date: LocalDate): WorryEntry {
        require(worry.isNotBlank()) { "고민 내용을 입력해 주세요." }
        val trimmedWorry = worry.trim()
        flowLogger.log(DOMAIN_MODULE, "고민 제출 수신", "date=$date, worryLength=${trimmedWorry.length}")
        if (repository.getByDate(date) != null) {
            flowLogger.log(DOMAIN_MODULE, "고민 제출 중단", "date=$date, reason=already_submitted")
            throw AlreadySubmittedTodayException()
        }

        val response = responseGenerator.generate(trimmedWorry)
        flowLogger.log(DOMAIN_MODULE, "AI 답변 수신", "responseLength=${response.length}")
        return repository.save(trimmedWorry, response, date).also { entry ->
            flowLogger.log(DOMAIN_MODULE, "고민 저장 결과 수신", "entryId=${entry.id}, date=${entry.date}")
        }
    }
}

private const val DOMAIN_MODULE = "DOMAIN"
