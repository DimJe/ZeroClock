package com.dimje.data.repository

import com.dimje.data.remote.datasource.ComfortResponseRemoteDataSource
import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.model.WorryRiskLevel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ComfortResponseRepositoryImplTest {
    @Test
    fun `원격 데이터소스의 위로 답변을 그대로 전달한다`() = runBlocking {
        val expected = ComfortResponseResult.Success(
            response = "오늘도 충분히 애썼어요.",
            riskLevel = WorryRiskLevel.NORMAL,
            isGenerated = true,
        )
        val remoteDataSource = RecordingComfortResponseRemoteDataSource(expected)
        val repository = ComfortResponseRepositoryImpl(remoteDataSource)

        val result = repository.generate("내일이 걱정돼요.")

        assertEquals(expected, result)
        assertEquals("내일이 걱정돼요.", remoteDataSource.receivedWorry)
    }

    private class RecordingComfortResponseRemoteDataSource(
        private val result: ComfortResponseResult,
    ) : ComfortResponseRemoteDataSource {
        var receivedWorry: String? = null

        override suspend fun generate(worry: String): ComfortResponseResult {
            receivedWorry = worry
            return result
        }
    }
}
