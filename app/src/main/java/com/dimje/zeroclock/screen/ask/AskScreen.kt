package com.dimje.zeroclock.screen.ask

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.zeroclock.base.BaseScreenWrapper

@Composable
fun AskScreen(viewModel: AskViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val effect = viewModel.effect.collectAsState(initial = null)
    val context = LocalContext.current
    LaunchedEffect(effect.value) {
        effect.value?.let {
            when (it) {
                is AskUiEffect.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    Log.d("tag", "AskScreen: $state, $viewModel")
    BaseScreenWrapper(state = state, onRetry = { viewModel.onEvent(AskUiEvent.LoadQuestions) }) {
        val successState = state as AskUiState.Success
        Column {
            successState.questions.forEach { question ->
                Text(question)
            }
            Button(onClick = { viewModel.onEvent(AskUiEvent.SubmitQuestion("새로운 질문")) }) {
                Text("질문 제출")
            }
        }
    }
}