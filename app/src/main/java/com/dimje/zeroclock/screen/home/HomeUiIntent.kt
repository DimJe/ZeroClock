package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiIntent

sealed interface HomeUiIntent : BaseUiIntent {
    data object ToggleMenu : HomeUiIntent
    data class SelectMenu(val route: String) : HomeUiIntent
    data object Retry : HomeUiIntent
}
