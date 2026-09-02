package com.dimje.domain.repository

import com.dimje.domain.model.ComfortResponseResult

interface ComfortResponseGenerator {
    suspend fun generate(worry: String): ComfortResponseResult
}
