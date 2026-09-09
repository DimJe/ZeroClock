package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiState
import com.dimje.domain.model.WorryEntry

data class HomeUiState(
    val isLoading: Boolean = true,
    val todayEntry: WorryEntry? = null,
    val monthlyWorryCount: Int = 0,
    val starSeed: Long = 0L,
    val isMenuExpanded: Boolean = false,
    val errorMessage: String? = null,
) : BaseUiState
