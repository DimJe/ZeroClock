package com.dimje.domain.time

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface DateProvider {
    fun today(): LocalDate

    fun observeDateChanges(): Flow<LocalDate>
}
