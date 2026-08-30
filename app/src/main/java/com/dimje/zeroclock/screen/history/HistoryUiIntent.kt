package com.dimje.zeroclock.screen.history

import com.dimje.zeroclock.base.BaseUiIntent
import java.time.LocalDate

sealed interface HistoryUiIntent : BaseUiIntent {
    data object PreviousMonth : HistoryUiIntent
    data object NextMonth : HistoryUiIntent
    data class SelectDate(val date: LocalDate) : HistoryUiIntent
    data object Retry : HistoryUiIntent
    data object Back : HistoryUiIntent
}
