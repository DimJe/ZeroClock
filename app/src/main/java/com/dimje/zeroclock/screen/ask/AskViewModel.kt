package com.dimje.zeroclock.screen.ask

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskViewModel @Inject constructor() :
    BaseViewModel<AskUiState, AskUiEvent, AskUiEffect>() {


    init {
        onEvent(AskUiEvent.LoadQuestions)
    }

    override fun onEvent(event: AskUiEvent) {
        when (event) {
            AskUiEvent.LoadQuestions -> loadQuestions()
            is AskUiEvent.SubmitQuestion -> submitQuestion(event.question)
        }
    }

    override fun createInitialState(): AskUiState = AskUiState.Loading


    private fun loadQuestions() {
        viewModelScope.launch {
            setState(AskUiState.Loading)
            delay(400)
            setState(AskUiState.Success(listOf("질문1", "질문2")))
        }
    }

    private fun submitQuestion(question: String) {
        viewModelScope.launch {
            // 질문 제출 로직
            postEffect(AskUiEffect.ShowToast("질문이 제출되었습니다: $question"))
        }
    }
}