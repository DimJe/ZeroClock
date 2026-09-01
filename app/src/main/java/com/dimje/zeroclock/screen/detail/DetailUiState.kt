package com.dimje.zeroclock.screen.detail

import com.dimje.domain.model.WorryEntry
import com.dimje.zeroclock.base.BaseUiState
import java.time.LocalDate

data class DetailUiState(
    val isLoading: Boolean = true,
    val date: LocalDate? = null,
    val entry: WorryEntry? = null,
    val errorMessage: String? = null,
) : BaseUiState
