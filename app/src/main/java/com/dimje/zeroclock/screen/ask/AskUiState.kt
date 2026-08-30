package com.dimje.zeroclock.screen.ask

import com.dimje.zeroclock.base.BaseUiState
import com.dimje.domain.model.WorryEntry

data class AskUiState(
    val isLoading: Boolean = true,
    val worry: String = "",
    val savedEntry: WorryEntry? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) : BaseUiState {
    val canSubmit: Boolean
        get() = worry.isNotBlank() && !isSubmitting && savedEntry == null
}
