package com.dimje.domain.usecase

import com.dimje.domain.model.WorryEntry
import com.dimje.domain.repository.ComfortResponseGenerator
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class AlreadySubmittedTodayException : IllegalStateException("오늘의 고민은 이미 기록했습니다.")

class ObserveWorriesUseCase(
    private val repository: WorryRepository,
) {
    operator fun invoke(): Flow<List<WorryEntry>> = repository.observeAll()
}

class GetWorryByDateUseCase(
    private val repository: WorryRepository,
) {
    suspend operator fun invoke(date: LocalDate): WorryEntry? = repository.getByDate(date)
}

class SubmitWorryUseCase(
    private val repository: WorryRepository,
    private val responseGenerator: ComfortResponseGenerator,
) {
    suspend operator fun invoke(worry: String, date: LocalDate): WorryEntry {
        require(worry.isNotBlank()) { "고민 내용을 입력해 주세요." }
        if (repository.getByDate(date) != null) throw AlreadySubmittedTodayException()

        val response = responseGenerator.generate(worry.trim())
        return repository.save(worry.trim(), response, date)
    }
}
