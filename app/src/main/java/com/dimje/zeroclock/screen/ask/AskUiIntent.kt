package com.dimje.zeroclock.screen.ask

import com.dimje.zeroclock.base.BaseUiIntent

sealed interface AskUiIntent : BaseUiIntent {
    data class WorryChanged(val worry: String) : AskUiIntent
    data object Submit : AskUiIntent
    data object Retry : AskUiIntent
    data object Back : AskUiIntent
}
