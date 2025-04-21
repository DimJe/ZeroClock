package com.dimje.zeroclock.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface BaseUiState
interface BaseUiEvent
interface BaseUiEffect

abstract class BaseViewModel<STATE : BaseUiState, EVENT : BaseUiEvent, EFFECT : BaseUiEffect> : ViewModel() {

    private val initialState : STATE by lazy { createInitialState() }
    abstract fun createInitialState() : STATE


    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<STATE> = _uiState

    private val _effect = MutableSharedFlow<EFFECT>()
    val effect: SharedFlow<EFFECT> = _effect

    abstract fun onEvent(event: EVENT)

    protected fun setState(newState: STATE) {
        _uiState.value = newState
    }

    protected fun postEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}