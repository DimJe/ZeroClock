package com.dimje.zeroclock.testing

import com.dimje.domain.time.DateProvider
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDateProvider(initialDate: LocalDate) : DateProvider {
    private val date = MutableStateFlow(initialDate)

    override fun today(): LocalDate = date.value

    override fun observeDateChanges(): Flow<LocalDate> = date

    fun moveTo(newDate: LocalDate) {
        date.value = newDate
    }
}
