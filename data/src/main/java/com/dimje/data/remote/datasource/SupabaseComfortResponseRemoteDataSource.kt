package com.dimje.data.remote.datasource

import com.dimje.data.remote.SupabaseWorryResponseService
import com.dimje.data.remote.model.WorryResponseDto
import com.dimje.data.remote.model.WorryResponseRequest
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.model.WorryRiskLevel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class SupabaseComfortResponseRemoteDataSource @Inject constructor(
    private val service: SupabaseWorryResponseService,
    private val flowLogger: DataFlowLogger,
) : ComfortResponseRemoteDataSource {
    override suspend fun generate(worry: String): ComfortResponseResult {
        flowLogger.log(DATA_MODULE, "Supabase 요청 전송", "worryLength=${worry.length}")
        val response = try {
            service.generate(WorryResponseRequest(worry))
        } catch (error: CancellationException) {
            throw error
        } catch (error: HttpException) {
            flowLogger.log(DATA_MODULE, "Supabase 오류 응답 수신", "statusCode=${error.code()}")
            throw WorryResponseApiException(
                message = errorMessage(error.code()),
                cause = error,
            )
        } catch (error: Exception) {
            flowLogger.log(DATA_MODULE, "Supabase 요청 실패", "reason=network")
            throw WorryResponseApiException(
                message = "위로 답변을 불러오지 못했어요. 네트워크 연결을 확인해 주세요.",
                cause = error,
            )
        }

        return response.toDomain().also { result ->
            val details = when (result) {
                is ComfortResponseResult.Success ->
                    "status=SUCCESS, riskLevel=${result.riskLevel}, responseLength=${result.response.length}"
                is ComfortResponseResult.Invalid -> "status=INVALID"
                is ComfortResponseResult.Unknown -> "status=UNKNOWN"
            }
            flowLogger.log(DATA_MODULE, "Supabase 답변 수신", details)
        }
    }

    private fun WorryResponseDto.toDomain(): ComfortResponseResult = when (status?.uppercase()) {
        STATUS_SUCCESS -> {
            val safeResponse = response?.trim()
            val safeRiskLevel = riskLevel?.uppercase()?.let { value ->
                runCatching { WorryRiskLevel.valueOf(value) }.getOrNull()
            }
            if (safeResponse.isNullOrEmpty() || safeRiskLevel == null) {
                flowLogger.log(DATA_MODULE, "Supabase 응답 파싱 실패", "reason=invalid_success_response")
                ComfortResponseResult.Unknown(UNKNOWN_MESSAGE)
            } else {
                ComfortResponseResult.Success(
                    response = safeResponse,
                    riskLevel = safeRiskLevel,
                    isGenerated = isGenerated == true,
                )
            }
        }

        STATUS_INVALID -> ComfortResponseResult.Invalid(message?.trim().orEmpty().ifBlank { INVALID_MESSAGE })
        STATUS_UNKNOWN -> ComfortResponseResult.Unknown(message?.trim().orEmpty().ifBlank { UNKNOWN_MESSAGE })
        else -> {
            flowLogger.log(DATA_MODULE, "Supabase 응답 파싱 실패", "reason=unknown_status")
            ComfortResponseResult.Unknown(UNKNOWN_MESSAGE)
        }
    }

    private fun errorMessage(statusCode: Int): String = when (statusCode) {
        429 -> "요청이 많아 답변이 늦어지고 있어요. 잠시 후 다시 시도해 주세요."
        else -> "위로 답변을 불러오지 못했어요. 잠시 후 다시 시도해 주세요."
    }

    private companion object {
        const val DATA_MODULE = "DATA"
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_INVALID = "INVALID"
        const val STATUS_UNKNOWN = "UNKNOWN"
        const val INVALID_MESSAGE = "고민이나 불안한 마음을 조금 더 구체적으로 작성해 주세요."
        const val UNKNOWN_MESSAGE = "지금은 내용을 안전하게 확인하지 못했어요. 잠시 후 다시 시도해 주세요."
    }
}
