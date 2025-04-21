package com.dimje.zeroclock.screen.ask

import com.dimje.zeroclock.base.BaseUiEvent

sealed class AskUiEvent : BaseUiEvent {
    data object LoadQuestions : AskUiEvent()
    data class SubmitQuestion(val question: String) : AskUiEvent()
}