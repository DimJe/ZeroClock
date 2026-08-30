package com.dimje.zeroclock.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface BaseUiState
interface BaseUiIntent
interface BaseUiEffect

abstract class BaseViewModel<STATE : BaseUiState, INTENT : BaseUiIntent, EFFECT : BaseUiEffect>(
    initialState: STATE,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    private val _effect = Channel<EFFECT>(Channel.BUFFERED)
    val effect: Flow<EFFECT> = _effect.receiveAsFlow()

    abstract fun onIntent(intent: INTENT)

    protected fun reduce(transform: STATE.() -> STATE) {
        _uiState.update(transform)
    }

    protected fun postEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
