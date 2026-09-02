package com.dimje.zeroclock.testing

import com.dimje.domain.model.WorryEntry
import java.time.LocalDate

fun worryEntry(id: Long, date: LocalDate): WorryEntry = WorryEntry(
    id = id,
    worry = "테스트 고민 $id",
    response = "테스트 답변 $id",
    date = date,
    createdAt = id,
)
