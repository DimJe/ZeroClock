package com.dimje.data.remote

import com.dimje.domain.logging.DataFlowLogger
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SupabaseWorryResponseApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: SupabaseWorryResponseApi
    private lateinit var flowLogger: RecordingDataFlowLogger

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        flowLogger = RecordingDataFlowLogger()
        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseWorryResponseService::class.java)
        api = SupabaseWorryResponseApi(service, flowLogger)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `고민을 POST JSON으로 전송하고 위로 답변을 반환한다`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"response":"오늘도 충분히 애썼어요."}"""),
        )

        val response = api.generate("내일 발표가 걱정돼요.")

        assertEquals("오늘도 충분히 애썼어요.", response)
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/functions/v1/generate-worry-response", request.path)
        assertTrue(request.getHeader("Content-Type").orEmpty().startsWith("application/json"))
        assertEquals(
            "내일 발표가 걱정돼요.",
            Json.parseToJsonElement(request.body.readUtf8()).jsonObject["worry"]?.jsonPrimitive?.content,
        )
        assertTrue(flowLogger.events.any { it.contains("Supabase 요청 전송") })
        assertTrue(flowLogger.events.any { it.contains("Supabase 답변 수신") })
    }

    @Test
    fun `서버 오류 응답이면 사용자용 예외를 반환한다`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(502)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"AI_SERVICE_ERROR"}"""),
        )

        val error = runCatching {
            runBlocking { api.generate("잠이 오지 않아요.") }
        }.exceptionOrNull()

        assertTrue(error is WorryResponseApiException)
        assertEquals("위로 답변을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.", error?.message)
        assertTrue(flowLogger.events.any { it.contains("statusCode=502") })
    }

    @Test
    fun `요청 제한 응답이면 잠시 후 다시 시도하라는 예외를 반환한다`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"RATE_LIMITED"}"""),
        )

        val error = runCatching {
            runBlocking { api.generate("오늘도 마음이 복잡해요.") }
        }.exceptionOrNull()

        assertTrue(error is WorryResponseApiException)
        assertEquals("요청이 많아 답변이 늦어지고 있어요. 잠시 후 다시 시도해 주세요.", error?.message)
        assertTrue(flowLogger.events.any { it.contains("statusCode=429") })
    }

    @Test
    fun `성공 응답에 response가 없으면 사용자용 예외를 반환한다`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"message":"empty"}"""),
        )

        val error = runCatching {
            runBlocking { api.generate("걱정이 많아요.") }
        }.exceptionOrNull()

        assertTrue(error is WorryResponseApiException)
        assertTrue(flowLogger.events.any { it.contains("empty_response") })
    }

    private class RecordingDataFlowLogger : DataFlowLogger {
        val events = mutableListOf<String>()

        override fun log(module: String, event: String, details: String) {
            events += "[$module][$event] $details"
        }
    }
}
