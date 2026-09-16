package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiIntent
import androidx.compose.ui.geometry.Rect

sealed interface HomeUiIntent : BaseUiIntent {
    data object ToggleMenu : HomeUiIntent
    data class SelectMenu(val route: String) : HomeUiIntent
    data object AppResumed : HomeUiIntent
    data object Retry : HomeUiIntent
    data object NextGuide : HomeUiIntent
    data object SkipGuide : HomeUiIntent
    data object ConfirmReminderPermission : HomeUiIntent
    data object DismissReminderPermissionInfo : HomeUiIntent
    data class NotificationStatusChanged(
        val canNotify: Boolean,
        val canRequestRuntimePermission: Boolean,
    ) : HomeUiIntent
    data class NotificationPermissionResult(val granted: Boolean) : HomeUiIntent
    data class GuideTargetMeasured(val step: HomeGuideStep, val bounds: Rect) : HomeUiIntent
}
