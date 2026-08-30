package com.dimje.domain.repository

interface ComfortResponseGenerator {
    suspend fun generate(worry: String): String
}
