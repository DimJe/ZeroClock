package com.dimje.domain.model

import java.time.LocalDate

data class WorryEntry(
    val id: Long,
    val worry: String,
    val response: String,
    val date: LocalDate,
    val createdAt: Long,
)
