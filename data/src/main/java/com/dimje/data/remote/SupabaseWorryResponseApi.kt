package com.dimje.data.remote

import com.dimje.domain.logging.DataFlowLogger
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class WorryResponseApiException(
    message: String,
    cause: Throwable? = null,
) : IllegalStateException(message, cause)

class SupabaseWorryResponseApi(
    private val service: SupabaseWorryResponseService,
    private val flowLogger: DataFlowLogger,
) {
    suspend fun generate(worry: String): String {
        flowLogger.log(DATA_MODULE, "Supabase 요청 전송", "worryLength=${worry.length}")
        val apiResponse = try {
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

        val response = apiResponse.response?.trim()
        if (response.isNullOrEmpty()) {
            flowLogger.log(DATA_MODULE, "Supabase 응답 파싱 실패", "reason=empty_response")
            throw WorryResponseApiException("위로 답변을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.")
        }

        flowLogger.log(DATA_MODULE, "Supabase 답변 수신", "responseLength=${response.length}")
        return response
    }

    private fun errorMessage(statusCode: Int): String = when (statusCode) {
        429 -> "요청이 많아 답변이 늦어지고 있어요. 잠시 후 다시 시도해 주세요."
        else -> "위로 답변을 불러오지 못했어요. 잠시 후 다시 시도해 주세요."
    }

    private companion object {
        const val DATA_MODULE = "DATA"
    }
}
