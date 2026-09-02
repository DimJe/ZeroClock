package com.dimje.data.remote.datasource

import com.dimje.domain.model.ComfortResponseResult

interface ComfortResponseRemoteDataSource {
    suspend fun generate(worry: String): ComfortResponseResult
}
