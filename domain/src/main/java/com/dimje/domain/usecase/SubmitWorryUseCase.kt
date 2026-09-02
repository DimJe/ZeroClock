package com.dimje.domain.usecase

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.model.RejectionReason
import com.dimje.domain.model.SubmitWorryResult
import com.dimje.domain.repository.ComfortResponseRepository
import com.dimje.domain.repository.WorryRepository
import java.time.LocalDate

class SubmitWorryUseCase(
    private val repository: WorryRepository,
    private val responseRepository: ComfortResponseRepository,
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) {
    suspend operator fun invoke(worry: String, date: LocalDate): SubmitWorryResult {
        require(worry.isNotBlank()) { "고민 내용을 입력해 주세요." }
        val trimmedWorry = worry.trim()
        flowLogger.log(DOMAIN_MODULE, "고민 제출 수신", "date=$date, worryLength=${trimmedWorry.length}")
        if (repository.getByDate(date) != null) {
            flowLogger.log(DOMAIN_MODULE, "고민 제출 중단", "date=$date, reason=already_submitted")
            throw AlreadySubmittedTodayException()
        }

        return when (val result = responseRepository.generate(trimmedWorry)) {
            is ComfortResponseResult.Success -> {
                flowLogger.log(
                    DOMAIN_MODULE,
                    "AI 답변 수신",
                    "riskLevel=${result.riskLevel}, responseLength=${result.response.length}",
                )
                SubmitWorryResult.Saved(
                    repository.save(
                        worry = trimmedWorry,
                        response = result.response,
                        date = date,
                        riskLevel = result.riskLevel,
                    ),
                ).also { saved ->
                    flowLogger.log(
                        DOMAIN_MODULE,
                        "고민 저장 결과 수신",
                        "entryId=${saved.entry.id}, date=${saved.entry.date}, riskLevel=${saved.entry.riskLevel}",
                    )
                }
            }

            is ComfortResponseResult.Invalid -> {
                flowLogger.log(DOMAIN_MODULE, "고민 저장 제외", "reason=invalid")
                SubmitWorryResult.Rejected(RejectionReason.INVALID, result.message)
            }

            is ComfortResponseResult.Unknown -> {
                flowLogger.log(DOMAIN_MODULE, "고민 저장 제외", "reason=unknown")
                SubmitWorryResult.Rejected(RejectionReason.UNKNOWN, result.message)
            }
        }
    }

    private companion object {
        const val DOMAIN_MODULE = "DOMAIN"
    }
}
