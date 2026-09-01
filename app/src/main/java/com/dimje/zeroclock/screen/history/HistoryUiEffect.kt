package com.dimje.zeroclock.screen.history

import com.dimje.zeroclock.base.BaseUiEffect
import java.time.LocalDate

sealed interface HistoryUiEffect : BaseUiEffect {
    data object NavigateBack : HistoryUiEffect
    data class NavigateToDetail(val date: LocalDate) : HistoryUiEffect
}
