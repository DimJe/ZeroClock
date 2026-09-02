package com.dimje.data.mapper

import com.dimje.data.local.WorryEntity
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import java.time.LocalDate

fun WorryEntity.toDomain(): WorryEntry = WorryEntry(
    id = id,
    worry = worry,
    response = response,
    date = LocalDate.parse(localDate),
    createdAt = createdAt,
    riskLevel = riskLevel?.let { value ->
        runCatching { WorryRiskLevel.valueOf(value) }.getOrNull()
    },
)

fun toWorryEntity(
    worry: String,
    response: String,
    date: LocalDate,
    createdAt: Long,
    riskLevel: WorryRiskLevel,
): WorryEntity = WorryEntity(
    worry = worry,
    response = response,
    localDate = date.toString(),
    createdAt = createdAt,
    riskLevel = riskLevel.name,
)
