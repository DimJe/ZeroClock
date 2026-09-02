package com.dimje.domain.usecase

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.repository.WorryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class ObserveWorriesUseCase(
    private val repository: WorryRepository,
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) {
    operator fun invoke(): Flow<List<WorryEntry>> = repository.observeAll().onEach { entries ->
        flowLogger.log(DOMAIN_MODULE, "고민 목록 전달", "entryCount=${entries.size}")
    }

    private companion object {
        const val DOMAIN_MODULE = "DOMAIN"
    }
}
