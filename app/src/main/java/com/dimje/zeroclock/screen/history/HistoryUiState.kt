package com.dimje.zeroclock.screen.history

import com.dimje.zeroclock.base.BaseUiState
import com.dimje.domain.model.WorryEntry
import java.time.LocalDate
import java.time.YearMonth

data class HistoryUiState(
    val isLoading: Boolean = true,
    val visibleMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val entries: List<WorryEntry> = emptyList(),
    val errorMessage: String? = null,
) : BaseUiState {
    val selectedEntry: WorryEntry?
        get() = entries.firstOrNull { it.date == selectedDate }
}
