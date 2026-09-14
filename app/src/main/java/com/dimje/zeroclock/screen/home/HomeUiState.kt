package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiState
import com.dimje.domain.model.WorryEntry
import androidx.compose.ui.geometry.Rect

data class HomeUiState(
    val isLoading: Boolean = true,
    val todayEntry: WorryEntry? = null,
    val monthlyWorryCount: Int = 0,
    val starSeed: Long = 0L,
    val isMenuExpanded: Boolean = false,
    val errorMessage: String? = null,
    val guideStep: HomeGuideStep? = null,
    val guideTarget: Rect? = null,
    val isSavingGuide: Boolean = false,
    val guideError: String? = null,
    val showReminderPermissionInfo: Boolean = false,
) : BaseUiState
