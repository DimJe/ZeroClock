package com.dimje.domain.usecase

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate

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

    private companion object {
        const val DOMAIN_MODULE = "DOMAIN"
    }
}
