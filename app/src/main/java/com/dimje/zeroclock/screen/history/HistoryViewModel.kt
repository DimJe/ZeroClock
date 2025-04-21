package com.dimje.zeroclock.screen.history

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor() :
    BaseViewModel<HistoryUiState, HistoryUiEvent, HistoryUiEffect>() {


    init {
        onEvent(HistoryUiEvent.LoadHistory)
    }

    override fun createInitialState(): HistoryUiState = HistoryUiState.Loading

    override fun onEvent(event: HistoryUiEvent) {
        when (event) {
            HistoryUiEvent.LoadHistory -> loadHistory()
            is HistoryUiEvent.OnItemClick -> {
                val json = URLEncoder.encode(Json.encodeToString(event.itemId), StandardCharsets.UTF_8.toString())
                postEffect(HistoryUiEffect.NavigateToDetail(json))
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            setState(HistoryUiState.Loading)
            delay(300)
            val history = listOf("이용 기록 A", "이용 기록 B", "이용 기록 C")
            setState(HistoryUiState.Success(history))
            postEffect(HistoryUiEffect.ShowToast("이용 기록을 불러왔어요"))
        }
    }
}